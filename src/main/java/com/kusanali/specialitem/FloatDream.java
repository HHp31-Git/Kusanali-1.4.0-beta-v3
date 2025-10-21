package com.kusanali.specialitem;

import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FloatDream extends Item {
    public FloatDream(Settings settings) {
        super(settings);
    }
    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_1")
                .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_2")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)));
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_3")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)));
        tooltip.add(Text.translatable("item.kusanali.float_dream.tooltip_4")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)));

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        // 检查冷却
        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            // 重击
            // 检测前方r=5半圆
            Vec3d playerPos = user.getPos();
            Vec3d lookVec = user.getRotationVec(1.0F);
            Vec3d areaCenter = playerPos.add(lookVec.multiply(5));
            Box detectionBox = new Box(areaCenter.add(-5, -5, -5), areaCenter.add(5, 5, 5));
            // 获取生物实体
            List<Entity> entitiesInArea = world.getOtherEntities(user, detectionBox,
                    entity -> entity instanceof LivingEntity && !entity.isSpectator());
            List<Entity> entitiesInSemiCircle = entitiesInArea.stream()
                    .filter(entity -> {
                        Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                        double dotProduct = toEntity.dotProduct(lookVec);
                        return dotProduct > 0;
                    })
                    .sorted(Comparator.comparingDouble(e -> e.squaredDistanceTo(playerPos)))
                    .toList();
            // 定位
            if (!entitiesInSemiCircle.isEmpty()) {
                Entity closestEntity = entitiesInSemiCircle.get(0);
                if (closestEntity instanceof LivingEntity) {
                    boolean wasOnFire = closestEntity.isOnFire();
                    float damageAmount = 10.0f;
                    if (wasOnFire) {
                        // 延长燃烧时间3秒
                        int currentFireTicks = closestEntity.getFireTicks();
                        closestEntity.setFireTicks(currentFireTicks + 60);
                        // 后续燃烧伤害+1
                        damageAmount += 1.0f;
                        // 提示效果触发
                        if (world instanceof ServerWorld) {
                            ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME,
                                    closestEntity.getX(), closestEntity.getY() + 1, closestEntity.getZ(),
                                    10, 0.5, 0.5, 0.5, 0.05);
                            world.playSound(null, closestEntity.getBlockPos(),
                                    SoundEvents.ENTITY_BLAZE_BURN, SoundCategory.NEUTRAL, 0.5f, 1.0f);
                        }
                    }
                    // 应用伤害
                    closestEntity.damage(world.getDamageSources().magic(), damageAmount);
                    // 棱长为1.5
                    double centerX = closestEntity.getX();
                    double centerY = closestEntity.getY() + closestEntity.getHeight() / 2;
                    double centerZ = closestEntity.getZ();
                    double halfSide = 0.75;
                    // 顶点
                    double[][] vertices = {
                            // 底面
                            {centerX - halfSide, centerY - halfSide, centerZ - halfSide},
                            {centerX + halfSide, centerY - halfSide, centerZ - halfSide},
                            {centerX + halfSide, centerY - halfSide, centerZ + halfSide},
                            {centerX - halfSide, centerY - halfSide, centerZ + halfSide},
                            // 顶面
                            {centerX - halfSide, centerY + halfSide, centerZ - halfSide},
                            {centerX + halfSide, centerY + halfSide, centerZ - halfSide},
                            {centerX + halfSide, centerY + halfSide, centerZ + halfSide},
                            {centerX - halfSide, centerY + halfSide, centerZ + halfSide}
                    };
                    // 棱边
                    int[][] edges = {
                            {0, 1}, {1, 2}, {2, 3}, {3, 0}, // 底面
                            {4, 5}, {5, 6}, {6, 7}, {7, 4}, // 顶面
                            {0, 4}, {1, 5}, {2, 6}, {3, 7}  // 高
                    };
                    // 生成粒子
                    ServerWorld serverWorld = (ServerWorld) world;
                    for (int[] edge : edges) {
                        double[] start = vertices[edge[0]];
                        double[] end = vertices[edge[1]];
                        int particlesPerEdge = 5;
                        for (int i = 0; i <= particlesPerEdge; i++) {
                            double ratio = (double) i / particlesPerEdge;
                            double particleX = start[0] + ratio * (end[0] - start[0]);
                            double particleY = start[1] + ratio * (end[1] - start[1]);
                            double particleZ = start[2] + ratio * (end[2] - start[2]);
                            serverWorld.spawnParticles(
                                    ParticleTypes.HAPPY_VILLAGER,
                                    particleX, particleY, particleZ,
                                    1,
                                    0, 0, 0,
                                    0
                            );
                        }
                    }
                    // 扣除耐久
                    stack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
                    // 冷却
                    player.getItemCooldownManager().set(this, 8);
                }
            }
        }
        return stack;
    }
    public static void registerAttackEvent() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof FloatDream) {
                // 检查冷却状态
                if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                    return ActionResult.PASS;
                }
                if (!world.isClient) {
                    // 攻击逻辑
                    performSpecialAttack(world, player);
                }
                // 冷却
                player.getItemCooldownManager().set(stack.getItem(), 3);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
    private static void performSpecialAttack(World world, PlayerEntity player) {
        // 踩键盘
        Vec3d playerPos = player.getPos();
        Vec3d lookVec = player.getRotationVec(1.0F).multiply(4);
        Vec3d[] squareCenters = new Vec3d[4];
        for (int i = 0; i < 4; i++) {
            double ratio = (i + 1) / 4.0;
            squareCenters[i] = playerPos.add(lookVec.multiply(ratio).add(0.2, 0.1, 0));
        }
        // 粒子生成
        for (Vec3d center : squareCenters) {
            spawnSquareParticles(world, center);
        }
        // 检测实体
        List<LivingEntity> affectedEntities = new ArrayList<>();
        for (Vec3d center : squareCenters) {
            Box detectionBox = new Box(
                    center.add(-0.5, -0.5, -0.5),
                    center.add(0.5, 0.5, 0.5)
            );
            List<LivingEntity> entitiesInBox = world.getEntitiesByClass(
                    LivingEntity.class, detectionBox, entity ->
                            entity != player && entity.isAlive()
            );
            affectedEntities.addAll(entitiesInBox);
        }
        // 造成伤害
        for (LivingEntity entity : affectedEntities) {
            boolean wasOnFire = entity.isOnFire();
            float damageAmount = 7.0f;
            if (wasOnFire) {
                // 延长燃烧时间3秒
                int currentFireTicks = entity.getFireTicks();
                entity.setFireTicks(currentFireTicks + 60);
                // 后续燃烧伤害+1
                damageAmount += 1.0f;
                // 提示效果触发
                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME,
                            entity.getX(), entity.getY() + 1, entity.getZ(),
                            10, 0.5, 0.5, 0.5, 0.05);
                        world.playSound(null, entity.getBlockPos(),
                            SoundEvents.ENTITY_BLAZE_BURN, SoundCategory.NEUTRAL, 0.5f, 1.0f);
                }
            }
            entity.damage(world.getDamageSources().magic(), damageAmount);
        }
    }
    private static void spawnSquareParticles(World world, Vec3d center) {
        double halfSide = 0.5;
        Vec3d[] corners = {
                new Vec3d(center.x - halfSide, center.y, center.z - halfSide),
                new Vec3d(center.x + halfSide, center.y, center.z - halfSide),
                new Vec3d(center.x + halfSide, center.y, center.z + halfSide),
                new Vec3d(center.x - halfSide, center.y, center.z + halfSide)
        };
        // 生成粒子
        if (world.isClient) {
            // 边
            for (int i = 0; i < 4; i++) {
                Vec3d start = corners[i];
                Vec3d end = corners[(i + 1) % 4];
                int particlesPerSide = 8;
                for (int j = 0; j <= particlesPerSide; j++) {
                    double ratio = (double) j / particlesPerSide;
                    double x = start.x + ratio * (end.x - start.x);
                    double z = start.z + ratio * (end.z - start.z);
                    world.addParticle(
                            ParticleTypes.HAPPY_VILLAGER,
                            x, center.y, z,
                            0, 0, 0
                    );
                }
            }
            // 中心
            world.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    center.x, center.y, center.z,
                    0, 0, 0
            );
        } else {
            ServerWorld serverWorld = (ServerWorld) world;
            for (int i = 0; i < 4; i++) {
                Vec3d start = corners[i];
                Vec3d end = corners[(i + 1) % 4];
                int particlesPerSide = 8;
                for (int j = 0; j <= particlesPerSide; j++) {
                    double ratio = (double) j / particlesPerSide;
                    double x = start.x + ratio * (end.x - start.x);
                    double z = start.z + ratio * (end.z - start.z);
                    serverWorld.spawnParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            x, center.y, z,
                            1, 0, 0, 0, 0
                    );
                }
            }
            // 中心粒子
            serverWorld.spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    center.x, center.y, center.z,
                    3, 0, 0, 0, 0
            );
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 普攻
        return super.postHit(stack, target, attacker);
    }


    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 14;
    }
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.getItem() == ModItems.ARANAS_FLOWER;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    @Override
    public int getEnchantability() {
        return 25;
    }
}
