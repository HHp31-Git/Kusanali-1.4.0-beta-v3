package com.kusanali.server;
import com.kusanali.register.ModEffects;
import com.kusanali.register.ModItems;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FloatDreamForEServer {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "activate_ability"),
                (server, player, handler, buf, responseSender) ->
                        server.execute(() -> {
                    // 检查冷却
                    long cooldownEnd = player.writeNbt(new NbtCompound()).getLong("ability_cooldown");
                    if (System.currentTimeMillis() < cooldownEnd) {
                        return; // 冷却中
                    }

                    // 检查玩家是否拥有float_dream物品
                    if (!hasFloatDream(player)) {
                        return;
                    }

                    // 执行能力
                    executeAbility(player);
                }));
    }

    private static boolean hasFloatDream(@NotNull PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == ModItems.FLOAT_DREAM) {
                return true;
            }
        }
        return false;
    }

    private static void executeAbility(@NotNull ServerPlayerEntity player) {
        // 射线检测，范围8格
        HitResult hit = player.raycast(8.0, 0.0f, false);

        if (hit.getType() == HitResult.Type.ENTITY) {
            // 优先处理生物
            EntityHitResult entityHit = (EntityHitResult) hit;
            if (entityHit.getEntity() instanceof LivingEntity target) {

                // 造成7点魔法伤害
                target.damage(player.getDamageSources().magic(), 7.0f);

                // 应用Tribble效果（发光+伤害加成）
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 9 * 20, 0));
                target.addStatusEffect(new StatusEffectInstance(ModEffects.TRIBBLE, 9 * 20, 0));

                // 可选：添加粒子效果和音效
                player.getWorld().playSound(null, player.getBlockPos(),
                        net.minecraft.sound.SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL,
                        net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            // 处理花朵
            BlockPos blockPos = new BlockPos((int) hit.getPos().x, (int) hit.getPos().y, (int) hit.getPos().z);
            if (player.getWorld().getBlockState(blockPos).getBlock() instanceof FlowerBlock) {
                // 破坏花并直接收集掉落物到玩家背包
                List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(
                        player.getWorld().getBlockState(blockPos),
                        (net.minecraft.server.world.ServerWorld) player.getWorld(),
                        blockPos, null, player, ItemStack.EMPTY);

                for (ItemStack stack : drops) {
                    player.getInventory().insertStack(stack);
                }

                player.getWorld().removeBlock(blockPos, false);

                // 可选：添加破坏粒子效果
                player.getWorld().playSound(null, blockPos,
                        net.minecraft.sound.SoundEvents.BLOCK_GRASS_BREAK,
                        net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }

        // 设置11秒冷却
        long cooldownEndTime = System.currentTimeMillis() + 11000;
        player.writeNbt(new NbtCompound()).putLong("ability_cooldown", cooldownEndTime);

        // 同步冷却到客户端
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeLong(cooldownEndTime);
        ServerPlayNetworking.send(player, new Identifier("kusanali", "e_cooldown_update"), buf);
    }
}
