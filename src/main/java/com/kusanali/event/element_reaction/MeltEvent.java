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

public class MeltEvent {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 20;

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有某效果（用于检测“刚获得”） */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发反应的实体（防止每 tick 重复触发） */
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

                // 冷却检查
                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl < SWIRL_COOLDOWN) {
                    continue; // 冷却中，跳过
                }

                StatusEffectInstance hydro = entity.getStatusEffect(ModEffects.PYRO);
                StatusEffectInstance pyro = entity.getStatusEffect(ModEffects.CYRO);

                if (hydro == null || pyro == null) {
                    REACTED.remove(uuid);
                    continue;
                }

                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean hydroInfinite = hydro.getDuration() == -1;
                boolean pyroInfinite = pyro.getDuration() == -1;

                /* ===== 清除非无限一方（立刻同步所有缓存） ===== */
                if (hydroInfinite && !pyroInfinite) {
                    entity.removeStatusEffect(ModEffects.PYRO);
                    times.remove(ModEffects.PYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap()).put(ModEffects.PYRO, false);
                    REACTED.put(uuid, true);
                    continue; // 本次 tick 不再结算
                }
                if (pyroInfinite && !hydroInfinite) {
                    entity.removeStatusEffect(ModEffects.CYRO);
                    times.remove(ModEffects.CYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap()).put(ModEffects.CYRO, false);
                    REACTED.put(uuid, true);
                    continue; // 本次 tick 不再结算
                }

                /* ===== 结算 ===== */
                long hydroTime = times.getOrDefault(ModEffects.PYRO, 0L);
                long pyroTime  = times.getOrDefault(ModEffects.CYRO, 0L);

                boolean hydroLater = (hydroTime == pyroTime) || (hydroTime > pyroTime);

                StatusEffect earlierEffect = hydroLater ? ModEffects.CYRO : ModEffects.PYRO;

                entity.removeStatusEffect(earlierEffect);
                times.remove(earlierEffect);
                PREV.getOrDefault(uuid, Collections.emptyMap()).put(earlierEffect, false);

                float damage = hydroLater ? 3.0f : 2.0f;
                DamageSource src = ModDamageTypes.reaction_type_1(world);
                entity.damage(src, damage);
                LAST_SWIRL_TIME.put(uuid, now);
                REACTED.put(uuid, true);
            }


            /* ========== 更新 ========== */
            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.CYRO,
                                entity.getStatusEffect(ModEffects.CYRO) != null);

                PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                        .put(ModEffects.PYRO,
                                entity.getStatusEffect(ModEffects.PYRO) != null);
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
