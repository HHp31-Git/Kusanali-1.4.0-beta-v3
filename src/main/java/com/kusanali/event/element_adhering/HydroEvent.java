package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HydroEvent {

    /** 最小刷新间隔（60 tick = 3 秒） */
    private static final int REFRESH_INTERVAL = 50;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                // 不在水中/雨中则跳过
                if (!isInWaterOrWaterlogged(living, world) && !isInRain(living, world)) continue;

                // 已有 Hydro 且剩余时间充足，不刷新
                StatusEffectInstance existing = living.getStatusEffect(ModEffects.HYDRO);
                if (existing != null && existing.getDuration() > REFRESH_INTERVAL) continue;

                living.addStatusEffect(
                        new StatusEffectInstance(
                                ModEffects.HYDRO,
                                200,
                                0,
                                false,
                                true,
                                true
                        )
                );
            }
        });
    }

    /**
     * 判断是否在水中或 Waterlogged 方块中
     */
    private static boolean isInWaterOrWaterlogged(LivingEntity entity, World world) {
        // 是否在水中（游泳、浸没）
        if (entity.isTouchingWater()) return true;

        // 是否在 Waterlogged 方块中
        BlockPos pos = entity.getBlockPos();
        if (world.getFluidState(pos).isOf(net.minecraft.fluid.Fluids.WATER)) return true;

        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());
        return world.getFluidState(eyePos).isOf(net.minecraft.fluid.Fluids.WATER);
    }

    /**
     * 判断是否在雨中
     */
    private static boolean isInRain(LivingEntity entity, World world) {
        if (!world.isRaining()) return false;

        BlockPos pos = entity.getBlockPos();
        if (!world.getBiome(pos).value().hasPrecipitation()) return false;

        return world.isSkyVisible(pos);
    }
}
