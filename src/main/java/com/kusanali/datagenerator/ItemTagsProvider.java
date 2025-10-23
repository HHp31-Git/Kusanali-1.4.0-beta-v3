package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.MUSIC_DISCS)
                .add(ModItems.IVE_NEVER_FORGOTTEN);
        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.AJI_PLANKS.asItem());
        getOrCreateTagBuilder(ItemTags.LOGS)
                .add(ModBlocks.AJI_LOG.asItem())
                .add(ModBlocks.STRIPPED_AJI_LOG.asItem())
                .add(ModBlocks.AJI_WOOD.asItem())
                .add(ModBlocks.STRIPPED_AJI_WOOD.asItem());
        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.AJI_LOG.asItem())
                .add(ModBlocks.AJI_WOOD.asItem())
                .add(ModBlocks.STRIPPED_AJI_LOG.asItem())
                .add(ModBlocks.STRIPPED_AJI_WOOD.asItem());
    }
}
