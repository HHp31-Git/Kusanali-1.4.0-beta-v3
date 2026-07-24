package com.kusanali.effect.middel;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ElectrifyEffect extends StatusEffect {

    private static final Map<UUID, Long> LAST_INFECT_TIME = new HashMap<>();
    public ElectrifyEffect() {
        super(StatusEffectCategory.HARMFUL, 0X9400D3);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    private static final Map<UUID, Integer> LAST_DAMAGE_TICK = new WeakHashMap<>();
    private static final Map<UUID, Integer> LAST_PARTICLE_TICK = new WeakHashMap<>();
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        int currentTick = serverWorld.getServer().getTicks();
        UUID uuid = entity.getUuid();

        // 每 20 tick 造成一次伤害
        int lastDamage = LAST_DAMAGE_TICK.getOrDefault(uuid, 0);
        if (currentTick - lastDamage >= 20) {
            LAST_DAMAGE_TICK.put(uuid, currentTick);

            float damage = 1.0f + amplifier;
            entity.setHealth(entity.getHealth() - damage);
            if (entity.getHealth() <= 0) {
                entity.setHealth(0);
                entity.onDeath(serverWorld.getDamageSources().magic());
            }

            // 触发传染
            infectNearbyHydroEntities(entity, amplifier);
        }

        // 每 5 tick 生成粒子
        int lastParticle = LAST_PARTICLE_TICK.getOrDefault(uuid, 0);
        if (currentTick - lastParticle >= 5) {
            LAST_PARTICLE_TICK.put(uuid, currentTick);
            spawnArcParticles(entity);
        }
    }
    private void infectNearbyHydroEntities(LivingEntity source, int amplifier) {
        if (!(source.getWorld() instanceof ServerWorld serverWorld)) return;

        // 获取实体碰撞箱中心
        Box box = source.getBoundingBox();
        Vec3d center = box.getCenter();

        // 定义检测范围
        Box searchBox = new Box(
                center.x - 1.7,
                center.y - 1.7,
                center.z - 1.7,
                center.x + 1.7,
                center.y + 1.7,
                center.z + 1.7
        );

        long currentTick = serverWorld.getTime();

        // 查找范围内所有带有 Hydro 效果的生物
        for (LivingEntity target : serverWorld.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e != source
                        && e.isAlive()
                        && e.hasStatusEffect(ModEffects.HYDRO)
        )) {
            UUID targetUuid = target.getUuid();

            // 检查冷却时间
            long lastInfect = LAST_INFECT_TIME.getOrDefault(targetUuid, 0L);
            if (currentTick - lastInfect < 18) {
                continue; // 还在冷却中，跳过
            }

            // 更新冷却时间
            LAST_INFECT_TIME.put(targetUuid, currentTick);

            // 造成传染伤害
            float damage = 1.0f + amplifier;
            DamageSource source1 = ModDamageTypes.reaction_type_3(serverWorld);
            target.damage(source1, damage);

            // 传染粒子效果
            spawnArcParticles(target);
        }
    }

    private void spawnArcParticles(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        // 随机偏移，让粒子分布在身体周围
        double offsetX = (entity.getRandom().nextDouble() - 0.5) * 0.8;
        double offsetY = (entity.getRandom().nextDouble() - 0.5) * 0.8;
        double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 0.8;

        // 生成电弧粒子
        serverWorld.spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                x + offsetX,
                y + offsetY,
                z + offsetZ,
                1,
                0,
                0,
                0,
                0
        );

        // 偶尔生成额外的电弧粒子（30% 概率）
        if (entity.getRandom().nextFloat() < 0.3f) {
            serverWorld.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    x + (entity.getRandom().nextDouble() - 0.5),
                    y + (entity.getRandom().nextDouble() - 0.5),
                    z + (entity.getRandom().nextDouble() - 0.5),
                    1, 0, 0, 0, 0
            );
        }
    }
}
