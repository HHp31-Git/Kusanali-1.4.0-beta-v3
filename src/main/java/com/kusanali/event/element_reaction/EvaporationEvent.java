package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class EvaporationEvent {

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有某效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Map<UUID, Boolean> REACTED =
            new WeakHashMap<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

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

                StatusEffectInstance pyro = entity.getStatusEffect(ModEffects.PYRO);
                StatusEffectInstance cyro = entity.getStatusEffect(ModEffects.CYRO);

                if (pyro == null || cyro == null) {
                    REACTED.remove(uuid);
                    continue;
                }

                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean pyroInfinite = pyro.getDuration() == -1;
                boolean cyroInfinite = cyro.getDuration() == -1;

                /* ===== 无限 vs 有限 ===== */
                if (pyroInfinite && !cyroInfinite) {
                    entity.removeStatusEffect(ModEffects.CYRO);
                    times.remove(ModEffects.CYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.CYRO, false);
                    REACTED.put(uuid, true);
                    continue;
                }
                if (cyroInfinite && !pyroInfinite) {
                    entity.removeStatusEffect(ModEffects.PYRO);
                    times.remove(ModEffects.PYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.PYRO, false);
                    REACTED.put(uuid, true);
                    continue;
                }

                /* ===== 有限 vs 有限 ===== */
                long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);
                long cyroTime = times.getOrDefault(ModEffects.CYRO, 0L);

                // 同 tick 获得：Pyro 视为先获得
                boolean pyroLater = (pyroTime == cyroTime) || (pyroTime > cyroTime);

                StatusEffectInstance earlier = pyroLater ? cyro : pyro;
                StatusEffect earlierEffect = pyroLater ? ModEffects.CYRO : ModEffects.PYRO;

                entity.removeStatusEffect(earlierEffect);
                times.remove(earlierEffect);
                PREV.getOrDefault(uuid, Collections.emptyMap())
                        .put(earlierEffect, false);

                float damage = pyroLater ? 2.0f : 3.0f;
                DamageSource src = ModDamageTypes.reaction_type_2(world);
                entity.damage(src, damage);

                REACTED.put(uuid, true);
            }

            /* ========== 更新 PREV ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.PYRO,
                                entity.getStatusEffect(ModEffects.PYRO) != null);

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.CYRO,
                                entity.getStatusEffect(ModEffects.CYRO) != null);
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
