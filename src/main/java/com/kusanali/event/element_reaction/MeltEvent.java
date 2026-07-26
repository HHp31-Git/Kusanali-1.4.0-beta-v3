package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.*;

public class MeltEvent {

    /** 反应冷却时间（20 tick = 1 秒） */
    private static final int REACTION_COOLDOWN = 20;

    /** 记录实体上次触发反应的时间 */
    private static final Map<UUID, Long> LAST_REACTION_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME = new WeakHashMap<>();

    /** 上一 tick 是否拥有某效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV = new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Map<UUID, Boolean> REACTED = new WeakHashMap<>();

    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    /** 粒子数量配置 */
    private static final int FLAME_COUNT = 18;       // 火焰粒子数
    private static final int SNOWBALL_COUNT = 18;     // 冰晶粒子数

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {

            long now = world.getTime();

            /* ========== 更新获得时间 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.PYRO, entity, now);
                track(uuid, ModEffects.CYRO, entity, now);
            }

            /* ========== 结算反应 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 冷却检查
                long lastReaction = LAST_REACTION_TIME.getOrDefault(uuid, 0L);
                if (now - lastReaction < REACTION_COOLDOWN) {
                    updatePrev(uuid, entity);
                    continue;
                }

                StatusEffectInstance pyro = entity.getStatusEffect(ModEffects.PYRO);
                StatusEffectInstance cyro = entity.getStatusEffect(ModEffects.CYRO);

                if (pyro == null || cyro == null) {
                    REACTED.remove(uuid);
                    updatePrev(uuid, entity);
                    continue;
                }

                if (REACTED.containsKey(uuid)) {
                    updatePrev(uuid, entity);
                    continue;
                }

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) {
                    updatePrev(uuid, entity);
                    continue;
                }

                boolean pyroInfinite = pyro.getDuration() == -1;
                boolean cyroInfinite = cyro.getDuration() == -1;

                /* ===== 两者都无限 ===== */
                if (pyroInfinite && cyroInfinite) {
                    applyMeltReaction(world, entity, 2.0f, true, true);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    updatePrev(uuid, entity);
                    continue;
                }

                /* ===== 无限 vs 有限 ===== */
                if (pyroInfinite) {
                    // Pyro 无限，Cyro 有限 → 删除 Cyro
                    entity.removeStatusEffect(ModEffects.CYRO);
                    times.remove(ModEffects.CYRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.CYRO, false);

                    applyMeltReaction(world, entity, 2.0f, true, false);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    updatePrev(uuid, entity);
                    continue;
                }

                if (cyroInfinite) {
                    // Cyro 无限，Pyro 有限 → 删除 Pyro
                    entity.removeStatusEffect(ModEffects.PYRO);
                    times.remove(ModEffects.PYRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.PYRO, false);

                    applyMeltReaction(world, entity, 3.0f, false, true);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    updatePrev(uuid, entity);
                    continue;
                }

                /* ===== 有限 vs 有限 ===== */
                long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
                long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);

                boolean keepCyro = (pyroTime == cyroTime) || (pyroTime > cyroTime);
                StatusEffect earlierEffect = keepCyro ? ModEffects.CYRO : ModEffects.PYRO;

                entity.removeStatusEffect(earlierEffect);
                times.remove(earlierEffect);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(earlierEffect, false);

                float damage = keepCyro ? 2.0f : 3.0f;
                boolean keepPyro = !keepCyro;  // 如果 Pyro 后获得，保留 Pyro
                // 如果 Cyro 后获得，保留 Cyro

                applyMeltReaction(world, entity, damage, keepPyro, keepCyro);
                LAST_REACTION_TIME.put(uuid, now);
                REACTED.put(uuid, true);

