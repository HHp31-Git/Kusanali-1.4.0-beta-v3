package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class OverloadEvent {

    private static final int SWIRL_COOLDOWN = 20;

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 实体 -> 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME =
            new WeakHashMap<>();

    /** 上一 tick 是否拥有某效果 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV =
            new WeakHashMap<>();

    /** 已触发反应的实体 */
    private static final Map<UUID, Boolean> REACTED =
            new WeakHashMap<>();

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

                track(uuid, ModEffects.ELECTRO, entity, now);
                track(uuid, ModEffects.PYRO, entity, now);
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

                StatusEffectInstance electro = entity.getStatusEffect(ModEffects.ELECTRO);
                StatusEffectInstance pyro = entity.getStatusEffect(ModEffects.PYRO);

                if (electro == null || pyro == null) {
                    REACTED.remove(uuid);
                    continue;
                }
                if (REACTED.containsKey(uuid)) continue;

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) continue;

                boolean electroInfinite = electro.getDuration() == -1;
                boolean pyroInfinite = pyro.getDuration() == -1;
                /* ===== 无限处理 ===== */
                if (electroInfinite && !pyroInfinite) {
                    entity.removeStatusEffect(ModEffects.PYRO);
                        times.remove(ModEffects.PYRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.PYRO, false);
                    REACTED.put(uuid, true);
                    continue;
                }
                if (pyroInfinite && !electroInfinite) {
                    entity.removeStatusEffect(ModEffects.ELECTRO);
                    times.remove(ModEffects.ELECTRO);
                    PREV.getOrDefault(uuid, Collections.emptyMap())
                            .put(ModEffects.ELECTRO, false);
                    REACTED.put(uuid, true);
                    continue;
                }
                /* ===== 有限处理 ===== */
                long electroTime = times.getOrDefault(ModEffects.ELECTRO, 0L);
                long pyroTime = times.getOrDefault(ModEffects.PYRO, 0L);

                // 同 tick Pyro 视为先获得
                boolean pyroLater = (electroTime == pyroTime) || (electroTime < pyroTime);
                StatusEffect earlierEffect = pyroLater ? ModEffects.PYRO : ModEffects.ELECTRO;
                entity.removeStatusEffect(earlierEffect);
                times.remove(earlierEffect);
                PREV.getOrDefault(uuid, Collections.emptyMap())
                        .put(earlierEffect, false);

                // 伤害值固定为 2.5
                float damage = 2.5f;
                DamageSource src = ModDamageTypes.reaction_type_3(world);
                entity.damage(src, damage);
                Vec3d pos = entity.getPos();
                double x = pos.getX();
                double y = pos.getY() + 0.5;
                double z = pos.getZ();
                world.createExplosion(null, x, y, z,
                        0.5f, false, World.ExplosionSourceType.NONE);
                // 使用实体的朝向反方向作为击退方向
                float yaw = entity.getYaw();
                double rad = Math.toRadians(yaw);
                double dx = -Math.sin(rad);  // 面向的反方向
                double dz = Math.cos(rad);
                entity.takeKnockback(0.4, dx, dz);
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
                        .put(ModEffects.PYRO,
                                entity.getStatusEffect(ModEffects.PYRO) != null);
            }
        });
    }
}
