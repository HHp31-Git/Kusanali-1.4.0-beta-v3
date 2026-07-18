package com.kusanali.datagenerator;

import com.kusanali.Kusanali;
import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output,
                            CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    public static final TagKey<Item> ELEMENT_ITEMS =
            TagKey.of(RegistryKeys.ITEM, new Identifier(Kusanali.MOD_ID, "element_items"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, new Identifier("kusanali", "aji_logs")))
                .add(ModBlocks.AJI_LOG.asItem())
                .add(ModBlocks.AJI_WOOD.asItem())
                .add(ModBlocks.STRIPPED_AJI_LOG.asItem())
                .add(ModBlocks.STRIPPED_AJI_WOOD.asItem());

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
        getOrCreateTagBuilder(ItemTags.SMALL_FLOWERS)
                .add(ModBlocks.SUMIRU_ROSE.asItem());

        getOrCreateTagBuilder(ELEMENT_ITEMS)
                .add(ModItems.ANEMO_ITEM)
                .add(ModItems.CYRO_ITEM)
                .add(ModItems.ELECTRO_ITEM)
                .add(ModItems.GEO_ITEM)
                .add(ModItems.HYDRO_ITEM)
                .add(ModItems.PYRO_ITEM)
                .add(ModItems.ANEMO_ITEM);
    }
}
