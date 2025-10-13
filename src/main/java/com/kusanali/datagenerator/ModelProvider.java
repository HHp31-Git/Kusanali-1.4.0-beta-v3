package com.kusanali.datagenerator;

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

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //itemModelGenerator.register(ModItems.FLOAT_DREAM, Models.HANDHELD);
        itemModelGenerator.register(ModItems.BLESSED_SEED, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLESSED_BENCH, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARANAS_FLOWER, Models.GENERATED);
        itemModelGenerator.register(ModItems.CANDIED_NUT, Models.GENERATED);
        itemModelGenerator.register(ModItems.HALVAMAZE, Models.GENERATED);
        itemModelGenerator.register(ModItems.IVE_NEVER_FORGOTTEN, Models.GENERATED);
        itemModelGenerator.register(ModItems.AJILENAKH, Models.GENERATED);

        itemModelGenerator.registerArmor((ArmorItem) ModItems.COROLLA);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.CLIENT);
    }
}
