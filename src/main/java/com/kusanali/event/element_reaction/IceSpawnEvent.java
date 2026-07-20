package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class IceSpawnEvent {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 70;

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

    /** Freezing 持续时间 */
    private static final int FREEZING_DURATION = 200;

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
            if (!(world instanceof ServerWorld)) return;
            long now = world.getTime();

            /* ========== 更新获得时间 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.CYRO, entity, now);
                track(uuid, ModEffects.HYDRO, entity, now);
            }

            /* ========== 结算反应 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 冷却检查
                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl < SWIRL_COOLDOWN) {
                    continue; // 冷却中，跳过
                }

                // 已有Freezing不触发
                if (entity.hasStatusEffect(ModEffects.FREEZING)) {
                    REACTED.remove(uuid);
                    continue;
                }

                StatusEffectInstance cyro = entity.getStatusEffect(ModEffects.CYRO);
                StatusEffectInstance hydro = entity.getStatusEffect(ModEffects.HYDRO);

                if (cyro == null || hydro == null) {
                    REACTED.remove(uuid);
                    continue;
                }
                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean cyroInfinite = cyro.getDuration() == -1;
                boolean hydroInfinite = hydro.getDuration() == -1;

                /* ===== 无限处理 ===== */
                if (cyroInfinite && !hydroInfinite) {
                    entity.removeStatusEffect(ModEffects.HYDRO);
                    times.remove(ModEffects.HYDRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.HYDRO, false);

                    applyFreezeReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                if (hydroInfinite && !cyroInfinite) {
                    entity.removeStatusEffect(ModEffects.CYRO);
                    times.remove(ModEffects.CYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.CYRO, false);

                    applyFreezeReaction(entity, world);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== 有限处理 ===== */
                long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);
                long hydroTime = times.getOrDefault(ModEffects.HYDRO, 0L);

                // 同 tick Cyro 视为先获得
                boolean cyroLater = (cyroTime == hydroTime) || (cyroTime > hydroTime);
                StatusEffect earlierEffect = cyroLater ? ModEffects.HYDRO : ModEffects.CYRO;

                entity.removeStatusEffect(earlierEffect);
                times.remove(earlierEffect);
                PREV.getOrDefault(uuid, Collections.emptyMap())
                        .put(earlierEffect, false);

                applyFreezeReaction(entity, world);
                LAST_SWIRL_TIME.put(uuid, now);
                REACTED.put(uuid, true);
            }

            /* ========== 更新 PREV ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.CYRO,
                                entity.getStatusEffect(ModEffects.CYRO) != null);

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.HYDRO,
                                entity.getStatusEffect(ModEffects.HYDRO) != null);
            }
        });
    }

    private static void applyFreezeReaction(LivingEntity entity, ServerWorld world) {
        entity.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.FREEZING,
                        FREEZING_DURATION,
                        0,
                        false,
                        true,
                        true
                )
        );
        entity.damage(ModDamageTypes.reaction_type_3(world), 1.0f);
    }
}
