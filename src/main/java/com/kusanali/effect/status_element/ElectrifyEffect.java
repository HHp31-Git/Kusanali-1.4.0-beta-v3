package com.kusanali.effect.status_element;

import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class ElectrifyEffect extends StatusEffect {

    private static final Map<UUID, Integer> LAST_DAMAGE_TICK = new WeakHashMap<>();
    private static final Map<UUID, Integer> LAST_PARTICLE_TICK = new WeakHashMap<>();
    private static final Map<UUID, Long> LAST_INFECT_TIME = new HashMap<>();

    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    public ElectrifyEffect() {
        super(StatusEffectCategory.HARMFUL, 0X9400D3);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // 只在服务端执行
        if (entity.getWorld().isClient()) return;
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        int currentTick = serverWorld.getServer().getTicks();
        UUID uuid = entity.getUuid();

        // 每 20 tick 造成一次伤害
        int lastDamage = LAST_DAMAGE_TICK.getOrDefault(uuid, 0);
        if (currentTick - lastDamage >= 20) {
            LAST_DAMAGE_TICK.put(uuid, currentTick);

            // 使用 damage + 防重入
            if (!REENTERING.contains(uuid)) {
                REENTERING.add(uuid);
                try {
                    float damage = 1.0f + amplifier;
                    entity.damage(serverWorld.getDamageSources().magic(), damage);
                } finally {
                    serverWorld.getServer().execute(() -> REENTERING.remove(uuid));
                }
            }

            // 触发传染
            infectNearbyHydroEntities(entity, amplifier, serverWorld);
        }

        // 每 5 tick 生成粒子
        int lastParticle = LAST_PARTICLE_TICK.getOrDefault(uuid, 0);
        if (currentTick - lastParticle >= 5) {
            LAST_PARTICLE_TICK.put(uuid, currentTick);
            spawnArcParticles(entity);
        }
    }

    private void infectNearbyHydroEntities(LivingEntity source, int amplifier, ServerWorld serverWorld) {
        // 获取实体碰撞箱中心
        Box box = source.getBoundingBox();
        Vec3d center = box.getCenter();

        // 定义检测范围
        Box searchBox = new Box(
                center.x - 1.7, center.y - 1.7, center.z - 1.7,
                center.x + 1.7, center.y + 1.7, center.z + 1.7
        );

        long currentTick = serverWorld.getTime();

        // 查找范围内所有带有 Hydro 效果的生物
        for (LivingEntity target : serverWorld.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e != source && e.isAlive() && e.hasStatusEffect(ModEffects.HYDRO)
        )) {
            UUID targetUuid = target.getUuid();

            // 检查冷却时间
            long lastInfect = LAST_INFECT_TIME.getOrDefault(targetUuid, 0L);
            if (currentTick - lastInfect < 18) continue;

            LAST_INFECT_TIME.put(targetUuid, currentTick);

            // 造成传染伤害（防递归）
            if (!REENTERING.contains(targetUuid)) {
                REENTERING.add(targetUuid);
                try {
                    float damage = 1.0f + amplifier;
                    target.damage(serverWorld.getDamageSources().magic(), damage);
                } finally {
                    serverWorld.getServer().execute(() -> REENTERING.remove(targetUuid));
                }
            }

            // 传染粒子效果
            spawnArcParticles(target);
        }
    }

    private void spawnArcParticles(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;

        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() * 0.5;
        double z = entity.getZ();

        double offsetX = (entity.getRandom().nextDouble() - 0.5) * 0.8;
        double offsetY = (entity.getRandom().nextDouble() - 0.5) * 0.8;
        double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 0.8;

        serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                x + offsetX, y + offsetY, z + offsetZ,
                1, 0, 0, 0, 0);

        if (entity.getRandom().nextFloat() < 0.3f) {
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                    x + (entity.getRandom().nextDouble() - 0.5),
                    y + (entity.getRandom().nextDouble() - 0.5),
                    z + (entity.getRandom().nextDouble() - 0.5),
                    1, 0, 0, 0, 0);
        }
    }
}
