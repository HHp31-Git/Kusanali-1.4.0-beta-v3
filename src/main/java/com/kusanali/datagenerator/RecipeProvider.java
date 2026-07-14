package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FLOAT_DREAM)
                .pattern(" b ").pattern("ifi").pattern("cec")
                .input('f', ModItems.ARANAS_FLOWER)
                .input('i', Items.DIAMOND_BLOCK)
                .input('b', ModItems.BLESSED_SEED)
                .input('c', ModItems.BLESSED_BENCH)
                .input('e', Items.EMERALD_BLOCK)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .criterion(FabricRecipeProvider.hasItem(Items.DIAMOND_BLOCK),
                        FabricRecipeProvider.conditionsFromItem(Items.DIAMOND_BLOCK))
                .criterion(FabricRecipeProvider.hasItem(ModItems.BLESSED_SEED),
                        FabricRecipeProvider.conditionsFromItem(ModItems.BLESSED_SEED))
                .criterion(FabricRecipeProvider.hasItem(ModItems.BLESSED_BENCH),
                        FabricRecipeProvider.conditionsFromItem(ModItems.BLESSED_BENCH))
                .criterion(FabricRecipeProvider.hasItem(Items.EMERALD_BLOCK),
                        FabricRecipeProvider.conditionsFromItem(Items.EMERALD_BLOCK))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COROLLA)
                .pattern(" b ").pattern("b b").pattern(" b ")
                .input('b', ModItems.ARANAS_FLOWER)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CLIENT)
                .pattern(" bb").pattern("b b").pattern("bb ")
                .input('b', ModItems.BLESSED_BENCH)
                .criterion(FabricRecipeProvider.hasItem(ModItems.BLESSED_BENCH),
                        FabricRecipeProvider.conditionsFromItem(ModItems.BLESSED_BENCH))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.IVE_NEVER_FORGOTTEN)
                .pattern(" b ").pattern("bab").pattern(" b ")
                .input('a', ModItems.ARANAS_FLOWER)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .input('b', ModItems.BLESSED_SEED)
                .criterion(FabricRecipeProvider.hasItem(ModItems.BLESSED_SEED),
                        FabricRecipeProvider.conditionsFromItem(ModItems.BLESSED_SEED))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CANDIED_NUT)
                .pattern(" b ").pattern("bcb").pattern(" a ")
                .input('b', Items.SUGAR)
                .criterion(FabricRecipeProvider.hasItem(Items.SUGAR),
                        FabricRecipeProvider.conditionsFromItem(Items.SUGAR))
                .input('c', ModItems.AJILENAKH)
                .criterion(FabricRecipeProvider.hasItem(ModItems.AJILENAKH),
                        FabricRecipeProvider.conditionsFromItem(ModItems.AJILENAKH))
                .input('a', Items.MILK_BUCKET)
                .criterion(FabricRecipeProvider.hasItem(Items.MILK_BUCKET),
                        FabricRecipeProvider.conditionsFromItem(Items.MILK_BUCKET))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HALVAMAZE)
                .pattern("dbd").pattern("bcb").pattern(" a ")
                .input('b', Items.SUGAR)
                .criterion(FabricRecipeProvider.hasItem(Items.SUGAR),
                        FabricRecipeProvider.conditionsFromItem(Items.SUGAR))
                .input('c', ModItems.AJILENAKH)
                .criterion(FabricRecipeProvider.hasItem(ModItems.AJILENAKH),
                        FabricRecipeProvider.conditionsFromItem(ModItems.AJILENAKH))
                .input('a', Items.MILK_BUCKET)
                .criterion(FabricRecipeProvider.hasItem(Items.MILK_BUCKET),
                        FabricRecipeProvider.conditionsFromItem(Items.MILK_BUCKET))
                .input('d', ModItems.BLESSED_SEED)
                .criterion(FabricRecipeProvider.hasItem(ModItems.BLESSED_SEED),
                        FabricRecipeProvider.conditionsFromItem(ModItems.BLESSED_SEED))
                .offerTo(consumer);


        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BLESSED_BENCH, 1)
                .input(Items.STICK)
                .input(ModItems.ARANAS_FLOWER)
                .criterion(hasItem(ModItems.ARANAS_FLOWER), conditionsFromItem(ModItems.ARANAS_FLOWER))
                .offerTo(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BLESSED_SEED, 1)
                .input(Items.WHEAT_SEEDS)
                .input(ModItems.ARANAS_FLOWER)
                .criterion(hasItem(ModItems.ARANAS_FLOWER), conditionsFromItem(ModItems.ARANAS_FLOWER))
                .offerTo(consumer);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.AJI_SAPLING, 1)
                .input(ModItems.AJILENAKH)
                .criterion(hasItem(ModItems.AJILENAKH), conditionsFromItem(ModItems.AJILENAKH))
                .offerTo(consumer);

        TagKey<Item> ajiLogsTag =
                TagKey.of(RegistryKeys.ITEM, new Identifier("kusanali", "aji_log"));
                TagKey.of(RegistryKeys.ITEM, new Identifier("kusanali", "aji_wood"));
                TagKey.of(RegistryKeys.ITEM, new Identifier("kusanali", "stripped_aji_log"));
                TagKey.of(RegistryKeys.ITEM, new Identifier("kusanali", "stripped_aji_wood"));
                //合成木板tag集合
        ShapelessRecipeJsonBuilder.create(
                        RecipeCategory.MISC,
                        ModBlocks.AJI_PLANKS,4
                )
                .input(ajiLogsTag)
                .criterion("has_aji_log", conditionsFromTag(ajiLogsTag))
                .offerTo(consumer, new Identifier("kusanali", "aji_planks_from_logs"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.AJI_WOOD, 1)
                .input(ModBlocks.AJI_LOG)
                .criterion(hasItem(ModBlocks.AJI_LOG), conditionsFromItem(ModBlocks.AJI_LOG))
                .offerTo(consumer);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.STRIPPED_AJI_WOOD, 1)
                .input(ModBlocks.STRIPPED_AJI_LOG)
                .criterion(hasItem(ModBlocks.STRIPPED_AJI_LOG), conditionsFromItem(ModBlocks.STRIPPED_AJI_LOG))
                .offerTo(consumer);
    }
}
