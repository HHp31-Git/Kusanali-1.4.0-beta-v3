package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.*;

public class DiffusionEvent {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 14;

    /** 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发实体 */
    private static final Map<UUID, Boolean> REACTED =
            new WeakHashMap<>();

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 可以被扩散的元素 */
    private static final List<StatusEffect> SWIRLABLE_ELEMENTS = List.of(
            ModEffects.CYRO,
            ModEffects.PYRO,
            ModEffects.HYDRO
    );

    /** 扩散半径 */
    private static final double RADIUS = 1.5;

    /** 延长时间 */
    private static final int EXTEND_DURATION = 200;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            long now = world.getTime();

            /* ========== 更新获得时间 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                track(uuid, ModEffects.ANEMO, entity, now);
                for (StatusEffect element : SWIRLABLE_ELEMENTS) {
                    track(uuid, element, entity, now);
                }
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

                StatusEffectInstance anemo = entity.getStatusEffect(ModEffects.ANEMO);
                if (anemo == null) {
                    REACTED.remove(uuid);
                    continue;
                }
                StatusEffect spreadElement = null;
                StatusEffectInstance spreadInstance = null;
                for (StatusEffect element : SWIRLABLE_ELEMENTS) {
                    StatusEffectInstance instance = entity.getStatusEffect(element);
                    if (instance != null) {
                        spreadElement = element;
                        spreadInstance = instance;
                        break;
                    }
                }

                if (spreadElement == null) {
                    REACTED.remove(uuid);
                    continue;
                }

                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean shouldRemoveAnemo = spreadInstance.getDuration() != -1;
                if (anemo.getDuration() != -1) {
                    shouldRemoveAnemo = true;
                }

                if (shouldRemoveAnemo) {
                    entity.removeStatusEffect(ModEffects.ANEMO);
                    times.remove(ModEffects.ANEMO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.ANEMO, false);
                }

                /* ===== 处理 ===== */
                if (spreadInstance.getDuration() != -1) {
                    // 效果刷新
                    entity.removeStatusEffect(spreadElement);
                    entity.addStatusEffect(
                            new StatusEffectInstance(
                                    spreadElement,
                                    EXTEND_DURATION,
                                    0,
                                    false,
                                    true,
                                    true
                            )
                    );
                    times.put(spreadElement, now);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(spreadElement, true);
                }

                entity.damage(
                        ModDamageTypes.reaction_type_3(world),
                        1.0f
                );

                /* ===== 扩散 ===== */
                Box area = new Box(
                        entity.getX() - RADIUS,
                        entity.getY() - RADIUS,
                        entity.getZ() - RADIUS,
                        entity.getX() + RADIUS,
                        entity.getY() + RADIUS,
                        entity.getZ() + RADIUS
                );

                for (var nearby : world.getEntitiesByClass(
                        LivingEntity.class,
                        area,
                        e -> e != entity && e.isAlive()
                )) {
                    nearby.addStatusEffect(
                            new StatusEffectInstance(
                                    spreadElement,
                                    EXTEND_DURATION,
                                    0,
                                    false,
                                    true,
                                    true
                            )
                    );
                }
                LAST_SWIRL_TIME.put(uuid, now);
                REACTED.put(uuid, true);
            }

            /* ========== 更新 PREV ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.ANEMO,
                                entity.getStatusEffect(ModEffects.ANEMO) != null);

                for (StatusEffect element : SWIRLABLE_ELEMENTS) {
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(element,
                                    entity.getStatusEffect(element) != null);
                }
            }
        });
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
