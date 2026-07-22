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
        PlayerBlockBreakEvents.AFTER.register(
                (world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == ModBlocks.AJI_LEAVES) {
                if (!world.isClient) {
                    Random random = new Random();
                    if (random.nextFloat() < 0.05f) {
                        ItemStack additionalDrop = new ItemStack(ModItems.AJILENAKH, 1);
                        ItemEntity itemEntity = new ItemEntity(
                                world,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                additionalDrop
                        );
                        itemEntity.setVelocity(
                                random.nextDouble() * 0.1 - 0.05,
                                0.2,
                                random.nextDouble() * 0.1 - 0.05
                        );
                        world.spawnEntity(itemEntity);
                    }
                }
            }
        });
    }
}
