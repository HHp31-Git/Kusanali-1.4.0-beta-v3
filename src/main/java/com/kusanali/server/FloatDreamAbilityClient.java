package com.kusanali.server;

import com.kusanali.register.ModItems;
import com.kusanali.register.ModKeySet;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class FloatDreamAbilityClient implements HudRenderCallback {

    // ==================== 冷却状态变量 ====================
    public static long rCooldownEnd = 0;
    public static long gCooldownEnd = 0;
    public static long rCooldownStart = 0;
    public static long gCooldownStart = 0;

    public static void register() {
        // 注册 HUD 渲染
        HudRenderCallback.EVENT.register(new FloatDreamAbilityClient());

        // 注册客户端 Tick 事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeySet.ACTIVATE_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    ClientPlayNetworking.send(
                            new Identifier("kusanali", "activate_r_ability"),
                            new PacketByteBuf(Unpooled.buffer())
                    );
                }
            }

            if (ModKeySet.E_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    ClientPlayNetworking.send(
                            new Identifier("kusanali", "activate_g_ability"),
                            new PacketByteBuf(Unpooled.buffer())
                    );
                }
            }
        });

        // ==================== 接收冷却同步 ====================

        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("kusanali", "r_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> {
                        rCooldownEnd = serverCooldownEnd;
                        rCooldownStart = System.currentTimeMillis();
                    });
                });

        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("kusanali", "g_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> {
                        gCooldownEnd = serverCooldownEnd;
                        gCooldownStart = System.currentTimeMillis();
                    });
                });

        // ==================== 断开连接时重置 ====================
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            rCooldownEnd = 0;
            gCooldownEnd = 0;
            rCooldownStart = 0;
            gCooldownStart = 0;
        });
    }

    private static boolean isFloatDreamHeld(net.minecraft.entity.player.PlayerEntity player) {
        if (player == null) return false;
        return player.getMainHandStack().getItem() == ModItems.FLOAT_DREAM
                || player.getOffHandStack().getItem() == ModItems.FLOAT_DREAM;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!isFloatDreamHeld(client.player)) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        long currentTime = System.currentTimeMillis();
        int windowWidth = client.getWindow().getScaledWidth();

        int baseY = 10;
        int lineHeight = textRenderer.fontHeight + 4;

        // 防御性计算总冷却时长
        long gTotal = Math.max(gCooldownEnd - gCooldownStart, 1);
        long rTotal = Math.max(rCooldownEnd - rCooldownStart, 1);

        // 渲染 G 技能（上方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY,
                "所识遍记 ", gCooldownEnd, currentTime, gTotal);

        // 渲染 R 技能（下方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY + lineHeight,
                "心景幻成 ", rCooldownEnd, currentTime, rTotal);
    }

    /**
     * 绘制单个技能的冷却 HUD（百分比显示）
     */
    private void renderCooldownHud(DrawContext drawContext, TextRenderer textRenderer,
                                   int windowWidth, int y, String prefix,
                                   long cooldownEnd, long cooldownStart, long currentTime) {
        String text;
        int color;

        if (cooldownEnd == 0 || currentTime >= cooldownEnd) {
            // 充能完成
            text = prefix + "100%";
            color = 0x00FF00;
        } else {
            long totalCooldown = cooldownEnd - cooldownStart;
            if (totalCooldown <= 0) {
                text = prefix + "100%";
                color = 0x00FF00;
            } else {
                long elapsed = currentTime - cooldownStart;
                int percent = (int) (elapsed * 100 / totalCooldown);
                percent = Math.min(percent, 99);
                text = String.format("%s%d%%", prefix, percent);
                color = 0xFFFF00;
            }
        }

        int x = windowWidth - textRenderer.getWidth(text) - 10;
        drawContext.drawTextWithShadow(textRenderer, text, x, y, color);
    }
}

