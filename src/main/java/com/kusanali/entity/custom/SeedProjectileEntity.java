package com.kusanali.entity.custom;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class SeedProjectileEntity extends ProjectileEntity {

    /** 飞行阶段 */
    private enum Phase {
        /** 垂直上升阶段 */
        RISING,
        /** 抛物线追踪阶段 */
        HOMING,
        /** 直线上升消失阶段 */
        FLYING_UP
    }

    /** 上升高度（格） */
    private static final double RISE_HEIGHT = 1.0;

    /** 上升速度 */
    private static final double RISE_SPEED = 0.15;

    /** 追踪范围（格） */
    private static final double TRACK_RANGE = 8.0;

    /** 伤害值 */
    private static final float DAMAGE = 3.5f;

    /** 最大飞行距离 */
    private static final double MAX_RANGE = 32.0;

    /** 当前飞行阶段 */
    private Phase phase = Phase.RISING;

    /** 起始位置 */
    private Vec3d startPos;

    /** 追踪目标 */
    private LivingEntity target;

    /** 当前弧线进度 */
    private double arcProgress = 0;

    /** 防重入标记 */
    private boolean isProcessingHit = false;

    public SeedProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) return;

        // 初始化起始位置
        if (startPos == null) {
            startPos = this.getPos();
        }

        // 更新飞行距离
        double distanceTraveled = startPos.distanceTo(this.getPos());

        switch (phase) {
            case RISING -> tickRising();
            case HOMING -> tickHoming();
            case FLYING_UP -> tickFlyingUp();
        }

        // 超出最大距离则消失
        if (distanceTraveled > MAX_RANGE && !isProcessingHit) {
            this.discard();
        }
    }

    /**
     * 阶段一：垂直上升
     */
    private void tickRising() {
        // 向上飞行
        this.setVelocity(0, RISE_SPEED, 0);

        // 检查是否上升到目标高度
        if (this.getY() >= startPos.y + RISE_HEIGHT) {
            // 尝试寻找目标
            LivingEntity foundTarget = findNearestHostile();

            if (foundTarget != null && !foundTarget.isRemoved() && foundTarget.isAlive()) {
                // 找到目标 → 进入追踪阶段
                this.target = foundTarget;
                this.phase = Phase.HOMING;
                this.arcProgress = 0;
            } else {
                // 没找到目标 → 继续上升 10 格后消失
                this.phase = Phase.FLYING_UP;
            }
        }
    }

    /**
     * 阶段二：抛物线追踪目标
     */
    private void tickHoming() {
        if (target == null || !target.isAlive() || target.isRemoved()) {
            // 目标消失，转为上升消失
            phase = Phase.FLYING_UP;
            return;
        }

        // 计算当前位置到目标的方向
        Vec3d currentPos = this.getPos();
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.5, 0); // 瞄准目标身体中部
        Vec3d direction = targetPos.subtract(currentPos);

        double distance = direction.length();
        if (distance < 0.5) {
            // 到达目标，造成伤害
            hitTarget(target);
            return;
        }

        // 抛物线计算
        arcProgress += 0.05; // 进度递增速度
        double t = Math.min(arcProgress, 1.0);

        // 水平方向：向目标移动
        Vec3d horizontalDir = new Vec3d(direction.x, 0, direction.z).normalize();
        double horizontalSpeed = 0.25;

        // 垂直方向：抛物线
        double startY = startPos.y + RISE_HEIGHT;
        double endY = targetPos.y;
        double totalVerticalDist = endY - startY;
        // 抛物线公式：y = 4h*t*(1-t) + lerp(startY, endY, t)
        double arc = 4 * 0.8 * t * (1 - t); // 0.8 是弧线高度系数
        double verticalVelocity = (totalVerticalDist * 0.05) + (arc - getPreviousArc()) * 20;

        this.setVelocity(
                horizontalDir.x * horizontalSpeed,
                verticalVelocity,
                horizontalDir.z * horizontalSpeed
        );

        // 面向飞行方向
        double yaw = Math.atan2(horizontalDir.z, horizontalDir.x);
        this.setYaw((float) (yaw * 180 / Math.PI) - 90);
    }

    private double getPreviousArc() {
        double prevT = Math.max(arcProgress - 0.05, 0);
        return 4 * 0.8 * prevT * (1 - prevT);
    }

    /**
     * 阶段三：无目标时直线上升消失
     */
    private void tickFlyingUp() {
        this.setVelocity(0, RISE_SPEED * 0.8, 0);

        // 上升 10 格后消失
        if (this.getY() >= startPos.y + RISE_HEIGHT + 10.0) {
            this.discard();
        }
    }

    /**
     * 寻找 8 格内最近的敌对生物
     */
    private LivingEntity findNearestHostile() {
        Vec3d center = this.getPos();
        Box searchBox = new Box(
                center.x - TRACK_RANGE, center.y - 2, center.z - TRACK_RANGE,
                center.x + TRACK_RANGE, center.y + 4, center.z + TRACK_RANGE
        );

        List<LivingEntity> candidates = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e != this.getOwner()
                        && e.isAlive()
                        && !e.isRemoved()
                        && e instanceof Monster  // 只追踪敌对生物
                        && e.isAttackable()
        );

        // 找最近的非创造模式玩家或敌对生物
        return candidates.stream()
                .filter(e -> !(e instanceof net.minecraft.entity.player.PlayerEntity p) || !p.isCreative())
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(this)))
                .orElse(null);
    }

    /**
     * 命中目标造成伤害
     */
    private void hitTarget(@Nullable LivingEntity target) {
        if (isProcessingHit) return;
        isProcessingHit = true;

        // 提前检查目标有效性
        if (target == null || !target.isAlive()) {
            this.discard();
            return;
        }
        World world = this.getWorld();
        // 获取伤害来源
        Entity owner = this.getOwner();
        // 构建伤害
        DamageSource source;
        if (owner instanceof LivingEntity) {
            source = ModDamageTypes.reaction_type_2(world);
        } else {
            source = world.getDamageSources().thrown(this, owner);
        }

        target.damage(source, DAMAGE);
        target.addStatusEffect(new StatusEffectInstance(ModEffects.DENDRO, 200, 0));

        // 生成命中粒子
        spawnHitParticles(target);

        // 消失
        this.discard();
    }

    /**
     * 生成命中粒子特效
     */
    private void spawnHitParticles(LivingEntity target) {
        if (!(this.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld)) return;

        double x = target.getX();
        double y = target.getY() + target.getHeight() * 0.5;
        double z = target.getZ();

        // 绿色草种子粒子
        serverWorld.spawnParticles(
                net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                x, y, z,
                10,
                0.5, 0.5, 0.5,
                0.1
        );

        // 叶片碎片
        serverWorld.spawnParticles(
                net.minecraft.particle.ParticleTypes.CRIT,
                x, y, z,
                8,
                0.3, 0.3, 0.3,
                0.05
        );
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (isProcessingHit) return;
        isProcessingHit = true;

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity hit = entityHit.getEntity();

            if (hit instanceof LivingEntity livingHit && hit instanceof Monster) {
                hitTarget(livingHit);
                return;
            }
        }

        // 碰到方块或其他非敌对实体 → 直接消失
        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hit = entityHitResult.getEntity();
        if (hit instanceof LivingEntity livingHit && hit instanceof Monster) {
            hitTarget(livingHit);
        } else {
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // 碰到方块直接消失
        this.discard();
    }

    /* ======================== NBT 持久化 ======================== */

    @Override
    protected void initDataTracker() {
        // 弹射物不需要 DataTracker
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Phase")) {
            this.phase = Phase.valueOf(nbt.getString("Phase"));
        }
        if (nbt.contains("StartX") && nbt.contains("StartY") && nbt.contains("StartZ")) {
            this.startPos = new Vec3d(
                    nbt.getDouble("StartX"),
                    nbt.getDouble("StartY"),
                    nbt.getDouble("StartZ")
            );
        }
        if (nbt.contains("ArcProgress")) {
            this.arcProgress = nbt.getDouble("ArcProgress");
        }
        nbt.contains("TargetUUID");
    }


    @Override
    protected void writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt) {
        nbt.putString("Phase", phase.name());
        if (startPos != null) {
            nbt.putDouble("StartX", startPos.x);
            nbt.putDouble("StartY", startPos.y);
            nbt.putDouble("StartZ", startPos.z);
        }
        nbt.putDouble("ArcProgress", arcProgress);
        if (target != null) {
            nbt.putUuid("TargetUUID", target.getUuid());
        }
    }
}