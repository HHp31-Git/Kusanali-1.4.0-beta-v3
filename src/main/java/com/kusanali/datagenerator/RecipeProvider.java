package com.kusanali.datagenerator;

import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

import static net.minecraft.world.gen.feature.Feature.SIMPLE_BLOCK;

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
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.IVE_NEVER_FORGOTTEN)
                .pattern(" b ").pattern("bab").pattern(" b ")
                .input('a', ModItems.ARANAS_FLOWER)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .input('b', ModItems.BLESSED_SEED)
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
    }
}
