package com.kusanali.event.element_reaction;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.*;

public class DiffusionEvent {

    /** 扩散冷却时间（14 tick = 0.7 秒） */
    private static final int SWIRL_COOLDOWN = 14;

    /** 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME = new WeakHashMap<>();

    /** 上一 tick 是否拥有 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV = new WeakHashMap<>();

    /** 已触发实体 */
    private static final Map<UUID, Boolean> REACTED = new WeakHashMap<>();

    /** 记录实体上次触发扩散的时间 */
    private static final Map<UUID, Long> LAST_SWIRL_TIME = new WeakHashMap<>();

    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    /** 可以被扩散的元素 */
    private static final List<StatusEffect> SWIRLABLE_ELEMENTS = List.of(
            ModEffects.CYRO,
            ModEffects.PYRO,
            ModEffects.HYDRO
    );

    /** 扩散半径 */
    private static final double RADIUS = 1.5;

    /** 延长时间（10 秒 = 200 tick） */
    private static final int EXTEND_DURATION = 200;

    /** 元素对应的粒子 */
    private static final Map<StatusEffect, ParticleEffect> ELEMENT_PARTICLES = Map.of(
            ModEffects.CYRO, ParticleTypes.ITEM_SNOWBALL,
            ModEffects.PYRO, ParticleTypes.FLAME,
            ModEffects.HYDRO, ParticleTypes.FALLING_WATER
    );

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();

            for (var ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;
                UUID uuid = entity.getUuid();

                // 1. 更新获得时间
                track(uuid, ModEffects.ANEMO, entity, now);
                for (StatusEffect element : SWIRLABLE_ELEMENTS) {
                    track(uuid, element, entity, now);
                }

                // 2. 结算反应
                long lastSwirl = LAST_SWIRL_TIME.getOrDefault(uuid, 0L);
                if (now - lastSwirl < SWIRL_COOLDOWN) {
                    updatePrev(uuid, entity);
                    continue;
                }

                StatusEffectInstance anemo = entity.getStatusEffect(ModEffects.ANEMO);
                if (anemo == null) {
                    REACTED.remove(uuid);
                    updatePrev(uuid, entity);
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

                boolean anemoInfinite = anemo.getDuration() == -1;
                boolean spreadInfinite = spreadInstance.getDuration() == -1;

                if (!anemoInfinite) {
                    entity.removeStatusEffect(ModEffects.ANEMO);
                    times.remove(ModEffects.ANEMO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(ModEffects.ANEMO, false);
                }

                /* ===== 处理扩散元素刷新 ===== */
                if (!spreadInfinite) {
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
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(spreadElement, true);
                }

                if (!REENTERING.contains(uuid)) {
                    REENTERING.add(uuid);
                    try {
                        entity.damage(world.getDamageSources().magic(), 1.0f);
                    } finally {
                        world.getServer().execute(() -> REENTERING.remove(uuid));
                    }
                }

                /* ===== 扩散粒子特效 ===== */
                spawnSwirlParticles(world, entity, spreadElement);

                /* ===== 扩散到附近 ===== */
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

                    // 被扩散的实体粒子
                    spawnTargetParticles(world, nearby, spreadElement);
                }

                LAST_SWIRL_TIME.put(uuid, now);
                REACTED.put(uuid, true);

                updatePrev(uuid, entity);
            }
        });
    }

    /**
     * 扩散源粒子特效
     * - 元素粒子爆发
     * - 风环扩散
     */
    private static void spawnSwirlParticles(ServerWorld world, LivingEntity entity, StatusEffect element) {
        double cx = entity.getX();
        double cy = entity.getY() + entity.getHeight() * 0.5;
        double cz = entity.getZ();

        ParticleEffect elementParticle = ELEMENT_PARTICLES.getOrDefault(element, ParticleTypes.HAPPY_VILLAGER);

        // 元素粒子爆发
        for (int i = 0; i < 20; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = world.random.nextDouble() * 0.8 + 0.2;
            double heightOffset = (world.random.nextDouble() - 0.5) * 1.5;

            double px = cx + Math.cos(angle) * radius;
            double py = cy + heightOffset;
            double pz = cz + Math.sin(angle) * radius;

            world.spawnParticles(
                    elementParticle,
                    px, py, pz,
                    1,
                    0, 0, 0,
                    0.05
            );
        }

        // 风环扩散
        for (int ring = 0; ring < 3; ring++) {
            double ringRadius = 0.5 + ring * 0.4;
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 / 8) * i + ring * 0.5;
                double px = cx + Math.cos(angle) * ringRadius;
                double pz = cz + Math.sin(angle) * ringRadius;

                world.spawnParticles(
                        ParticleTypes.CLOUD,
                        px, cy, pz,
                        1,
                        0, 0, 0,
                        0.03
                );
            }
        }
    }

    /**
     * 被扩散目标的粒子特效
     */

    private static void spawnTargetParticles(ServerWorld world, LivingEntity target, StatusEffect element) {
        double tx = target.getX();
        double ty = target.getY() + target.getHeight() * 0.5;
        double tz = target.getZ();

        ParticleEffect elementParticle = ELEMENT_PARTICLES.getOrDefault(element, ParticleTypes.HAPPY_VILLAGER);

        // 环绕目标的粒子
        for (int i = 0; i < 5; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = 0.3;

            double px = tx + Math.cos(angle) * radius;
            double pz = tz + Math.sin(angle) * radius;

            world.spawnParticles(
                    elementParticle,
                    px, ty, pz,
                    1,
                    0, 0, 0,
                    0.02
            );
        }

        // 目标身上的元素闪光
        world.spawnParticles(
                ParticleTypes.END_ROD,
                tx, ty, tz,
                3,
                0.2, 0.2, 0.2,
                0.01
        );
    }

    private static void updatePrev(UUID uuid, LivingEntity entity) {
        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.ANEMO,
                        entity.getStatusEffect(ModEffects.ANEMO) != null);

        for (StatusEffect element : SWIRLABLE_ELEMENTS) {
            PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                    .put(element,
                            entity.getStatusEffect(element) != null);
        }
    }

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
