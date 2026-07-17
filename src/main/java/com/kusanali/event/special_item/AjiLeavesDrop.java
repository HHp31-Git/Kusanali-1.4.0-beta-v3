package com.kusanali.event.special_item;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class AjiLeavesDrop {
    //控制枣椰掉落
    public static void register() {
        // 注册方块破坏事件监听器
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // 检查被破坏的方块是否为 AJI_LEAVES
            if (state.getBlock() == ModBlocks.AJI_LEAVES) {
                // 确保逻辑在服务器端执行（避免客户端重复执行）
                if (!world.isClient) {
                    // 创建随机数生成器
                    Random random = new Random();

                    // 计算 5% 概率 (0.05)
                    if (random.nextFloat() < 0.05f) {
                        // 创建 ajilenakh 物品堆栈（数量为1）
                        ItemStack additionalDrop = new ItemStack(ModItems.AJILENAKH, 1);

                        // 在方块位置生成物品实体（掉落物）
                        // 使用方块中心坐标（+0.5偏移）使掉落物出现在方块中间
                        ItemEntity itemEntity = new ItemEntity(
                                world,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                additionalDrop
                        );

                        // 设置掉落物的随机轻微速度（模拟自然掉落效果）
                        itemEntity.setVelocity(
                                random.nextDouble() * 0.1 - 0.05,
                                0.2,
                                random.nextDouble() * 0.1 - 0.05
                        );

                        // 将物品实体生成到世界中
                        world.spawnEntity(itemEntity);
                    }
                }
            }
        });
    }
}
