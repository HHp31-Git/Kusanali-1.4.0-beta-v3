package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class EvaporationEvent {

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

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {

            long now = world.getTime();

            /* ========== 单次遍历 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 1. 更新获得时间
                track(uuid, ModEffects.PYRO, entity, now);
                track(uuid, ModEffects.HYDRO, entity, now);

                // 2. 结算反应
                long lastReaction = LAST_REACTION_TIME.getOrDefault(uuid, 0L);
                if (now - lastReaction < REACTION_COOLDOWN) {
                    updatePrev(uuid, entity);
                    continue;
                }

                StatusEffectInstance pyro = entity.getStatusEffect(ModEffects.PYRO);
                StatusEffectInstance cyro = entity.getStatusEffect(ModEffects.HYDRO);

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
                    applyMeltDamage(entity, world, 2.0f);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    updatePrev(uuid, entity);
                    continue;
                }

                /* ===== 无限 vs 有限 ===== */
                if (pyroInfinite) {
                    entity.removeStatusEffect(ModEffects.HYDRO);
                    times.remove(ModEffects.HYDRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(ModEffects.HYDRO, false);

                    applyMeltDamage(entity, world, 2.0f);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    updatePrev(uuid, entity);
                    continue;
                }

                if (cyroInfinite) {
                    entity.removeStatusEffect(ModEffects.PYRO);
                    times.remove(ModEffects.PYRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(ModEffects.PYRO, false);

                    applyMeltDamage(entity, world, 3.0f);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    updatePrev(uuid, entity);
                    continue;
                }

                /* ===== 有限 vs 有限 ===== */
                long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
                long cyroTime = times.getOrDefault(ModEffects.HYDRO, 0L);

                // 同 tick 获得：Pyro 视为先获得
                boolean pyroLater = (pyroTime == cyroTime) || (pyroTime > cyroTime);
                StatusEffect earlierEffect = pyroLater ? ModEffects.HYDRO : ModEffects.PYRO;

                entity.removeStatusEffect(earlierEffect);
                times.remove(earlierEffect);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(earlierEffect, false);

                float damage = pyroLater ? 2.0f : 3.0f;
                applyMeltDamage(entity, world, damage);
                LAST_REACTION_TIME.put(uuid, now);
                REACTED.put(uuid, true);

                // 3. 更新 PREV
                updatePrev(uuid, entity);
            }
        });
    }

    /**
     * 应用融化伤害（防递归）
     */
    private static void applyMeltDamage(LivingEntity entity, ServerWorld world, float damage) {
        if (REENTERING.contains(entity.getUuid())) return;

        REENTERING.add(entity.getUuid());
        try {
            // 使用 entity.damage() 替代 setHealth，触发完整伤害流程
            entity.damage(ModDamageTypes.reaction_type_2(world), damage);

            // 生成粒子特效
            spawnMeltParticles(world, entity);
        } finally {
            world.getServer().execute(() -> REENTERING.remove(entity.getUuid()));
        }
    }

    /** 更新 PREV 映射 */
    private static void updatePrev(UUID uuid, LivingEntity entity) {
        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.PYRO,
                        entity.getStatusEffect(ModEffects.PYRO) != null);

        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.HYDRO,
                        entity.getStatusEffect(ModEffects.HYDRO) != null);
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
    private static void spawnMeltParticles(ServerWorld world, LivingEntity entity) {
        double cx = entity.getX();
        double cy = entity.getY() + entity.getHeight() * 0.5;
        double cz = entity.getZ();
        // 蒸汽爆发
        for (int i = 0; i < 10; i++) {
            double px = cx + (world.random.nextDouble() - 0.5) * 1.2;
            double py = cy + (world.random.nextDouble() - 0.5) * 0.8;
            double pz = cz + (world.random.nextDouble() - 0.5) * 1.2;

            world.spawnParticles(
                    ParticleTypes.POOF,
                    px, py, pz,
                    1,
                    0, 0.1, 0,
                    0.04
            );
        }
    }
}
