package com.kusanali;

import com.kusanali.datagenerator.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class KusanaliDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(BlockLootTable::new);
        pack.addProvider(BlockTagsProvider::new);
        pack.addProvider(ChiLangProvider::new);
        pack.addProvider(EngLangProvider::new);
        pack.addProvider(ItemTagsProvider::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(AdvancementProvider::new);
        pack.addProvider(BiomeProvider::new);
	}
}
