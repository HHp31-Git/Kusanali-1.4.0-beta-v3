package com.kusanali.world.feature;

import com.kusanali.Kusanali;
import com.kusanali.register.ModBlocks;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> AJI_TREE_KEY = of("aji_tree");
    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> registry) {
        ConfiguredFeatures.register(registry, AJI_TREE_KEY, Feature.TREE,
                new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(ModBlocks.AJI_LOG),
                        new StraightTrunkPlacer(4, 2, 1),
                        BlockStateProvider.of(ModBlocks.AJI_LEAVES),
                        new BlobFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(2), 3),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).build());
    }
    public static RegistryKey<ConfiguredFeature<?, ?>> of(String id) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(Kusanali.MOD_ID, id));
    }
}
