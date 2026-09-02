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
                .input('i', Items.DIAMOND)
                .input('b', ModItems.EMERALD_GEMSTONE)
                .input('c', ModItems.EMERALD_CHUCK)
                .input('e', Items.EMERALD)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .criterion(FabricRecipeProvider.hasItem(Items.DIAMOND),
                        FabricRecipeProvider.conditionsFromItem(Items.DIAMOND))
                .criterion(FabricRecipeProvider.hasItem(ModItems.EMERALD_GEMSTONE),
                        FabricRecipeProvider.conditionsFromItem(ModItems.EMERALD_GEMSTONE))
                .criterion(FabricRecipeProvider.hasItem(ModItems.EMERALD_CHUCK),
                        FabricRecipeProvider.conditionsFromItem(ModItems.EMERALD_CHUCK))
                .criterion(FabricRecipeProvider.hasItem(Items.EMERALD),
                        FabricRecipeProvider.conditionsFromItem(Items.EMERALD))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COROLLA)
                .pattern(" b ").pattern("b b").pattern(" b ")
                .input('b', ModItems.ARANAS_FLOWER)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CLIENT)
                .pattern(" bb").pattern("bcb").pattern("bb ")
                .input('b', ModItems.EMERALD_FRAGMENT)
                .input('c', ModItems.AKASA_CORE)
                .criterion(FabricRecipeProvider.hasItem(ModItems.EMERALD_FRAGMENT),
                        FabricRecipeProvider.conditionsFromItem(ModItems.EMERALD_FRAGMENT))
                .criterion(FabricRecipeProvider.hasItem(ModItems.AKASA_CORE),
                        FabricRecipeProvider.conditionsFromItem(ModItems.AKASA_CORE))
                .offerTo(consumer);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.IVE_NEVER_FORGOTTEN)
                .pattern(" b ").pattern("bab").pattern(" b ")
                .input('a', ModItems.ARANAS_FLOWER)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARANAS_FLOWER),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARANAS_FLOWER))
                .input('b', ModItems.EMERALD_FRAGMENT)
                .criterion(FabricRecipeProvider.hasItem(ModItems.EMERALD_FRAGMENT),
                        FabricRecipeProvider.conditionsFromItem(ModItems.EMERALD_FRAGMENT))
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
                .input('d', ModItems.EMERALD_FRAGMENT)
                .criterion(FabricRecipeProvider.hasItem(ModItems.EMERALD_FRAGMENT),
                        FabricRecipeProvider.conditionsFromItem(ModItems.EMERALD_FRAGMENT))
                .offerTo(consumer);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.AKASA_CORE)
                .pattern("aba").pattern("bcb").pattern("aba")
                .input('a', Items.REDSTONE_BLOCK)
                .criterion(FabricRecipeProvider.hasItem(Items.REDSTONE_BLOCK),
                        FabricRecipeProvider.conditionsFromItem(Items.REDSTONE_BLOCK))
                .input('c', Items.DIAMOND_BLOCK)
                .criterion(FabricRecipeProvider.hasItem(Items.DIAMOND_BLOCK),
                        FabricRecipeProvider.conditionsFromItem(Items.DIAMOND_BLOCK))
                .input('b', Items.IRON_BLOCK)
                .criterion(FabricRecipeProvider.hasItem(Items.IRON_BLOCK),
                        FabricRecipeProvider.conditionsFromItem(Items.IRON_BLOCK))
                .offerTo(consumer);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.TROUPE_SWORD)
                .pattern(" cd").pattern(" c ").pattern("b  ")
                .input('b', Items.STICK)
                .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                        FabricRecipeProvider.conditionsFromItem(Items.STICK))
                .input('c', Items.IRON_INGOT)
                .criterion(FabricRecipeProvider.hasItem(Items.IRON_INGOT),
                        FabricRecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                .input('d', Items.IRON_NUGGET)
                .criterion(FabricRecipeProvider.hasItem(Items.IRON_NUGGET),
                        FabricRecipeProvider.conditionsFromItem(Items.IRON_NUGGET))
                .offerTo(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.AJI_SAPLING, 1)
                .input(ModItems.AJILENAKH)
                .criterion(hasItem(ModItems.AJILENAKH), conditionsFromItem(ModItems.AJILENAKH))
                .offerTo(consumer);

        TagKey<Item> ajiLogsTag = TagKey.of(RegistryKeys.ITEM, new Identifier("kusanali", "aji_logs"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.AJI_PLANKS, 4)
                .input(ajiLogsTag)
                .criterion("has_aji_logs", conditionsFromTag(ajiLogsTag))
                .offerTo(consumer, new Identifier("kusanali", "aji_planks_from_logs"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.AJI_WOOD, 1)
                .input(ModBlocks.AJI_LOG)
                .criterion(hasItem(ModBlocks.AJI_LOG), conditionsFromItem(ModBlocks.AJI_LOG))
                .offerTo(consumer);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.STRIPPED_AJI_WOOD, 1)
                .input(ModBlocks.STRIPPED_AJI_LOG)
                .criterion(hasItem(ModBlocks.STRIPPED_AJI_LOG), conditionsFromItem(ModBlocks.STRIPPED_AJI_LOG))
                .offerTo(consumer);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMERALD_GEMSTONE, 1)
                .input(ModItems.EMERALD_CHUCK)
                .input(ModItems.EMERALD_CHUCK)
                .input(ModItems.EMERALD_CHUCK)
                .criterion(hasItem(ModItems.EMERALD_CHUCK), conditionsFromItem(ModItems.EMERALD_CHUCK))
                .offerTo(consumer);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMERALD_CHUCK, 1)
                .input(ModItems.EMERALD_FRAGMENT)
                .input(ModItems.EMERALD_FRAGMENT)
                .input(ModItems.EMERALD_FRAGMENT)
                .criterion(hasItem(ModItems.EMERALD_FRAGMENT), conditionsFromItem(ModItems.EMERALD_FRAGMENT))
                .offerTo(consumer);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.EMERALD_FRAGMENT, 1)
                .input(ModItems.EMERALD_SLIVER)
                .input(ModItems.EMERALD_SLIVER)
                .input(ModItems.EMERALD_SLIVER)
                .criterion(hasItem(ModItems.EMERALD_SLIVER), conditionsFromItem(ModItems.EMERALD_SLIVER))
                .offerTo(consumer);
    }
}
