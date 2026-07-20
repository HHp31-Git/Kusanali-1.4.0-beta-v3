package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CyroSnowEvent {

    /** Cyro 持续时间（10 秒 = 200 tick） */
    private static final int CYRO_DURATION = 200;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                // 判断是否在细雪中
                if (isInPowderSnow(living)) {
                    // 在细雪中：持续给予 10 秒 Cyro
                    living.addStatusEffect(new StatusEffectInstance(
                            ModEffects.CYRO,
                            CYRO_DURATION,
                            0,
                            false,
                            true,
                            true
                    ));
                }
                // 离开细雪后不做任何操作，Cyro 自然衰减
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
