package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class DendroBurnEvent {

    /** 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发实体 */
    private static final Map<UUID, Boolean> REACTED =
            new WeakHashMap<>();

    /** 反应冷却时间 */
    private static final int REACTION_COOLDOWN = 46;

    /** 记录实体上次触发反应的时间 */
    private static final Map<UUID, Long> LAST_REACTION_TIME = new WeakHashMap<>();

    /** Burning 持续时间（3 秒 = 60 tick） */
    private static final int BURNING_DURATION = 60;

    /** 反应伤害 */
    private static final float REACTION_DAMAGE = 1.0f;

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

                track(uuid, ModEffects.DENDRO, entity, now);
                track(uuid, ModEffects.PYRO, entity, now);
            }

            /* ========== 结算反应 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 冷却检查：每 2.3 秒最多触发一次
                long lastReaction = LAST_REACTION_TIME.getOrDefault(uuid, 0L);
                if (now - lastReaction < REACTION_COOLDOWN) {
                    continue;
                }

                StatusEffectInstance dendro = entity.getStatusEffect(ModEffects.DENDRO);
                StatusEffectInstance cyro = entity.getStatusEffect(ModEffects.PYRO);

                if (dendro == null || cyro == null) {
                    REACTED.remove(uuid);
                    continue;
                }
                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean dendroInfinite = dendro.getDuration() == -1;
                boolean cyroInfinite = cyro.getDuration() == -1;

                /* ===== 两者都无限 ===== */
                if (dendroInfinite && cyroInfinite) {
                    applyReaction(entity, world);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== 无限处理 ===== */
                if (dendroInfinite) {
                    entity.removeStatusEffect(ModEffects.PYRO);
                    times.remove(ModEffects.PYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.PYRO, false);

                    applyReaction(entity, world);
                    LAST_REACTION_TIME.put(uuid, now);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== 有限处理 ===== */
                entity.removeStatusEffect(ModEffects.DENDRO);
                times.remove(ModEffects.DENDRO);
                PREV.getOrDefault(uuid, Collections.emptyMap())
                        .put(ModEffects.DENDRO, false);

                applyReaction(entity, world);
                LAST_REACTION_TIME.put(uuid, now);
                REACTED.put(uuid, true);
            }

            /* ========== 更新 PREV ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.DENDRO,
                                entity.getStatusEffect(ModEffects.DENDRO) != null);

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.PYRO,
                                entity.getStatusEffect(ModEffects.PYRO) != null);
            }
        });
    }

    /** 应用反应 */
    private static void applyReaction(LivingEntity entity, ServerWorld world) {
        entity.damage(ModDamageTypes.reaction_type_3(world), REACTION_DAMAGE);
        entity.addStatusEffect(new StatusEffectInstance(
                ModEffects.BURNING,
                BURNING_DURATION,
                0,
                false,
                true,
                true
        ));
    }
}
