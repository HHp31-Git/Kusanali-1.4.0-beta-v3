package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DripstoneGeoEvent {

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                if (living.age % 20 != 0
                        && living.hasStatusEffect(ModEffects.GEO)) {
                    continue;
                }

                if (isHitByStalactite(living, world)) {
                    living.addStatusEffect(
                            new StatusEffectInstance(
                                    ModEffects.GEO,
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

    /** 判断是否被钟乳石击中 */
    private static boolean isHitByStalactite(LivingEntity entity, ServerWorld world) {
        BlockPos headPos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());

        // 头顶是滴水石锥
        if (world.getBlockState(headPos).isOf(Blocks.POINTED_DRIPSTONE)) {
            return true;
        }

        // 脚下是滴水石锥
        BlockPos footPos = entity.getBlockPos();
        return world.getBlockState(footPos).isOf(Blocks.POINTED_DRIPSTONE);
    }
}
