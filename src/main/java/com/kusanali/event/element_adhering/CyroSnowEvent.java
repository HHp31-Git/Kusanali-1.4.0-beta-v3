package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.BlockPos;

public class CyroSnowEvent {

    /** 最小刷新间隔（40 tick = 2 秒），避免每 tick 刷新 */
    private static final int MIN_REFRESH_INTERVAL = 60;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            world.getTime();

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                // 不在细雪中则跳过
                if (!isInPowderSnow(living)) continue;

                // 已有 Cyro 且剩余时间充足，不刷新
                StatusEffectInstance existing = living.getStatusEffect(ModEffects.CYRO);
                if (existing != null && existing.getDuration() > MIN_REFRESH_INTERVAL) continue;

                // 在细雪中：持续给予 10 秒 Cyro
                living.addStatusEffect(new StatusEffectInstance(
                        ModEffects.CYRO,
                        200,
                        0,
                        false,
                        true,
                        true
                ));
            }
        });
    }

    /**
     * 判断实体是否在细雪方块中
     */
    private static boolean isInPowderSnow(LivingEntity entity) {
        BlockPos feetPos = entity.getBlockPos();
        return entity.getWorld().getBlockState(feetPos).isOf(Blocks.POWDER_SNOW);
    }
}
