package com.kusanali.server;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

import static net.minecraft.block.Block.getDroppedStacks;

public class FloatDreamHander {
    private static final TrackedData<Long> R_COOLDOWN = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.LONG);
    private static final TrackedData<Long> G_COOLDOWN = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.LONG);

    private static void setCooldownTime(ServerPlayerEntity player, String cooldownKey, long cooldownEnd) {
        if ("r_ability_cooldown".equals(cooldownKey)) {
            player.getDataTracker().set(R_COOLDOWN, cooldownEnd);
        } else if ("g_ability_cooldown".equals(cooldownKey)) {
            player.getDataTracker().set(G_COOLDOWN, cooldownEnd);
        }
    }

    private static long getCooldownTime(ServerPlayerEntity player, String cooldownKey) {
        if ("r_ability_cooldown".equals(cooldownKey)) {
            return player.getDataTracker().get(R_COOLDOWN);
        } else if ("g_ability_cooldown".equals(cooldownKey)) {
            return player.getDataTracker().get(G_COOLDOWN);
        }
        return 0;
    }

    public static void register() {
        // 在服务器端初始化DataTracker
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                player.getDataTracker().startTracking(R_COOLDOWN, 0L);
                player.getDataTracker().startTracking(G_COOLDOWN, 0L);
            }
        });
        // 处理R键技能
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "activate_r_ability"),
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    if (validateAbilityUse(player)) return;

                    // 检查冷却
                    if (getCooldownTime(player, "r_ability_cooldown") > System.currentTimeMillis()) {
                        return;
                    }

                    // 执行R键技能逻辑
                    executeRAbility(player);
                }));

        // 处理G键技能
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "activate_g_ability"),
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    if (validateAbilityUse(player)) return;

                    // 检查冷却
                    if (getCooldownTime(player, "g_ability_cooldown") > System.currentTimeMillis()) {
                        return;
                    }

                    // 执行G键技能逻辑
                    executeGAbility(player);
                }));
    }

    private static boolean validateAbilityUse(PlayerEntity player) {
        // 检查是否手持float_dream
        ItemStack mainHand = player.getMainHandStack();
        return mainHand.getItem() != ModItems.FLOAT_DREAM;
    }

    private static void executeRAbility(ServerPlayerEntity player) {
        // 设置30秒冷却
        long cooldownEnd = System.currentTimeMillis() + 30000;
        setCooldownTime(player, "r_ability_cooldown", cooldownEnd);

        // 给玩家自身力量I效果，持续20秒
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20, 0));

        // 获取30格半径内的敌对生物
        Box area = new Box(player.getBlockPos()).expand(30);
        player.getWorld().getEntitiesByClass(
                net.minecraft.entity.LivingEntity.class, area,
                entity -> entity instanceof Monster && entity != player
        ).forEach(entity -> {
            // 施加发光效果（20秒）
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20, 0));
            // 施加自定义魔法伤害效果（20秒）
            entity.addStatusEffect(new StatusEffectInstance(ModEffects.MAGIC_DAMAGE, 20 * 20, 0));
        });

        // 同步冷却到客户端
        PacketByteBuf packetByteBuf1 = new PacketByteBuf(PacketByteBufs.create().writeLong(cooldownEnd));
        ServerPlayNetworking.send(player, new Identifier("kusanali", "r_cooldown_update"),
                packetByteBuf1);
    }

    private static void executeGAbility(ServerPlayerEntity player) {
        // 设置11秒冷却
        long cooldownEnd = System.currentTimeMillis() + 11000;
        setCooldownTime(player, "g_ability_cooldown", cooldownEnd);

        // 获取玩家朝向和位置
        Vec3d playerPos = player.getPos();
        Vec3d lookVec = player.getRotationVec(1.0F);

        // 创建前方7格的检测区域
        Vec3d areaEnd = playerPos.add(lookVec.multiply(7));
        Box detectionBox = new Box(playerPos, areaEnd).expand(3); // 扩展3格范围，形成更准确的检测区域

        // 获取检测区域内的所有生物实体
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                detectionBox,
                entity -> entity != player && entity.isAlive()
        );

        // 计算扇形检测的阈值（cos 30度）
        final double THRESHOLD = Math.cos(Math.toRadians(30));

        // 找出最近的实体
        LivingEntity target = entities.stream()
                .filter(entity -> {
                    Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                    return toEntity.dotProduct(lookVec) > THRESHOLD;
                })
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(playerPos)))
                .orElse(null);

        if (target != null) {
            // 对实体施加效果
            DamageSources sources = player.getWorld().getDamageSources();
            target.damage(sources.magic(), 6.0f);
            target.addStatusEffect(new StatusEffectInstance(ModEffects.TRIBBLE, 9 * 20, 0));
            target.addStatusEffect(new StatusEffectInstance(ModEffects.SEED_SIGN, 9 * 20, 0));
        } else {
            // 如果没有找到实体，检测范围内的花朵
            BlockPos.stream(detectionBox)
                    .filter(pos -> player.getWorld().getBlockState(pos).getBlock() instanceof FlowerBlock)
                    .forEach(pos -> {
                        // 收集花朵掉落物
                        List<ItemStack> drops = getDroppedStacks(
                                player.getWorld().getBlockState(pos),
                                (ServerWorld) player.getWorld(),
                                pos, null, player, player.getMainHandStack()
                        );

                        // 将掉落物添加到玩家背包
                        drops.forEach(player.getInventory()::insertStack);

                        // 移除花朵
                        player.getWorld().removeBlock(pos, false);
                    });
            cooldownEnd = System.currentTimeMillis() + 5000; // 原冷却时间-6秒
        }

        // 同步冷却到客户端
        PacketByteBuf packetByteBuf2 = new PacketByteBuf(PacketByteBufs.create().writeLong(cooldownEnd));
        ServerPlayNetworking.send(player, new Identifier("kusanali", "g_cooldown_update"),
                packetByteBuf2);
    }
}
