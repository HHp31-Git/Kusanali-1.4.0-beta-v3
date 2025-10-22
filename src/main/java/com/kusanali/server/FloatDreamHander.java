package com.kusanali.server;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

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
        Vec3d areaCenter = playerPos.add(lookVec.multiply(7));

        // 创建检测区域
        Box detectionBox = new Box(areaCenter.add(-7, -7, -7), areaCenter.add(7, 7, 7));

        // 获取检测区域内的所有生物实体
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                detectionBox,
                entity -> entity != player && entity.isAlive()
        );

        // 筛选出在前方扇形区域内的实体，并按距离排序
        LivingEntity target = entities.stream()
                .filter(entity -> {
                    Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                    double dotProduct = toEntity.dotProduct(lookVec);
                    return dotProduct > 0.5; // 60度扇形区域
                })
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(playerPos)))
                .orElse(null);

        // 如果找到目标实体
        if (target != null) {
            // 施加发光效果（9秒）
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 9 * 20, 0));
            // 施加Tribble效果（9秒，使受到的伤害+2）
            target.addStatusEffect(new StatusEffectInstance(ModEffects.TRIBBLE, 9 * 20, 0));
        }

        // 同步冷却到客户端
        PacketByteBuf packetByteBuf2 = new PacketByteBuf(PacketByteBufs.create().writeLong(cooldownEnd));
        ServerPlayNetworking.send(player, new Identifier("kusanali", "g_cooldown_update"),
                packetByteBuf2);
    }
}
