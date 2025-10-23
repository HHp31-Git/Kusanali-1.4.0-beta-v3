package com.kusanali.world.feature.tree;

import com.kusanali.world.feature.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class ModTreeGeneration {
    public static void registerTree() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.BADLANDS,BiomeKeys.WOODED_BADLANDS,BiomeKeys.ERODED_BADLANDS),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.AJI_TREE_PLACED_KEY);
    }
}
