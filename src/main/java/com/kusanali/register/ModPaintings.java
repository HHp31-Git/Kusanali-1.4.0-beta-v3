package com.kusanali.register;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPaintings extends PaintingVariant {
    public static final PaintingVariant NHD_1 = register("nhd_1", new PaintingVariant(32, 32));
    public static final PaintingVariant NHD_2 = register("nhd_2", new PaintingVariant(32,32));

    public ModPaintings(int width, int height) {
        super(width, height);
    }
    private static PaintingVariant register(String id, PaintingVariant variant) {
        return Registry.register(Registries.PAINTING_VARIANT, new Identifier("kusanali", id), variant);
    }
    public static void init(){

    }
}
