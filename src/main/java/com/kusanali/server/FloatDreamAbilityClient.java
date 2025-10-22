package com.kusanali.server;

import com.kusanali.register.ModItems;
import com.kusanali.register.ModKeySet;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;


public class FloatDreamAbilityClient implements HudRenderCallback {
    public static long rCooldownEnd = 0;
    public static long gCooldownEnd = 0;

    public static void register() {
        HudRenderCallback.EVENT.register(new FloatDreamAbilityClient());

        // 监听按键事件
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeySet.ACTIVATE_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    ClientPlayNetworking.send(new Identifier("kusanali", "activate_r_ability"),
                            PacketByteBufs.create());
                }
            }

            if (ModKeySet.E_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    ClientPlayNetworking.send(new Identifier("kusanali", "activate_g_ability"),
                            PacketByteBufs.create());
                }
            }
        });

        // 接收服务器冷却同步
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "r_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> rCooldownEnd = serverCooldownEnd);
                });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "g_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> gCooldownEnd = serverCooldownEnd);
                });
    }

    private static boolean isFloatDreamHeld(net.minecraft.entity.player.PlayerEntity player) {
        return player != null && player.getMainHandStack().getItem() == ModItems.FLOAT_DREAM;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!isFloatDreamHeld(client.player)) {
            return; // 如果没有手持float_dream，直接返回不渲染
        }

        TextRenderer textRenderer = client.textRenderer;
        long currentTime = System.currentTimeMillis();

        int windowWidth = client.getWindow().getScaledWidth();
        int lineHeight = textRenderer.fontHeight + 2;

        // 渲染G键技能冷却（在上方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, lineHeight,
                "所识遍记 ", gCooldownEnd, currentTime);

        // 渲染R键技能冷却（在下方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, lineHeight * 2,
                "心景幻成 ", rCooldownEnd, currentTime);
    }

    private void renderCooldownHud(DrawContext drawContext, TextRenderer textRenderer,
                                   int windowWidth, int y, String prefix,
                                   long cooldownEnd, long currentTime) {
        String text;
        int color;

        if (cooldownEnd == 0 || currentTime >= cooldownEnd) {
            text = prefix + "充能完成";
            color = 0x00FF00; // 绿色
        } else {
            long remaining = cooldownEnd - currentTime;
            long totalCooldown = prefix.equals("所识遍记") ? 11000 : 30000;
            int percent = (int) (remaining * 100 / totalCooldown);
            text = String.format("%s%d%%", prefix, percent);
            color = 0xFFFF00; // 黄色
        }

        int x = windowWidth - textRenderer.getWidth(text) - 10;
        drawContext.drawTextWithShadow(textRenderer, text, x, y, color);
    }
}
