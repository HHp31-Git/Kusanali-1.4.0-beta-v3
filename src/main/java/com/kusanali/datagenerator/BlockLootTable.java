package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class BlockLootTable extends FabricBlockLootTableProvider {
    public BlockLootTable(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.AJI_LOG);
        addDrop(ModBlocks.AJI_WOOD);
        addDrop(ModBlocks.AJI_PLANKS);
        addDrop(ModBlocks.AJI_SAPLING);
        addDrop(ModBlocks.STRIPPED_AJI_LOG);
        addDrop(ModBlocks.STRIPPED_AJI_WOOD);

        addPottedPlantDrops(ModBlocks.POTTED_SUMIRU_ROSE);
        addPottedPlantDrops(ModBlocks.POTTED_PADISARAH);
        addDrop(ModBlocks.SUMIRU_ROSE);
        addDrop(ModBlocks.PADISARAH);

        addDrop(ModBlocks.AJI_LEAVES, leavesDrops(ModBlocks.AJI_LEAVES, ModBlocks.AJI_SAPLING, 0F, 0.05F));
    }
}
