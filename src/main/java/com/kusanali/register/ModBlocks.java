package com.kusanali.register;

import com.kusanali.world.feature.tree.AjiTreeGenerator;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block SUMIRU_ROSE =
            //须弥蔷薇
            register("sumiru_rose", new FlowerBlock(StatusEffects.LUCK, 10,
                    FabricBlockSettings.copyOf(Blocks.ORANGE_TULIP)));
    public static final Block POTTED_SUMIRU_ROSE =
            //须弥蔷薇-盆栽
            register("potted_sumiru_rose", new FlowerPotBlock(ModBlocks.SUMIRU_ROSE,
                    FabricBlockSettings.copyOf(Blocks.POTTED_ORANGE_TULIP)));

    public static final Block AJI_LOG =
            //枣椰原木
            register("aji_log", new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block STRIPPED_AJI_LOG =
            //去皮枣椰原木
            register("stripped_aji_log", new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block AJI_WOOD =
            //枣椰木
            register("aji_wood", new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_AJI_WOOD =
            //去皮枣椰木
            register("stripped_aji_wood", new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block AJI_PLANKS =
            //枣椰木板
            register("aji_planks", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block AJI_LEAVES =
            //枣椰树叶
            register("aji_leaves", new LeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block AJI_SAPLING =
            //枣椰树苗
            register("aji_sapling", new SaplingBlock(new AjiTreeGenerator(),
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, Identifier.of("kusanali", path), block);
        Registry.register(Registries.ITEM, Identifier.of("kusanali", path), new BlockItem(block, new Item.Settings()));
        return block;
    }
    public static void initialize() {
    }
}
