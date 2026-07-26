package com.kusanali.event.element_reaction;

import com.kusanali.entity.ModEntities;
import com.kusanali.entity.custom.DendroSeedEntity;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BoomEvent {

    /** 反应冷却时间（20 tick = 1 秒） */
    private static final int REACTION_COOLDOWN = 12;

    /** 记录实体上次触发反应的时间 */
    private static final Map<UUID, Long> LAST_REACTION_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME = new WeakHashMap<>();

    /** 上一 tick 是否拥有效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV = new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Map<UUID, Boolean> REACTED = new WeakHashMap<>();

    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    /** 生成种子概率：70% 生成 1 个，30% 生成 2 个 */
    private static final double SINGLE_SEED_CHANCE = 0.7;

    /** 种子生成半径 */
    private static final double SPAWN_RADIUS = 1.0;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {

            long now = world.getTime();

            /* ========== 更新获得时间 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.DENDRO, entity, now);
                track(uuid, ModEffects.HYDRO, entity, now);
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

                StatusEffectInstance dendro = entity.getStatusEffect(ModEffects.DENDRO);
                StatusEffectInstance hydro = entity.getStatusEffect(ModEffects.HYDRO);

                if (dendro == null || hydro == null) {
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

                boolean dendroInfinite = dendro.getDuration() == -1;
                boolean hydroInfinite = hydro.getDuration() == -1;

                /* ===== 清除有限效果 ===== */
                if (!dendroInfinite) {
                    entity.removeStatusEffect(ModEffects.DENDRO);
                    times.remove(ModEffects.DENDRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.DENDRO, false);
                }

                if (!hydroInfinite) {
                    entity.removeStatusEffect(ModEffects.HYDRO);
                    times.remove(ModEffects.HYDRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>()).put(ModEffects.HYDRO, false);
                }

                // 两个都无限时，只触发一次反应但不清除
                // 仍然触发反应，但不清除无限效果

                /* ===== 应用绽放反应 ===== */
                applyBloomReaction(world, entity);

                LAST_REACTION_TIME.put(uuid, now);
                REACTED.put(uuid, true);

                updatePrev(uuid, entity);
            }
        });
    }

    /**
     * 应用绽放反应：伤害 + 生成种子
     */
    private static void applyBloomReaction(ServerWorld world, LivingEntity entity) {
        UUID uuid = entity.getUuid();
        if (REENTERING.contains(uuid)) return;

        REENTERING.add(uuid);
        try {
            // 1. 造成伤害（类型为 reaction_type_2）
            entity.damage(ModDamageTypes.reaction_type_2(world), 1.0f);

            // 2. 生成种子
            spawnSeeds(world, entity);

            // 3. 粒子特效
            spawnBloomParticles(world, entity);

        } finally {
            world.getServer().execute(() -> REENTERING.remove(uuid));
        }
    }

    /**
     * 在生物脚底周围生成种子
     * 70% 概率生成 1 个，30% 概率生成 2 个
     */
    private static void spawnSeeds(ServerWorld world, LivingEntity entity) {
        Vec3d center = entity.getPos();

        // 确定生成数量
        int seedCount = 1;
        if (world.random.nextDouble() >= SINGLE_SEED_CHANCE) {
            seedCount = 2;
        }

        // 生成种子
        for (int i = 0; i < seedCount; i++) {
            // 在半径为 1 的圆上随机角度
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble() * SPAWN_RADIUS;

            double spawnX = center.x + Math.cos(angle) * radius;
            double spawnZ = center.z + Math.sin(angle) * radius;

            DendroSeedEntity seed = new DendroSeedEntity(
                    ModEntities.DENDRO_SEED,
                    world
            );
            seed.setPosition(spawnX, center.y, spawnZ);
            world.spawnEntity(seed);
        }
    }

    /**
     * 绽放反应粒子特效
     */
    private static void spawnBloomParticles(ServerWorld world, LivingEntity entity) {
        double cx = entity.getX();
        double cy = entity.getY() + entity.getHeight() * 0.5;
        double cz = entity.getZ();

        // 绿色草粒子爆发
        for (int i = 0; i < 20; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble() * 1.5;
            double height = (world.random.nextDouble() - 0.5);

            double px = cx + Math.cos(angle) * radius;
            double py = cy + height;
            double pz = cz + Math.sin(angle) * radius;

            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                    px, py, pz,
                    1,
                    Math.cos(angle) * 0.05,
                    world.random.nextDouble() * 0.05,
                    Math.sin(angle) * 0.05,
                    0.05
            );
        }

        // 水花飞溅
        for (int i = 0; i < 10; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble();

            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;

            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.FALLING_WATER,
                    px, cy + 0.2, pz,
                    1,
                    Math.cos(angle) * 0.03,
                    0.1,
                    Math.sin(angle) * 0.03,
                    0.02
            );
        }

        // 地面圆形扩散环
        for (int ring = 0; ring < 2; ring++) {
            double ringRadius = 0.5 + ring * 0.8;
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 / 8) * i;
                world.spawnParticles(
                        net.minecraft.particle.ParticleTypes.CLOUD,
                        cx + Math.cos(angle) * ringRadius,
                        cy - 0.3,
                        cz + Math.sin(angle) * ringRadius,
                        1,
                        0, 0.02, 0,
                        0.02
                );
            }
        }

        // 中心闪光
        world.spawnParticles(
                net.minecraft.particle.ParticleTypes.CRIT,
                cx, cy, cz,
                5,
                0.2, 0.2, 0.2,
                0.1
        );
    }

    /** 更新 PREV 映射 */
    private static void updatePrev(UUID uuid, LivingEntity entity) {
        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.DENDRO,
                        entity.getStatusEffect(ModEffects.DENDRO) != null);

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
}
