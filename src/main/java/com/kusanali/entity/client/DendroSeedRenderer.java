package com.kusanali.entity.client;

import com.kusanali.Kusanali;
import com.kusanali.entity.custom.DendroSeedEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DendroSeedRenderer extends EntityRenderer<DendroSeedEntity> {
    public static final Identifier SEED_TEXTURE = new Identifier(Kusanali.MOD_ID, "textures/entity/dendro_seed.png");
    private final DendroSeed model;

    public DendroSeedRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new DendroSeed(context.getPart(ModModelLayers.DENDRO_SEED));
        this.shadowRadius = 0.0f; // 无阴影
    }

    @Override
    public void render(DendroSeedEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        // 调整位置使模型居中
        matrices.translate(0, 0.375, 0);
        this.model.render(matrices,
                vertexConsumers.getBuffer(this.model.getLayer(SEED_TEXTURE)),
                light,
                OverlayTexture.DEFAULT_UV,
                1.0f, 1.0f, 1.0f, 1.0f
        );
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(DendroSeedEntity entity) {
        return SEED_TEXTURE;
    }
}
