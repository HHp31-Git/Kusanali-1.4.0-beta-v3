package com.kusanali.event;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
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

            // 检查玩家是否佩戴"client"头盔（头盔槽位索引为3）
            ItemStack helmetStack = player.getInventory().getArmorStack(3);
            if (helmetStack.isEmpty() || !helmetStack.getItem().getName().getString().equals("client")) {
                return ActionResult.PASS; // 未佩戴指定头盔，不处理
            }

            // 获取服务器玩家对象
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            // 1. 传送前：在当前位置生成粒子效果

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
        ServerWorld dreamWorld = Objects.requireNonNull(player.getServer()).getWorld(
                Objects.requireNonNull(player.getServer().getRegistryManager()
                        .get(RegistryKeys.WORLD)
                        .get(DREAM_DIMENSION_ID)).getRegistryKey()
        );

        if (dreamWorld == null) {
            System.err.println("维度 dream_di_1 错误");
            return;
        }

        // 在主世界玩家数据中存储当前位置（包括床的位置和玩家朝向）
        NbtCompound playerData = player.writeNbt(new NbtCompound());
        NbtCompound portalPos = new NbtCompound();
        portalPos.putInt("x", bedPos.getX());
        portalPos.putInt("y", bedPos.getY());
        portalPos.putInt("z", bedPos.getZ());
        playerData.put("last_overworld_pos", portalPos);
        playerData.putFloat("yaw", player.getYaw());
        playerData.putFloat("pitch", player.getPitch());

        // 传送到自定义维度的固定坐标 (0, 4, 0)
        // 注意：确保该位置是安全且可站立的
        BlockPos dreamPos = new BlockPos(0, 4, 0);
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
    private static void spawnTeleportParticles(World world, Vec3d centerPos, int count) {
        // 确保只在客户端渲染粒子，避免在服务器端执行
        if (world.isClient) {
            for (int i = 0; i < count; ++i) {
                // 在玩家位置周围随机偏移，创造一团粒子的效果
                double offsetX = (world.random.nextDouble() - 0.5) * 2.0; // -1.0 到 1.0
                double offsetY = world.random.nextDouble() * 2.0; // 0 到 2.0
                double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;

                double x = centerPos.x + offsetX;
                double y = centerPos.y + offsetY;
                double z = centerPos.z + offsetZ;

                // 添加粒子到世界，速度向量设为0
                world.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        } else {
            // 服务器端：向所有附近的玩家发送生成粒子的数据包
            // 这里使用ServerWorld的方法来同步粒子效果到客户端
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnParticles(ParticleTypes.PORTAL,
                    centerPos.x, centerPos.y, centerPos.z, // 中心坐标
                    count, // 粒子数量
                    1.0, 1.0, 1.0, // 在XYZ方向上的分布范围
                    0.0); // 速度
        }
    }
}
