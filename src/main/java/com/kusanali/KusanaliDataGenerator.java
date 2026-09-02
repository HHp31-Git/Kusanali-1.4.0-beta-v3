package com.kusanali;

import com.kusanali.datagenerator.*;
import com.kusanali.world.feature.ModConfiguredFeatures;
import com.kusanali.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

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
        pack.addProvider(WorldGenerator::new);
        pack.addProvider(EntityTypeTagsProvider::new);
        pack.addProvider(DamageTypeProvider::new);
        pack.addProvider(DamageTypeTagProvider::new);
        pack.addProvider(EnchantTagProvider::new);
	}
    @Override
    public void buildRegistry(RegistryBuilder builder) {
        builder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        builder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
        builder.addRegistry(RegistryKeys.DAMAGE_TYPE, context -> {});
    }
}
