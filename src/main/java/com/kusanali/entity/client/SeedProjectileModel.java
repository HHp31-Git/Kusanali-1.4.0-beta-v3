package com.kusanali.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SeedProjectileModel extends EntityModel<Entity> {
    private final ModelPart bone;

    public SeedProjectileModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        // 完全复用 DendroSeed 的模型数据
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone",
                ModelPartBuilder.create()
                        .uv(0, 9).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-2.0F, -5.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                        .uv(9, 9).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 13).cuboid(-3.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(7, 13).cuboid(2.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(14, 13).cuboid(-1.0F, -4.0F, 2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(17, 0).cuboid(-1.0F, -4.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 22.0F, 0.0F) // 稍微调整位置
        );
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // 弹射物旋转动画：沿各轴旋转
        this.bone.pitch = animationProgress * 0.3f;
        this.bone.yaw = animationProgress * 0.2f;
        this.bone.roll = animationProgress * 0.15f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        // 修正偏移
        matrices.translate(0, -1.5, 0);
        bone.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}