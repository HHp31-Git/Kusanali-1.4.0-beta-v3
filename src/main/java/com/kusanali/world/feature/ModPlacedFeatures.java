package com.kusanali.world.feature;

import com.kusanali.register.ModBlocks;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import static com.kusanali.Kusanali.MOD_ID;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> AJI_TREE_PLACED_KEY = of("aji_tree_placed");
    public static final RegistryKey<PlacedFeature> SUMIRU_ROSE_PLACED_KEY = of("sumiru_rose_placed");

    public static void bootstrap(Registerable<PlacedFeature> featureRegistry) {
        RegistryEntryLookup<ConfiguredFeature<?,?>> registryEntryLookup =
                featureRegistry.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);



        PlacedFeatures.register(featureRegistry, AJI_TREE_PLACED_KEY,
                registryEntryLookup.getOrThrow(ModConfiguredFeatures.AJI_TREE_KEY),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                        PlacedFeatures.createCountExtraModifier(1, 0.02f, 1),
                        ModBlocks.AJI_SAPLING
                ));

        PlacedFeatures.register(featureRegistry, SUMIRU_ROSE_PLACED_KEY,
                registryEntryLookup.getOrThrow(ModConfiguredFeatures.SUMIRU_ROSE_KEY),
                RarityFilterPlacementModifier.of(20),
                SquarePlacementModifier.of(),
                PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                BiomePlacementModifier.of());

    }
    public static RegistryKey<PlacedFeature> of(String id) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MOD_ID, id));
    }
}
