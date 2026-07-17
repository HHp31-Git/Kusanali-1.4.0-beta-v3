package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HydroEvent {

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                if (isInWaterOrWaterlogged(living, world)) {
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
            }
        });
    }

    /**
     * 判断是否在水中或 Waterlogged 方块中
     */
    private static boolean isInWaterOrWaterlogged(LivingEntity entity, World world) {
        BlockPos pos = entity.getBlockPos();

        // 1. 是否在水中（游泳、浸没）
        if (entity.isTouchingWater()) {
            return true;
        }

        // 2. 是否在 Waterlogged 方块中（如台阶、楼梯、栅栏）
        if (world.getFluidState(pos).isOf(net.minecraft.fluid.Fluids.WATER)) {
            return true;
        }

        // 3. 检查眼部高度（防止站在水里但脚不在）
        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());
        return world.getFluidState(eyePos).isOf(net.minecraft.fluid.Fluids.WATER);
    }
}
