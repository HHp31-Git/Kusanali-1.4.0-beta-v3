package com.kusanali.effect.status_element;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class SuperconductivityEffect extends StatusEffect {
    private static final TagKey<DamageType> PHYSICAL = DamageTypeTagProvider.PHYSICAL;

    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    /** 物理易伤倍率（33%） */
    private static final float VULNERABILITY_MULTIPLIER = 0.33f;

    public SuperconductivityEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7FBFFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    /**
     * 注册伤害事件监听器
     * 在模组初始化时调用
     */
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            // 只在服务端处理
            if (entity.getWorld().isClient()) return true;

            // 检查是否有超导效果
            if (!entity.hasStatusEffect(ModEffects.SUPERCONDUCTIVITY)) {
                return true; // 允许原版伤害
            }

            // 检查是否为物理伤害
            if (!source.isIn(PHYSICAL)) {
                return true; // 非物理伤害，不处理
            }

            // 防重入
            UUID uuid = entity.getUuid();
            if (REENTERING.contains(uuid)) {
                return true;
            }

            REENTERING.add(uuid);
            try {
                // 额外易伤伤害：原始伤害的 33%
                float extraDamage = amount * VULNERABILITY_MULTIPLIER;

                if (extraDamage > 0.1f) {
                    // 使用 setHealth 避免递归
                    float newHealth = entity.getHealth() - extraDamage;
                    entity.setHealth(Math.max(newHealth, 0));

                    if (newHealth <= 0) {
                        entity.setHealth(0);
                        entity.onDeath(source);
                    }

                    // 粒子效果提示
                    if (entity.getWorld() instanceof ServerWorld serverWorld) {
                        spawnExtraDamageParticles(entity, serverWorld);
                    }
                }

                return true; // 允许原版伤害继续

            } finally {
                Objects.requireNonNull(entity.getServer()).execute(() -> REENTERING.remove(uuid));
            }
        });
    }

    /**
     * 额外伤害的粒子效果
     */
    private static void spawnExtraDamageParticles(LivingEntity entity, ServerWorld world) {
        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        // 紫色闪电粒子（表示超导易伤生效）
        for (int i = 0; i < 5; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * 0.6;
            double offsetY = (entity.getRandom().nextDouble() - 0.5) * 0.6;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 0.6;

            world.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1,
                    0, 0, 0,
                    0.05
            );
        }

        // 冰晶碎片
        for (int i = 0; i < 3; i++) {
            double angle = entity.getRandom().nextDouble() * Math.PI * 2;
            double radius = 0.3;

            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;

            world.spawnParticles(
                    ParticleTypes.SNOWFLAKE,
                    px, y + 0.3, pz,
                    1,
                    Math.cos(angle) * 0.03,
                    0.05,
                    Math.sin(angle) * 0.03,
                    0.02
            );
        }
    }
}
