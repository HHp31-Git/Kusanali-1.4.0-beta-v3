package com.kusanali.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DendroSeedEntity extends LivingEntity {

    /** 弹射物生成冷却（40 tick = 2 秒） */
    private static final int PROJECTILE_COOLDOWN = 40;

    /** 记录上次生成弹射物的时间 */
    private long lastProjectileTime = 0;

    /** 弹射物纹理变体（用于渲染器区分） */
    private String textureVariant = "default";

    public DendroSeedEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.noClip = true;
    }

    /* ======================== 无限血量 ======================== */

    @Override
    public void tick() {
        super.tick();
        this.setHealth(this.getMaxHealth());

        // 锁定位置
        this.setVelocity(0, 0, 0);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
    }


    /* ======================== 无法移动 ======================== */

    @Override
    public boolean isImmobile() { return true; }

    @Override
    public void travel(Vec3d movementInput) {}

    @Override
    public boolean isPushable() { return false; }

    @Override
    public Arm getMainArm() {
        return null;
    }

    @Override
    public boolean canMoveVoluntarily() { return false; }

    /* ======================== 伤害免疫 ======================== */

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }

    /* ======================== 纹理变体 ======================== */

    public void setTextureVariant(String variant) {
        this.textureVariant = variant;
    }

    public String getTextureVariant() {
        return textureVariant;
    }

    /* ======================== NBT 持久化 ======================== */

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("TextureVariant", textureVariant);
        nbt.putLong("LastProjectileTime", lastProjectileTime);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHealth(this.getMaxHealth());
        if (nbt.contains("TextureVariant")) {
            this.textureVariant = nbt.getString("TextureVariant");
        }
        if (nbt.contains("LastProjectileTime")) {
            this.lastProjectileTime = nbt.getLong("LastProjectileTime");
        }
    }
}
