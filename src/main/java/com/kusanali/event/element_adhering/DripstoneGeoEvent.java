package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DripstoneGeoEvent {

    public static void register() {
        // 改为监听钟乳石掉落伤害事件，比每 tick 查方块更准确
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getWorld().isClient()) return true;

            // 只有被掉落的钟乳石击中才挂 Geo
            if (!source.isOf(DamageTypes.FALLING_STALACTITE)) return true;

            entity.addStatusEffect(
                    new StatusEffectInstance(
                            ModEffects.GEO,
                            200,  // 10 秒
                            0,
                            false,
                            true,
                            true
                    )
            );

            return true;
        });
    }
}