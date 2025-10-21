package com.kusanali.register;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block SUMIRU_ROSE =
            register("sumiru_rose", new FlowerBlock(StatusEffects.LUCK, 10,
                    FabricBlockSettings.copyOf(Blocks.ORANGE_TULIP)));
    public static final Block POTTED_SUMIRU_ROSE =
            register("potted_sumiru_rose", new Block(FabricBlockSettings.copyOf(Blocks.POTTED_ORANGE_TULIP)));
    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, Identifier.of("kusanali", path), block);
        Registry.register(Registries.ITEM, Identifier.of("kusanali", path), new BlockItem(block, new Item.Settings()));
        return block;
    }
    public static void initialize() {
    }
}
