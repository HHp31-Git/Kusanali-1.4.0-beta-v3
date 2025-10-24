package com.kusanali.world.feature;

import com.kusanali.Kusanali;
import com.kusanali.register.ModBlocks;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> AJI_TREE_KEY = of("aji_tree");

    public static final RegistryKey<ConfiguredFeature<?, ?>> SUMIRU_ROSE_KEY = of("sumiru_rose_key");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> registry) {
        ConfiguredFeatures.register(registry, AJI_TREE_KEY, Feature.TREE,
                new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(ModBlocks.AJI_LOG),
                        new ForkingTrunkPlacer(5, 2, 2),
                        BlockStateProvider.of(ModBlocks.AJI_LEAVES),
                        new LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1), 3),
                        new TwoLayersFeatureSize(2, 0, 2)
                ).build());

        ConfiguredFeatures.register(registry, SUMIRU_ROSE_KEY, Feature.FLOWER,
                new RandomPatchFeatureConfig(20, 4, 3, PlacedFeatures.createEntry(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.SUMIRU_ROSE))
                )));
    }
    public static RegistryKey<ConfiguredFeature<?, ?>> of(String id) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(Kusanali.MOD_ID, id));
    }
}
