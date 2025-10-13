package com.kusanali;

import com.kusanali.world.dimension.Dream_1_eff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.util.Identifier;

public class KusanaliModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DimensionRenderingRegistry.registerDimensionEffects(
                new Identifier(Kusanali.MOD_ID, "dream_di_1"),
                new Dream_1_eff(0.0f, true, Dream_1_eff.SkyType.NORMAL, false, false)
        );
    }
}
