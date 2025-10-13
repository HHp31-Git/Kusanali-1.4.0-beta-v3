package com.kusanali.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Dream_1ChuckGen extends ChunkGenerator {
    public Dream_1ChuckGen(BiomeSource biomeSource) {
        super(biomeSource);
    }
    public static final Codec<Dream_1ChuckGen> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(Dream_1ChuckGen::getBiomeSource)
            ).apply(instance, instance.stable(Dream_1ChuckGen::new))
    );

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockState(new BlockPos(x, 3, z), Blocks.GRASS_BLOCK.getDefaultState(), false);
                for (int y = 2; y >= 0; y--) {
                    chunk.setBlockState(new BlockPos(x, y, z), Blocks.DIRT.getDefaultState(), false);
                }
                chunk.setBlockState(new BlockPos(x, 0, z), Blocks.BEDROCK.getDefaultState(), false);
            }// 超平坦
        }
    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return 3;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 3;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[world.getHeight()];
        for (int y = getMinimumY(); y < world.getTopY(); y++) {
            if (y == 0) states[y - getMinimumY()] = Blocks.BEDROCK.getDefaultState();
            else if (y < 63) states[y - getMinimumY()] = Blocks.DIRT.getDefaultState();
            else if (y == 63) states[y - getMinimumY()] = Blocks.GRASS_BLOCK.getDefaultState();
            else states[y - getMinimumY()] = Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(getMinimumY(), states);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

    }
}
