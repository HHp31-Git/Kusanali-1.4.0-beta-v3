package com.kusanali.event.element_reaction;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class CrystallizationEvent {
    /** 结晶冷却 */
    private static final int CRYSTALLIZE_COOLDOWN = 18;

    /** 效果首次获得时间 */
    private static final Map<UUID, Map<StatusEffect, Long>> APPLY_TIME = new WeakHashMap<>();

    /** 上一 tick 是否拥有 */
    private static final Map<UUID, Map<StatusEffect, Boolean>> PREV = new WeakHashMap<>();

    /** 已触发实体 */
    private static final Map<UUID, Boolean> REACTED = new WeakHashMap<>();

    /** 记录实体上次触发的时间 */
    private static final Map<UUID, Long> LAST_CRYSTALLIZE_TIME = new WeakHashMap<>();

    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    /** 可以与 Geo 结晶的元素 */
    private static final List<StatusEffect> CRYSTALLIZABLE_ELEMENTS = List.of(
            ModEffects.PYRO,
            ModEffects.HYDRO,
            ModEffects.ELECTRO,
            ModEffects.CYRO
    );

    /** 护盾等级映射：元素 -> 对应的心颜色等级 */
    private static final Map<StatusEffect, Integer> SHIELD_LEVEL_MAP = new HashMap<>();

    static {
        // 每个元素使用不同的等级来改变心的颜色
        // 等级 0 = 黄色（默认），1 = 蓝色，2 = 红色，3 = 紫色，4 = 青色
        SHIELD_LEVEL_MAP.put(ModEffects.PYRO, 2);    // 红色
        SHIELD_LEVEL_MAP.put(ModEffects.HYDRO, 1);   // 蓝色
        SHIELD_LEVEL_MAP.put(ModEffects.ELECTRO, 3); // 紫色
        SHIELD_LEVEL_MAP.put(ModEffects.CYRO, 4);    // 青色
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            long now = world.getTime();

            for (net.minecraft.entity.Entity ent : world.iterateEntities()) {
                if (!(ent instanceof LivingEntity entity)) continue;

                UUID uuid = entity.getUuid();

                // 1. 更新效果获得时间
                track(uuid, ModEffects.GEO, entity, now);
                for (StatusEffect element : CRYSTALLIZABLE_ELEMENTS) {
                    track(uuid, element, entity, now);
                }

                // 2. 检查冷却
                long lastCrystallize = LAST_CRYSTALLIZE_TIME.getOrDefault(uuid, 0L);
                if (now - lastCrystallize < CRYSTALLIZE_COOLDOWN) {
                    updatePrev(uuid, entity);
                    continue;
                }

                // 3. 检查是否拥有 Geo 效果
                StatusEffectInstance geo = entity.getStatusEffect(ModEffects.GEO);
                if (geo == null) {
                    REACTED.remove(uuid);
                    updatePrev(uuid, entity);
                    continue;
                }

                // 4. 查找可结晶的元素
                StatusEffect crystallizedElement = null;
                StatusEffectInstance elementInstance = null;
                for (StatusEffect element : CRYSTALLIZABLE_ELEMENTS) {
                    StatusEffectInstance instance = entity.getStatusEffect(element);
                    if (instance != null) {
                        crystallizedElement = element;
                        elementInstance = instance;
                        break;
                    }
                }

                if (crystallizedElement == null) {
                    REACTED.remove(uuid);
                    updatePrev(uuid, entity);
                    continue;
                }

                // 5. 防止重复触发
                if (REACTED.containsKey(uuid)) {
                    updatePrev(uuid, entity);
                    continue;
                }

                Map<StatusEffect, Long> times = APPLY_TIME.get(uuid);
                if (times == null) {
                    updatePrev(uuid, entity);
                    continue;
                }

                boolean geoInfinite = geo.getDuration() == -1;
                boolean elementInfinite = elementInstance.getDuration() == -1;

                /* ===== 清除 Geo 效果 ===== */
                if (!geoInfinite) {
                    entity.removeStatusEffect(ModEffects.GEO);
                    times.remove(ModEffects.GEO);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(ModEffects.GEO, false);
                }

                /* ===== 清除元素效果（不延长） ===== */
                if (!elementInfinite) {
                    entity.removeStatusEffect(crystallizedElement);
                    times.remove(crystallizedElement);
                    PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                            .put(crystallizedElement, false);
                }

                /* ===== 生成护盾 ===== */
                if (!REENTERING.contains(uuid)) {
                    REENTERING.add(uuid);
                    try {
                        applyShield(entity, crystallizedElement);
                    } finally {
                        world.getServer().execute(() -> REENTERING.remove(uuid));
                    }
                }

                /* ===== 结晶粒子特效 ===== */
                spawnCrystallizeParticles(world, entity);
                entity.damage(ModDamageTypes.reaction_type_3(world), 1.0f);

                LAST_CRYSTALLIZE_TIME.put(uuid, now);
                REACTED.put(uuid, true);

                updatePrev(uuid, entity);
            }
        });
    }

    /**
     * 应用护盾效果
     * 固定 1.5 颗心（30 点吸收量），根据元素显示不同颜色的心
     */
    private static void applyShield(LivingEntity entity, StatusEffect element) {
        // 获取对应元素的护盾等级（决定心的颜色）
        int shieldLevel = SHIELD_LEVEL_MAP.getOrDefault(element, 0);
        // 添加新护盾
        entity.addStatusEffect(new StatusEffectInstance(
                StatusEffects.ABSORPTION,
                300,
                shieldLevel,  // 等级决定心的颜色
                false,
                false,
                true
        ));
    }

    /**
     * 结晶粒子特效
     */
    private static void spawnCrystallizeParticles(ServerWorld world, LivingEntity entity) {
        Vec3d center = entity.getPos();
        double cx = center.x;
        double cy = center.y + entity.getHeight() * 0.5;
        double cz = center.z;

        // 六边形结晶环
        for (int i = 0; i < 6; i++) {
            double angle = (Math.PI * 2 / 6) * i;
            double radius = 1.2;

            double px = cx + Math.cos(angle) * radius;
            double py = cy + 0.3;
            double pz = cz + Math.sin(angle) * radius;

            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.CRIT,
                    px, py, pz,
                    3,
                    0.1, 0.1, 0.1,
                    0.05
            );
        }
        // 地面结晶环
        for (int ring = 0; ring < 2; ring++) {
            double ringRadius = 0.8 + ring * 0.6;
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
    }

    private static void updatePrev(UUID uuid, LivingEntity entity) {
        PREV.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(ModEffects.GEO,
                        entity.getStatusEffect(ModEffects.GEO) != null);

        for (StatusEffect element : CRYSTALLIZABLE_ELEMENTS) {
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
