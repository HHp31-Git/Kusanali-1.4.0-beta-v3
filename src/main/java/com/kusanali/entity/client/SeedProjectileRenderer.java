package com.kusanali.entity.client;

import com.kusanali.Kusanali;
import com.kusanali.entity.custom.SeedProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SeedProjectileRenderer extends EntityRenderer<SeedProjectileEntity> {
    private static final Identifier TEXTURE = new Identifier(Kusanali.MOD_ID, "textures/entity/dendro_seed.png");
    private final SeedProjectileModel model;

    public SeedProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SeedProjectileModel(context.getPart(ModModelLayers.SEED_PROJECTILE));
        this.shadowRadius = 0.0f;
    }

    @Override
    public void render(SeedProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // 跟随实体朝向旋转
        matrices.multiply(this.dispatcher.getRotation());

        // 缩放适配弹射物大小
        matrices.scale(0.5f, 0.5f, 0.5f);

        this.model.render(matrices,
                vertexConsumers.getBuffer(this.model.getLayer(TEXTURE)),
                light,
                OverlayTexture.DEFAULT_UV,
                1.0f, 1.0f, 1.0f, 1.0f
        );
        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SeedProjectileEntity entity) {
        return TEXTURE;
    }
}