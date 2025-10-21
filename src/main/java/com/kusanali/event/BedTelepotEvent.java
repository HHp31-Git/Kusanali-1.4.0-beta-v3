package com.kusanali.event;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.BedBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class BedTelepotEvent {
    private static final Identifier DREAM_DIMENSION_ID = new Identifier("kusanali", "dream_di_1");

    public static void register() {
        // 注册玩家与方块交互事件，使用HIGH优先级确保先于睡觉逻辑执行
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // 检查交互的方块是否为床
            if (!(world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof BedBlock)) {
                return ActionResult.PASS; // 不是床，不处理
            }

            // 确保逻辑在服务器端执行
            if (world.isClient) {
                return ActionResult.PASS;
            }

            // 检查玩家是否佩戴头盔
            ItemStack helmetStack = player.getInventory().getArmorStack(3);
            if (helmetStack.getItem() != ModItems.CLIENT) {
                return ActionResult.PASS; // 不是指定的头盔物品
            }

            // 获取服务器玩家对象
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            // 传送前：在当前位置生成粒子效果
            if (world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
                // 在主世界：传送至自定义维度
                teleportToDreamDimension(serverPlayer, hitResult.getBlockPos());
            } else if (world.getRegistryKey().getValue().equals(DREAM_DIMENSION_ID)) {
                // 在自定义维度：传送回主世界
                teleportToOverworld(serverPlayer);
            }

            // 返回SUCCESS并取消事件，阻止原版睡觉逻辑
            return ActionResult.SUCCESS;
        });
    }

    private static void teleportToDreamDimension(ServerPlayerEntity player, BlockPos bedPos) {
        ServerWorld dreamWorld =
                Objects.requireNonNull(player.getServer()).getWorld(RegistryKey.of(RegistryKeys.WORLD, DREAM_DIMENSION_ID));
        if (dreamWorld == null) {
            System.err.println("梦境1执行错误");
            return;
        }

        // 在主世界玩家数据中存储当前位置
        NbtCompound playerData = player.writeNbt(new NbtCompound());
        NbtCompound portalPos = new NbtCompound();
        portalPos.putInt("x", bedPos.getX());
        portalPos.putInt("y", bedPos.getY());
        portalPos.putInt("z", bedPos.getZ());
        playerData.put("last_overworld_pos", portalPos);
        playerData.putFloat("yaw", player.getYaw());
        playerData.putFloat("pitch", player.getPitch());

        // 传送到固定坐标
        BlockPos dreamPos = new BlockPos(0, -60, 0);
        player.teleport(dreamWorld,
                dreamPos.getX() + 0.5, dreamPos.getY(), dreamPos.getZ() + 0.5,
                player.getYaw(), player.getPitch());
    }

    private static void teleportToOverworld(ServerPlayerEntity player) {
        ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
        NbtCompound playerData = player.writeNbt(new NbtCompound());

        // 读取存储的主世界位置
        if (playerData.contains("last_overworld_pos")) {
            NbtCompound portalPos = playerData.getCompound("last_overworld_pos");
            int x = portalPos.getInt("x");
            int y = portalPos.getInt("y");
            int z = portalPos.getInt("z");
            float yaw = playerData.getFloat("yaw");
            float pitch = playerData.getFloat("pitch");

            // 传回存储的主世界位置
            player.teleport(overworld,
                    x + 0.5, y, z + 0.5,
                    yaw, pitch);
        } else {
            // 如果没有存储的位置，传送到主世界出生点
            BlockPos spawnPos = Objects.requireNonNull(overworld).getSpawnPos();
            player.teleport(overworld,
                    spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    player.getYaw(), player.getPitch());
        }
    }
}
