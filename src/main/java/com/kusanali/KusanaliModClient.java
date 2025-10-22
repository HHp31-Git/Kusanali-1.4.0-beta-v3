package com.kusanali;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModKeySet;
import com.kusanali.server.FloatDreamAbilityClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.render.RenderLayer;

public class KusanaliModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUMIRU_ROSE, RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_SUMIRU_ROSE, RenderLayer.getCutout());
        });

        ModKeySet.register();
        FloatDreamAbilityClient.register();
    }
}
