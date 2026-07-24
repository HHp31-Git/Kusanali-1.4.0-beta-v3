package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.biome.Biome;

public class RainingHydroEvent {

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // 每 20 tick 检测一次
            if (world.getTime() % 20 != 0) return;

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                // 不在雨中则跳过
                if (!isInRain(living)) continue;

                // 已有 Hydro 且剩余时间充足，不刷新
                StatusEffectInstance existing = living.getStatusEffect(ModEffects.HYDRO);
                if (existing != null && existing.getDuration() > 170) continue;

                // 在雨中：持续给予 10 秒 Hydro
                living.addStatusEffect(new StatusEffectInstance(
                        ModEffects.HYDRO,
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
     * 判断实体是否在雨中
     */
    private static boolean isInRain(LivingEntity entity) {
        var world = entity.getWorld();

        if (!world.isRaining()) return false;

        var headPos = entity.getBlockPos().up();
        if (world.getBlockState(headPos).isOpaque()) return false;

        var biome = world.getBiome(entity.getBlockPos());
        return biome.value().getPrecipitation(entity.getBlockPos()) == Biome.Precipitation.RAIN;
    }
}
