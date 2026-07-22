package com.kusanali.entity.client;

import com.kusanali.Kusanali;
import com.kusanali.entity.custom.DendroSeedEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class DendroSeedRenderer extends LivingEntityRenderer<DendroSeedEntity, DendroSeed<DendroSeedEntity>> {
    public static final Identifier SEED_TEXTURE = new Identifier(Kusanali.MOD_ID, "textures/entity/dendro_seed.png");
    public DendroSeedRenderer(EntityRendererFactory.Context context) {
        super(context, new DendroSeed<>(context.getPart(ModModelLayers.DENDRO_SEED)), 0.5f);
    }

    @Override
    public Identifier getTexture(DendroSeedEntity entity) {
        return SEED_TEXTURE;
    }
}
