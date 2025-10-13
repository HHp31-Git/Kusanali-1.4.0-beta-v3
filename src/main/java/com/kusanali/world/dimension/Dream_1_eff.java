package com.kusanali.world.dimension;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

public class Dream_1_eff extends DimensionEffects {
    public Dream_1_eff(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened) {
        super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        double R = 1.0;
        double G = 0.8;
        double B = 0.9;
        return new Vec3d(R, G, B);
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }
}
