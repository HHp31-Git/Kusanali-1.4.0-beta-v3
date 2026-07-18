package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.WeakHashMap;

public class HydroEvent {

    private static final WeakHashMap<UUID, Integer> LAST_HYDRO_TICK =
            new WeakHashMap<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            int globalTick = (int) world.getTime();

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                UUID uuid = living.getUuid();
                int lastTick = LAST_HYDRO_TICK.getOrDefault(uuid, -20);

                // 性能优化
                if (globalTick - lastTick < 20) continue;

                if (isInWaterOrWaterlogged(living, world)
                        || isInRain(living, world)) {

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

                    LAST_HYDRO_TICK.put(uuid, globalTick);
                }
            }
        });
    }

    /**
     * 判断是否在符合环境
     */
    private static boolean isInWaterOrWaterlogged(LivingEntity entity, World world) {
        BlockPos pos = entity.getBlockPos();

        // 是否在水中（游泳、浸没）
        if (entity.isTouchingWater()) {
            return true;
        }

        // 是否在 Waterlogged 方块中（如台阶、楼梯、栅栏）
        if (world.getFluidState(pos).isOf(net.minecraft.fluid.Fluids.WATER)) {
            return true;
        }
        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());
        return world.getFluidState(eyePos).isOf(net.minecraft.fluid.Fluids.WATER);
    }

    /**
     * 判断是否在雨中（原版逻辑）
     */
    private static boolean isInRain(LivingEntity entity, World world) {
        if (!world.isRaining()) return false;

        BlockPos pos = entity.getBlockPos();
        if (!world.getBiome(pos).value().hasPrecipitation()) return false;

        return world.isSkyVisible(pos);
    }
}
