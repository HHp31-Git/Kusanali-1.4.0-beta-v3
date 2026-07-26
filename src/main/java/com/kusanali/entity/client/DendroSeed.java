package com.kusanali.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 5.1.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

public class DendroSeed extends EntityModel<Entity> {
	private final ModelPart bone;
	public DendroSeed(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
		this.bone = root.getChild("bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 9).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-2.0F, -5.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(9, 9).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 13).cuboid(-3.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(7, 13).cuboid(2.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(14, 13).cuboid(-1.0F, -4.0F, 2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(17, 0).cuboid(-1.0F, -4.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // 种子不需要动画
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        // 修正 pivot(0,24,0) 导致的偏移
        matrices.translate(0, -1.5, 0);
        bone.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}