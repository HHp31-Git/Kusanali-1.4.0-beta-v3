package com.kusanali.specialitem;

import com.kusanali.Kusanali;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.kusanali.server.HelmetTracker;
import net.minecraft.nbt.NbtCompound;

public class Client extends ArmorItem {
    public Client(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // 确保在服务器端执行
        if (world.isClient()) {
            return TypedActionResult.pass(itemStack);
        }

        // 检查玩家是否戴着头盔
        if (user.getEquippedStack(EquipmentSlot.HEAD).getItem() != this) {
            return TypedActionResult.fail(itemStack);
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
        ServerWorld currentWorld = (ServerWorld) world;

        // 定义主世界和梦境世界的标识符
        Identifier dreamWorldId = new Identifier(Kusanali.MOD_ID, "dream_di_1");
        RegistryKey<World> overworldKey = World.OVERWORLD;
        RegistryKey<World> dreamWorldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier("kusanali", "dream_di_1"));

        if (currentWorld.getRegistryKey().equals(overworldKey)) {
            // 在主世界，记录位置并传送到梦境
            ServerWorld dreamWorld = currentWorld.getServer().getWorld(dreamWorldKey);
            if (dreamWorld != null) {
                // 创建一个新的 NbtCompound 来存储位置
                NbtCompound posNbt = new NbtCompound();
                Vec3d currentPos = serverPlayer.getPos();
                posNbt.putDouble("x", currentPos.x);
                posNbt.putDouble("y", currentPos.y);
                posNbt.putDouble("z", currentPos.z);

                // 将 NbtCompound 存入 DataTracker
                serverPlayer.getDataTracker().set(HelmetTracker.RETURN_POS, posNbt);

                // 传送到梦境
                serverPlayer.teleport(dreamWorld, 0, 4, 0, user.getYaw(), user.getPitch());
                return TypedActionResult.success(itemStack);
            }
        } else if (currentWorld.getRegistryKey().getValue().equals(dreamWorldId)) {
            // 在梦境世界，返回主世界
            ServerWorld overworld = currentWorld.getServer().getWorld(overworldKey);
            if (overworld != null) {
                // 从 DataTracker 获取存储位置的 NbtCompound
                NbtCompound posNbt = serverPlayer.getDataTracker().get(HelmetTracker.RETURN_POS);

                // 检查 NbtCompound 是否有效 (防止玩家第一次使用就传送到梦境)
                if (posNbt != null && posNbt.contains("x")) {
                    // 从 NbtCompound 中读取坐标
                    double x = posNbt.getDouble("x");
                    double y = posNbt.getDouble("y");
                    double z = posNbt.getDouble("z");

                    // 传送回主世界
                    serverPlayer.teleport(overworld, x, y, z, user.getYaw(), user.getPitch());
                } else {
                    // 如果没有记录，则传送到世界出生点
                    serverPlayer.teleport(overworld, overworld.getSpawnPos().getX(), overworld.getSpawnPos().getY(), overworld.getSpawnPos().getZ(), user.getYaw(), user.getPitch());
                }
                return TypedActionResult.success(itemStack);
            }
        }

        return TypedActionResult.fail(itemStack);
    }
}