                updatePrev(uuid, entity);
            }
        });
    }

    /**
     * 应用融化反应（伤害 + 粒子 + 音效）
     */
    private static void applyMeltReaction(ServerWorld world, LivingEntity entity,
                                          float damage, boolean keepPyro, boolean keepCyro) {
        // 1. 造成伤害（防递归）
        if (!REENTERING.contains(entity.getUuid())) {
            REENTERING.add(entity.getUuid());
            try {
                entity.damage(ModDamageTypes.reaction_type_1(world), damage);
            } finally {
                world.getServer().execute(() -> REENTERING.remove(entity.getUuid()));
            }
        }

        // 2. 生成粒子特效
        spawnMeltParticles(world, entity, keepPyro, keepCyro);

        // 3. 播放音效
        playMeltSound(world, entity, damage);
    }

    /**
     * 生成融化反应粒子特效
     */
    private static void spawnMeltParticles(ServerWorld world, LivingEntity entity,
                                           boolean keepPyro, boolean keepCyro) {
        double cx = entity.getX();
        double cy = entity.getY() + entity.getHeight() * 0.5;
        double cz = entity.getZ();

        /* ===== 火焰爆发 ===== */
        // 如果保留 Pyro 或两者都保留，火焰更强烈
        int flameCount = keepPyro ? FLAME_COUNT : FLAME_COUNT / 2;
        for (int i = 0; i < flameCount; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble() + 0.2;
            double heightOffset = (world.random.nextDouble() - 0.5) * 1.5;

            double px = cx + Math.cos(angle) * radius;
            double py = cy + heightOffset;
            double pz = cz + Math.sin(angle) * radius;

            // 火焰粒子
            world.spawnParticles(
                    ParticleTypes.FLAME,
                    px, py, pz,
                    1,
                    Math.cos(angle) * 0.05, 0.1, Math.sin(angle) * 0.05,
                    0.02
            );
        }

        /* ===== 冰晶破碎 ===== */
        // 如果保留 Cyro 或两者都保留，冰晶更密集
        int iceCount = keepCyro ? SNOWBALL_COUNT : SNOWBALL_COUNT / 2;
        for (int i = 0; i < iceCount; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble() * 0.8 + 0.2;
            double heightOffset = (world.random.nextDouble() - 0.5) * 1.2;

            double px = cx + Math.cos(angle) * radius;
            double py = cy + heightOffset;
            double pz = cz + Math.sin(angle) * radius;

            // 冰晶粒子（雪球碎屑）
            world.spawnParticles(
                    ParticleTypes.ITEM_SNOWBALL,
                    px, py, pz,
                    1,
                    Math.cos(angle) * 0.03, 0.05, Math.sin(angle) * 0.03,
                    0.04
            );
        }

        /* ===== 上升火苗（持续效果） ===== */
        for (int i = 0; i < 3; i++) {
            double px = cx + (world.random.nextDouble() - 0.5) * 0.4;
            double pz = cz + (world.random.nextDouble() - 0.5) * 0.4;

            world.spawnParticles(
                    ParticleTypes.SMALL_FLAME,
                    px, cy + 0.2, pz,
                    1,
                    0, 0.15, 0,
                    0.01
            );
        }

        /* ===== 冰霜残留（随机方向飞溅） ===== */
        for (int i = 0; i < 5; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double speed = 0.05 + world.random.nextDouble() * 0.1;

            double px = cx + Math.cos(angle) * 0.3;
            double py = cy + (world.random.nextDouble() - 0.5) * 0.5;
            double pz = cz + Math.sin(angle) * 0.3;

            world.spawnParticles(
                    ParticleTypes.SNOWFLAKE,
                    px, py, pz,
                    1,
                    Math.cos(angle) * speed,
                    world.random.nextDouble() * 0.1,
                    Math.sin(angle) * speed,
                    0.01
            );
        }
    }

    /**
     * 播放融化反应音效
     */
    private static void playMeltSound(ServerWorld world, LivingEntity entity, float damage) {
        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        // 高伤害（3.0）= 强烈融化 → 播放火声
        if (damage >= 3.0f) {
            world.playSound(
                    null, x, y, z,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS,
                    0.8f,
                    1.2f
            );
        } else {
            // 低伤害 → 播放气泡声（融化冒泡）
            world.playSound(
                    null, x, y, z,
                    SoundEvents.BLOCK_LAVA_EXTINGUISH,
                    SoundCategory.PLAYERS,
                    0.5f,
                    1.5f
            );
        }
    }

    /** 更新 PREV 映射 */
    private static void updatePrev(UUID uuid, LivingEntity entity) {
        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.PYRO,
                        entity.getStatusEffect(ModEffects.PYRO) != null);

        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.CYRO,
                        entity.getStatusEffect(ModEffects.CYRO) != null);
    }

    /** 记录首次获得时间 */
    private static void track(UUID uuid, StatusEffect effect,
                              LivingEntity entity, long now) {
        Map<StatusEffect, Boolean> prevMap = PREV.get(uuid);
        boolean had = prevMap != null && prevMap.getOrDefault(effect, false);
        boolean has = entity.getStatusEffect(effect) != null;

        if (has && !had) {
            APPLY_TIME
                    .computeIfAbsent(uuid, k -> new HashMap<>())
                    .put(effect, now);
        }
    }
}
