package com.kusanali.event.element_reaction;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class IntensifyEvent {
    /** 冷却（18 tick = 0.9 秒） */
    private static final int COOLDOWN = 18;

    /** 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME = new WeakHashMap<>();

    /** 上一 tick 是否拥有 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV = new WeakHashMap<>();

    /** 已触发实体 */
    private static final Map<UUID, Boolean> REACTED = new WeakHashMap<>();

    /** 记录实体上次触发的时间 */
    private static final Map<UUID, Long> LAST_TIME = new WeakHashMap<>();

    /** 激化持续时间（200 tick = 10 秒） */
    private static final int INTENSIFY_DURATION = 300;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();

            /* ========== 第一阶段：更新获得时间 ========== */
            for (net.minecraft.entity.Entity ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.ELECTRO, entity, now);
                track(uuid, ModEffects.DENDRO, entity, now);
            }

            /* ========== 第二阶段：结算反应 ========== */
            for (net.minecraft.entity.Entity ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 冷却检查
                long lastTime = LAST_TIME.getOrDefault(uuid, 0L);
                if (now - lastTime < COOLDOWN) {
                    continue;
                }

                // 已有激化效果则不触发
                if (entity.hasStatusEffect(ModEffects.INTENSIFY)) {
                    REACTED.remove(uuid);
                    continue;
                }

                StatusEffectInstance electro = entity.getStatusEffect(ModEffects.ELECTRO);
                StatusEffectInstance dendro = entity.getStatusEffect(ModEffects.DENDRO);

                if (electro == null || dendro == null) {
                    REACTED.remove(uuid);
                    continue;
                }

                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean electroInfinite = electro.getDuration() == -1;
                boolean dendroInfinite = dendro.getDuration() == -1;

                /* ===== 处理效果清除 ===== */
                // 两者都无限：保留两者，触发反应
                if (electroInfinite && dendroInfinite) {
                    applyReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                // Electro 无限，Dendro 有限：清除 Dendro
                if (electroInfinite) {
                    entity.removeStatusEffect(ModEffects.DENDRO);
                    times.remove(ModEffects.DENDRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(ModEffects.DENDRO, false);

                    applyReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                // Dendro 无限，Electro 有限：清除 Electro
                if (dendroInfinite) {
                    entity.removeStatusEffect(ModEffects.ELECTRO);
                    times.remove(ModEffects.ELECTRO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(ModEffects.ELECTRO, false);

                    applyReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                // 两者都有限：清除两者
                entity.removeStatusEffect(ModEffects.DENDRO);
                times.remove(ModEffects.DENDRO);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.DENDRO, false);

                entity.removeStatusEffect(ModEffects.ELECTRO);
                times.remove(ModEffects.ELECTRO);
                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.ELECTRO, false);

                applyReaction(entity, world);
                REACTED.put(uuid, true);
            }

            /* ========== 第三阶段：更新 PREV ========== */
            for (net.minecraft.entity.Entity ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.ELECTRO,
                                entity.getStatusEffect(ModEffects.ELECTRO) != null);

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.DENDRO,
                                entity.getStatusEffect(ModEffects.DENDRO) != null);
            }
        });
    }

    /**
     * 应用激化反应
     */
    private static void applyReaction(LivingEntity entity, ServerWorld world) {
        // 赋予激化效果
        entity.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.INTENSIFY,
                        INTENSIFY_DURATION,
                        0,
                        false,
                        true,
                        true
                )
        );
        // 粒子特效
        Vec3d center = entity.getPos();
        double cx = center.x;
        double cy = center.y + entity.getHeight() * 0.5;
        double cz = center.z;

        // 电火花粒子
        world.spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                cx, cy, cz,
                8,
                0.3, 0.3, 0.3,
                0.08
        );

        // 绿色草粒子
        world.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                cx, cy, cz,
                6,
                0.2, 0.2, 0.2,
                0.06
        );

        // 中心闪光
        world.spawnParticles(
                ParticleTypes.CRIT,
                cx, cy, cz,
                4,
                0.1, 0.1, 0.1,
                0.1
        );

        // 更新最后触发时间
        LAST_TIME.put(entity.getUuid(), world.getTime());
    }

    /**
     * 记录首次获得时间
     */
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
