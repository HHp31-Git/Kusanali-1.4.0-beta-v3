package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE);
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE);
        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL);
        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.AJI_LOG)
                .add(ModBlocks.AJI_WOOD)
                .add(ModBlocks.STRIPPED_AJI_LOG)
                .add(ModBlocks.STRIPPED_AJI_WOOD);
        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(ModBlocks.AJI_LEAVES);
        getOrCreateTagBuilder(BlockTags.SAPLINGS)
                .add(ModBlocks.AJI_SAPLING);
        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(ModBlocks.AJI_PLANKS);
    }
}
