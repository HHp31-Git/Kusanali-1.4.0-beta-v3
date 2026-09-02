package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerLog(ModBlocks.AJI_LOG)
                .log(ModBlocks.AJI_LOG).wood(ModBlocks.AJI_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_AJI_LOG)
                .log(ModBlocks.STRIPPED_AJI_LOG).wood(ModBlocks.STRIPPED_AJI_WOOD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.AJI_PLANKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.AJI_LEAVES);

        blockStateModelGenerator.registerTintableCross(ModBlocks.AJI_SAPLING,
                BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.SUMIRU_ROSE, ModBlocks.POTTED_SUMIRU_ROSE,
                BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerFlowerPotPlant(ModBlocks.PADISARAH, ModBlocks.POTTED_PADISARAH,
                BlockStateModelGenerator.TintType.NOT_TINTED);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.ARANAS_FLOWER, Models.GENERATED);
        itemModelGenerator.register(ModItems.CANDIED_NUT, Models.GENERATED);
        itemModelGenerator.register(ModItems.HALVAMAZE, Models.GENERATED);
        itemModelGenerator.register(ModItems.IVE_NEVER_FORGOTTEN, Models.GENERATED);
        itemModelGenerator.register(ModItems.AJILENAKH, Models.GENERATED);
        itemModelGenerator.register(ModItems.AKASA_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_GEMSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_CHUCK, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_SLIVER, Models.GENERATED);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.COROLLA);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.CLIENT);

        itemModelGenerator.register(ModItems.ANEMO_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.CYRO_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.DENDRO_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.ELECTRO_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.GEO_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.HYDRO_ITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.PYRO_ITEM, Models.GENERATED);

        itemModelGenerator.register(ModItems.TROUPE_SWORD, Models.HANDHELD);
    }
}
