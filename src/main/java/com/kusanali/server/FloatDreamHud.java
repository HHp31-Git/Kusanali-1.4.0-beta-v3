package com.kusanali.server;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class FloatDreamHud implements HudRenderCallback {
    public static long rCooldownEndTime = 0;  // R键冷却结束时间
    public static long eCooldownEndTime = 0;  // E键冷却结束时间
    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        long currentTime = System.currentTimeMillis();

        // 显示R键冷却
        if (currentTime < rCooldownEndTime) {
            long remainingSeconds = (rCooldownEndTime - currentTime) / 1000 + 1;
            String text = "心景幻成 " + remainingSeconds + "s";
            int x = client.getWindow().getScaledWidth() - textRenderer.getWidth(text) - 10;
            int y = client.getWindow().getScaledHeight() - 20;
            drawContext.drawTextWithShadow(textRenderer, text, x, y, 0x55FF55);
        }

        // 显示E键冷却
        if (currentTime < eCooldownEndTime) {
            long remainingSeconds = (eCooldownEndTime - currentTime) / 1000 + 1;
            String text = "所识遍记: " + remainingSeconds + "s";
            int x = client.getWindow().getScaledWidth() - textRenderer.getWidth(text) - 10;
            int y = client.getWindow().getScaledHeight() - 50;
            drawContext.drawTextWithShadow(textRenderer, text, x, y, 0xFFFFFF);
        }
    }
}
