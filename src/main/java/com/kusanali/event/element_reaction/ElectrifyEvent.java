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

public class ElectrifyEvent {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 60;

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Map<UUID, Boolean> REACTED =
            new WeakHashMap<>();

    /** Electrified 持续时间 */
    private static final int ELECTRIFIED_DURATION = 200;

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

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();

            /* ========== 更新获得时间 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.ELECTRO, entity, now);
                track(uuid, ModEffects.HYDRO, entity, now);
            }

            /* ========== 结算反应 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 冷却检查：每 0.7 秒最多触发一次
                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl < SWIRL_COOLDOWN) {
                    continue; // 冷却中，跳过
                }

                // 已有 Electrified 不触发
                if (entity.hasStatusEffect(ModEffects.ELECTRIFY)) {
                    REACTED.remove(uuid);
                    continue;
                }

                StatusEffectInstance electro = entity.getStatusEffect(ModEffects.ELECTRO);
                StatusEffectInstance hydro = entity.getStatusEffect(ModEffects.HYDRO);

                if (electro == null || hydro == null) {
                    REACTED.remove(uuid);
                    continue;
                }
                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean electroInfinite = electro.getDuration() == -1;
                boolean hydroInfinite = hydro.getDuration() == -1;

                /* ===== 两者都无限 ===== */
                if (electroInfinite && hydroInfinite) {
                    // 保留两者，触发反应
                    applyElectrifiedReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== Electro 无限，Hydro 有限 ===== */
                if (electroInfinite) {
                    entity.removeStatusEffect(ModEffects.HYDRO);
                    times.remove(ModEffects.HYDRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.HYDRO, false);

                    applyElectrifiedReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== Hydro 无限，Electro 有限 ===== */
                if (hydroInfinite) {
                    // 清除 Electro，保留 Hydro
                    entity.removeStatusEffect(ModEffects.ELECTRO);
                    times.remove(ModEffects.ELECTRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.ELECTRO, false);

                    applyElectrifiedReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== 两者都有限（Electro 优先，清除 Hydro） ===== */
                entity.removeStatusEffect(ModEffects.HYDRO);
                times.remove(ModEffects.HYDRO);
                PREV.getOrDefault(uuid, Collections.emptyMap())
                        .put(ModEffects.HYDRO, false);

                applyElectrifiedReaction(entity, world);
                LAST_SWIRL_TIME.put(uuid, now);
                REACTED.put(uuid, true);
            }

            /* ========== 更新 PREV ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.ELECTRO,
                                entity.getStatusEffect(ModEffects.ELECTRO) != null);

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.HYDRO,
                                entity.getStatusEffect(ModEffects.HYDRO) != null);
            }
        });
    }

    private static void applyElectrifiedReaction(LivingEntity entity, ServerWorld world) {
        entity.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.ELECTRIFY,
                        ELECTRIFIED_DURATION,
                        0,
                        false,
                        true,
                        true
                )
        );
        double tx = entity.getX();
        double ty = entity.getY() + entity.getHeight() * 0.5;
        double tz = entity.getZ();
        entity.damage(ModDamageTypes.reaction_type_2(world), 1.0f);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, tx, ty, tz,
                3, 0.1, 0.1, 0.1, 0.05);
    }
}
