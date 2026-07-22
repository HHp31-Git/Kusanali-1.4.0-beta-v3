package com.kusanali.entity.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.kusanali.Kusanali.MOD_ID;

public class ModModelLayers {
    public static final EntityModelLayer DENDRO_SEED =
            new EntityModelLayer(new Identifier(MOD_ID, "dendro_seed"), "main");
    public static final EntityModelLayer SEED_PROJECTILE =
            new EntityModelLayer(new Identifier(MOD_ID, "seed_projectile"), "main");
}
