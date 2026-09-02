package com.kusanali.event.item_getter;

import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EmeraldGetter {
    public static void register() {
        AttackBlockCallback.EVENT.register(
                (PlayerEntity player, World world, Hand hand, BlockPos pos,
                 Direction direction) -> {
                    if (world.isClient) return ActionResult.PASS;
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof CropBlock cropBlock) {
                        int age = cropBlock.getAge(state);
                        if (age == cropBlock.getMaxAge()) {
                            int range = (int)(Math.random() * 5) + 1;
                            if (range == 1) {
                                player.giveItemStack(ModItems.EMERALD_SLIVER.getDefaultStack());
                            }
                        }
                    }
                    if (block instanceof CocoaBlock) {
                        int age = state.get(CocoaBlock.AGE);
                        if (age == CocoaBlock.MAX_AGE) {
                            int range2 = (int) (Math.random() * 5) + 1;
                            if (range2 == 1) {
                                player.giveItemStack(ModItems.EMERALD_SLIVER.getDefaultStack());
                            }
                        }
                    }
                    return ActionResult.PASS;
                }
        );
    }
}
