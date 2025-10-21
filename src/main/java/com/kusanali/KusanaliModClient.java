package com.kusanali;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModKeySet;
import com.kusanali.server.FloatDreamHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class KusanaliModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUMIRU_ROSE, RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_SUMIRU_ROSE, RenderLayer.getCutout());
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> FloatDreamHud.rCooldownEndTime = serverCooldownEnd);
                });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "e_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> FloatDreamHud.eCooldownEndTime = serverCooldownEnd);
                });

        ModKeySet.register();
    }
}
