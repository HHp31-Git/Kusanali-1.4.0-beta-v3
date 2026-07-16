package com.kusanali.event;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PyroEvent {
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;

                if (isInFire(living, world)) {
                    living.addStatusEffect(
                            new StatusEffectInstance(
                                    ModEffects.PYRO,
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
    private static boolean isInFire(LivingEntity entity, ServerWorld world) {
        // 1. 是否在岩浆中
        if (entity.isInLava()) {
            return true;
        }

        BlockPos pos = entity.getBlockPos();

        // 2. 是否站在火方块上
        if (world.getBlockState(pos).isOf(Blocks.FIRE)) {
            return true;
        }

        // 3. 是否头部在火方块中（防止踩火但脚不在）
        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());
        if (world.getBlockState(eyePos).isOf(Blocks.FIRE)) {
            return true;
        }

        // 4. 是否正在燃烧（火焰弹 / 火焰附加 / 着火）
        return entity.isOnFire();
    }
}
