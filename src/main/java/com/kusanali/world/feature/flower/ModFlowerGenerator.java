package com.kusanali.world.feature.flower;

import com.kusanali.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class ModFlowerGenerator {
    public static void registerFlower() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.SUMIRU_ROSE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(
                        BiomeKeys.JUNGLE,
                        BiomeKeys.BAMBOO_JUNGLE,
                        BiomeKeys.FLOWER_FOREST,
                        BiomeKeys.SPARSE_JUNGLE
                ),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.PADISARAH_PLACED_KEY);
    }
}
