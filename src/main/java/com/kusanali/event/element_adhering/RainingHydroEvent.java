package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.biome.Biome;

public class RainingHydroEvent {

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                // 判断是否在雨中
                if (isInRain(living)) {
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
                // 离开雨后不做任何操作，Hydro 自然衰减
            }
        });
    }

    /**
     * 判断实体是否在雨中
     * - 世界正在下雨
     * - 头顶无遮挡
     * - 所在群系为降雨（非下雪）
     */
    private static boolean isInRain(LivingEntity entity) {
        var world = entity.getWorld();

        // 世界必须在下雨
        if (!world.isRaining()) return false;

        // 头顶不能有遮挡
        var headPos = entity.getBlockPos().up();
        if (world.getBlockState(headPos).isOpaque()) return false;

        // 所在群系必须是降雨（不是下雪）
        var biome = world.getBiome(entity.getBlockPos());
        return biome.value().getPrecipitation(entity.getBlockPos()) == Biome.Precipitation.RAIN;
    }
}
