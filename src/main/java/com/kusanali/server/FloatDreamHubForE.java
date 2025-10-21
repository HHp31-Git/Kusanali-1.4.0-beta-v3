package com.kusanali.server;

import com.kusanali.register.ModKeySet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class FloatDreamHubForE implements HudRenderCallback {
    public static long cooldownEndTime = 0;
    public static void register(){
        // 注册HUD渲染
        HudRenderCallback.EVENT.register(new FloatDreamHubForE());

        // 监听按键事件
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeySet.E_FLOAT_DREAM.wasPressed()) {
                // 发送激活请求到服务器
                ClientPlayNetworking.send(Identifier.of("kusanali", "activate_ability"),
                        PacketByteBufs.create());
            }
        });

        // 接收冷却时间同步
        ClientPlayNetworking.registerGlobalReceiver(Identifier.of("kusanali", "cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> cooldownEndTime = serverCooldownEnd);
                });
    }
    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        if (cooldownEndTime == 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        long currentTime = System.currentTimeMillis();

        if (currentTime < cooldownEndTime) {
            // 计算剩余冷却时间（秒）
            long remainingSeconds = (cooldownEndTime - currentTime) / 1000 + 1;
            String text = "能力冷却: " + remainingSeconds + "s";

            TextRenderer textRenderer = client.textRenderer;
            int x = client.getWindow().getScaledWidth() - textRenderer.getWidth(text) - 10;
            int y = client.getWindow().getScaledHeight() - 50; // 在R键功能上方

            drawContext.drawTextWithShadow(textRenderer, text, x, y, 0xFFFFFF);
        } else {
            cooldownEndTime = 0; // 冷却结束
        }
    }
}
