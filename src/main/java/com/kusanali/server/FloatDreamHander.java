package com.kusanali.server;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.block.FlowerBlock;

import java.util.List;

public class FloatDreamHander {
    public static void register() {
        // 处理R键技能
        ServerPlayNetworking.registerGlobalReceiver(Identifier.of("kusanali", "activate_r_ability"),
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    if (validateAbilityUse(player)) return;

                    // 检查冷却
                    if (player.writeNbt(new NbtCompound()).getLong("r_ability_cooldown") > System.currentTimeMillis()) {
                        return;
                    }

                    // 执行R键技能逻辑
                    executeRAbility(player);
                }));

        // 处理G键技能
        ServerPlayNetworking.registerGlobalReceiver(Identifier.of("kusanali", "activate_g_ability"),
                (server, player, handler, buf, responseSender) -> server.execute(() -> {
                    if (validateAbilityUse(player)) return;

                    // 检查冷却
                    if (player.writeNbt(new NbtCompound()).getLong("g_ability_cooldown") > System.currentTimeMillis()) {
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
        player.writeNbt(new NbtCompound()).putLong("r_ability_cooldown", cooldownEnd);

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
        ServerPlayNetworking.send(player, Identifier.of("kusanali", "r_cooldown_update"),
                packetByteBuf1);
    }

    private static void executeGAbility(ServerPlayerEntity player) {
        // 设置11秒冷却
        long cooldownEnd = System.currentTimeMillis() + 11000;
        player.writeNbt(new NbtCompound()).putLong("g_ability_cooldown", cooldownEnd);

        // 射线检测：7格距离，忽略流体
        HitResult hit = player.raycast(7.0, 0.0f, false);

        if (hit.getType() == HitResult.Type.ENTITY) {
            // 优先处理生物
            EntityHitResult entityHit = (EntityHitResult) hit;
            if (entityHit.getEntity() instanceof net.minecraft.entity.LivingEntity target) {

                // 施加发光效果（9秒）
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 9 * 20, 0));
                // 施加Tribble效果（9秒，使受到的伤害+2）
                target.addStatusEffect(new StatusEffectInstance(ModEffects.TRIBBLE, 9 * 20, 0));
            }
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            // 处理花朵
            BlockHitResult blockHit = (BlockHitResult) hit;
            if (player.getWorld().getBlockState(blockHit.getBlockPos()).getBlock() instanceof FlowerBlock) {
                // 破坏花并收集掉落物
                List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(
                        player.getWorld().getBlockState(blockHit.getBlockPos()),
                        (net.minecraft.server.world.ServerWorld) player.getWorld(),
                        blockHit.getBlockPos(), null, player, ItemStack.EMPTY);

                for (ItemStack stack : drops) {
                    player.getInventory().insertStack(stack);
                }

                player.getWorld().removeBlock(blockHit.getBlockPos(), false);
            }
        }

        // 同步冷却到客户端
        PacketByteBuf packetByteBuf2 = new PacketByteBuf(PacketByteBufs.create().writeLong(cooldownEnd));
        ServerPlayNetworking.send(player, Identifier.of("kusanali", "g_cooldown_update"),
                packetByteBuf2);
    }
}
