package com.kusanali;

import com.kusanali.entity.ModEntities;
import com.kusanali.entity.client.DendroSeed;
import com.kusanali.entity.client.DendroSeedRenderer;
import com.kusanali.entity.client.ModModelLayers;
import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModKeySet;
import com.kusanali.server.FloatDreamAbilityClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class KusanaliModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUMIRU_ROSE, RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_SUMIRU_ROSE, RenderLayer.getCutout());
        });

        ModKeySet.register();

        FloatDreamAbilityClient.register();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AJI_SAPLING, RenderLayer.getCutout());

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.DENDRO_SEED,
                DendroSeed::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.DENDRO_SEED, DendroSeedRenderer::new);
    }
}
