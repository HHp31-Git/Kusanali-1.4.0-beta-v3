package com.kusanali.server;

import com.kusanali.register.ModItems;
import com.kusanali.register.ModKeySet;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class FloatDreamHud implements HudRenderCallback {
    public static int cooldown = 0; // 客户端冷却计时（刻）
    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        if (cooldown > 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer textRenderer = client.textRenderer;

            // 在屏幕右下角显示冷却时间
            String text = "冷却: " + (cooldown / 20) + "s";
            int x = client.getWindow().getScaledWidth() - textRenderer.getWidth(text) - 10;
            int y = client.getWindow().getScaledHeight() - 20;

            drawContext.drawTextWithShadow(textRenderer, text, x, y, 0xFFFFFF);

            // 更新客户端冷却（每帧减少）
            cooldown--;
        }
    }
    public static void register() {
        // 注册HUD渲染
        HudRenderCallback.EVENT.register(new FloatDreamHud());

        // 监听按键事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeySet.ACTIVATE_FLOAT_DREAM.wasPressed()) {
                // 检查玩家是否手持float_dream物品
                if (client.player != null && client.player.getMainHandStack().getItem() == ModItems.FLOAT_DREAM) {
                    // 发送激活请求到服务器
                    ClientPlayNetworking.send(new Identifier("kusanali", "activate_float_dream"),
                            PacketByteBufs.create());
                }
            }
        });
    }
}
