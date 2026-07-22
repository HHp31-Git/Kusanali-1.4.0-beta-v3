package com.kusanali.event.element_reaction;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class IceBreakEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            // 只在服务端处理
            if (entity.getWorld().isClient()) return true;

            // 必须有 Freezing 效果
            if (!entity.hasStatusEffect(ModEffects.FREEZING)) return true;

            // 必须是物理伤害（使用已注册的 PHYSIC tag）
            if (!source.isIn(DamageTypeTagProvider.PHYSICAL)) return true;

            // 先清除 Freezing，防止重复触发
            entity.removeStatusEffect(ModEffects.FREEZING);

            spawnIceBreakParticles(entity);


            // 造成 4 点额外伤害（使用原伤害源）
            entity.damage(source, 4.0f);

            return true;
        });
    }
    private static void spawnIceBreakParticles(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        serverWorld.spawnParticles(
                ParticleTypes.ITEM_SNOWBALL,
                x, y, z,
                12,
                0.8,
                0.5,
                0.8,
                0.25
        );

        serverWorld.spawnParticles(
                ParticleTypes.WHITE_ASH,
                x, y, z,
                8,
                1.0,
                0.6,
                1.0,
                0.08
        );
        serverWorld.playSound(
                null,
                x, y, z,
                net.minecraft.sound.SoundEvents.BLOCK_GLASS_BREAK,
                net.minecraft.sound.SoundCategory.BLOCKS,
                1.0f,
                1.5f
        );
    }
}
