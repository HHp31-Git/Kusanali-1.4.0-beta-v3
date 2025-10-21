package com.kusanali.server;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class FloatDreamServer {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "activate_float_dream"),
                (server, player, handler, buf, responseSender) ->
                        server.execute(() -> {
                    // 检查玩家是否手持float_dream
                    ItemStack mainHandStack = player.getMainHandStack();
                    if (mainHandStack.getItem() != ModItems.FLOAT_DREAM)
                        return;

                    // 检查冷却（使用玩家NBT存储冷却数据）
                    if (player.writeNbt(new NbtCompound()).getInt("float_dream_cooldown") > 0) {
                         return; // 仍在冷却中
                    }
                    // 设置冷却（30秒 = 600游戏刻）
                    long cooldownEndTime = System.currentTimeMillis() + 30000; // 30秒
                    player.writeNbt(new NbtCompound()).putLong("float_dream_cooldown", cooldownEndTime);

                    // 同步到客户端
                    PacketByteBuf bufr = new PacketByteBuf(Unpooled.buffer());
                    bufr.writeLong(cooldownEndTime);
                    ServerPlayNetworking.send(player, new Identifier("kusanali", "cooldown_update"), bufr);

                    // 给玩家自身添加力量效果（20秒 = 400游戏刻，等级0表示I级）
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 0));

                    // 获取25格半径范围内的所有生物
                    Box area = new Box(player.getBlockPos()).expand(25);
                    player.getWorld().getEntitiesByClass(LivingEntity.class, area, entity ->
                            entity instanceof Monster && entity != player // 敌对生物且排除玩家自己
                    ).forEach(entity -> {
                        // 添加发光效果（20秒）
                        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 400, 0));

                        // 添加自定义魔法伤害效果（20秒，每5秒触发一次）
                        entity.addStatusEffect(new StatusEffectInstance(ModEffects.MAGIC_DAMAGE, 400, 0));
                    });
                }));

        // 服务器端冷却计时更新
        ServerTickEvents.END_SERVER_TICK.register(server ->
                server.getPlayerManager().getPlayerList().forEach(player -> {
            int cooldown = player.writeNbt(new NbtCompound()).getInt("float_dream_cooldown");
            if (cooldown > 0) {
                player.writeNbt(new NbtCompound()).putInt("float_dream_cooldown", cooldown - 1);

                // 每20刻（1秒）同步一次到客户端，减少网络流量
                if (cooldown % 20 == 0) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeLong(cooldown - 1);
                    ServerPlayNetworking.send(player, new Identifier("kusanali", "cooldown_update"),
                            buf);
                }
            }
        }));
    }
}
