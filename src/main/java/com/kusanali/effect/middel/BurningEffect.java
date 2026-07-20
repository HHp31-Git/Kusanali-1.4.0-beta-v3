package com.kusanali.effect.middel;

import com.kusanali.register.ModDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BurningEffect extends StatusEffect {
    public BurningEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF6347);
    }
    /** 伤害间隔（5 tick = 0.25 秒） */
    private static final int DAMAGE_INTERVAL = 5;

    /** 每次伤害值 */
    private static final float DAMAGE_PER_TICK = 0.5f;

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        // 检查是否在水中或雨中，如果是则清除效果
        if (isInWaterOrRain(entity)) {
            entity.removeStatusEffect(this);
            return;
        }

        var instance = entity.getStatusEffect(this);
        if (instance == null) return;

        int remainingDuration = instance.getDuration();
        int totalDuration = instance.getDuration() + remainingDuration;
        int elapsedTicks = totalDuration - remainingDuration;

        // 每 5 tick（0.25 秒）造成一次伤害
        if (elapsedTicks % DAMAGE_INTERVAL == 0) {
            float damage = DAMAGE_PER_TICK * (1 + amplifier);

            DamageSource src = ModDamageTypes.reaction_type_3(serverWorld);
            entity.damage(src, damage);
        }

        // 每 tick 生成火焰粒子
        spawnFlameParticles(entity, serverWorld);
    }

    /**
     * 检测实体是否在水中或雨中
     */
    private boolean isInWaterOrRain(LivingEntity entity) {
        // 在水中
        if (entity.isSubmergedInWater()) {
            return true;
        }

        // 在雨中（检测头顶是否有雨）
        World world = entity.getWorld();
        if (world.isRaining()) {
            var blockPos = entity.getBlockPos().up();
            if (!world.getBlockState(blockPos).isOpaque()) {
                var biome = world.getBiome(entity.getBlockPos());
                return biome.value().getPrecipitation(entity.getBlockPos()) == Biome.Precipitation.RAIN;
            }
        }

        return false;
    }

    /**
     * 生成火焰粒子
     */
    private void spawnFlameParticles(LivingEntity entity, ServerWorld world) {
        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        // 随机偏移，让火焰粒子分布在身体周围
        double offsetX = (entity.getRandom().nextDouble() - 0.5) * 0.6;
        double offsetY = (entity.getRandom().nextDouble() - 0.5) * 0.6;
        double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 0.6;

        world.spawnParticles(
                ParticleTypes.FLAME,
                x + offsetX,
                y + offsetY,
                z + offsetZ,
                1,      // 每次 1 个粒子
                0, 0, 0, // 无扩散
                0.02     // 轻微上升速度
        );

        // 30% 概率额外生成小火苗
        if (entity.getRandom().nextFloat() < 0.3f) {
            world.spawnParticles(
                    ParticleTypes.SMALL_FLAME,
                    x + (entity.getRandom().nextDouble() - 0.5) * 0.8,
                    y + (entity.getRandom().nextDouble() - 0.5) * 0.8,
                    z + (entity.getRandom().nextDouble() - 0.5) * 0.8,
                    1, 0, 0, 0,
                    0.01
            );
        }
    }
}
