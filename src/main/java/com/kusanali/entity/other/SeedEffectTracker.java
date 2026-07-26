package com.kusanali.entity.other;

import com.kusanali.entity.custom.DendroSeedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 为 DendroSeedEntity 提供状态效果跟踪
 * 因为 Entity 本身不能拥有状态效果
 */
public class SeedEffectTracker {

    /** 种子 UUID -> 效果 -> 剩余 tick */
    private static final Map<UUID, Map<StatusEffect, Integer>> EFFECTS = new HashMap<>();

    /** 种子 UUID -> 上一次更新时间 */
    private static final Map<UUID, Long> LAST_TICK = new HashMap<>();

    /**
     * 给种子添加效果
     */
    public static void addEffect(UUID seedUuid, StatusEffect effect, int durationTicks) {
        EFFECTS.computeIfAbsent(seedUuid, k -> new HashMap<>()).put(effect, durationTicks);
    }

    /**
     * 检查种子是否拥有某效果
     */
    public static boolean hasEffect(UUID seedUuid, StatusEffect effect) {
        Map<StatusEffect, Integer> effects = EFFECTS.get(seedUuid);
        if (effects == null) return false;
        Integer duration = effects.get(effect);
        return duration != null && duration > 0;
    }

    /**
     * 每 tick 更新所有种子的效果持续时间
     */
    public static void tickAll(ServerWorld world) {
        long now = world.getTime();

        // 遍历所有种子实体
        for (Entity entity : world.getEntitiesByClass(
                Entity.class,
                new Box(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE,
                        Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
                e -> e instanceof DendroSeedEntity
        )) {
            UUID uuid = entity.getUuid();

            // 清理已消失实体的数据
            if (entity.isRemoved()) {
                EFFECTS.remove(uuid);
                LAST_TICK.remove(uuid);
                continue;
            }

            // 每秒（20 tick）扣减持续时间
            Long lastTick = LAST_TICK.get(uuid);
            if (lastTick == null || now - lastTick >= 20) {
                LAST_TICK.put(uuid, now);

                Map<StatusEffect, Integer> effects = EFFECTS.get(uuid);
                if (effects != null) {
                    // 复制 keySet 避免并发修改
                    for (StatusEffect effect : new HashMap<>(effects).keySet()) {
                        int remaining = effects.get(effect) - 20; // 每秒减 20 tick
                        if (remaining <= 0) {
                            effects.remove(effect);
                        } else {
                            effects.put(effect, remaining);
                        }
                    }
                    if (effects.isEmpty()) {
                        EFFECTS.remove(uuid);
                    }
                }
            }
        }
    }

    /**
     * 清理已消失种子的数据（在实体消失时调用）
     */
    public static void removeSeed(UUID seedUuid) {
        EFFECTS.remove(seedUuid);
        LAST_TICK.remove(seedUuid);
    }
}