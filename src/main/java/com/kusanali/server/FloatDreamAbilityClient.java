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
     *
     * @param drawContext   绘图上下文
     * @param textRenderer  文本渲染器
     * @param windowWidth   窗口宽度，用于右对齐
     * @param y             当前渲染的 Y 坐标
     * @param prefix        技能名称前缀（如 "所识遍记 "）
     * @param cooldownEnd   冷却结束的时间戳
     * @param currentTime   当前时间戳
     * @param totalCooldown 该次技能释放的总冷却时长（毫秒）
     */
    private void renderCooldownHud(DrawContext drawContext, TextRenderer textRenderer,
                                   int windowWidth, int y, String prefix,
                                   long cooldownEnd, long currentTime,
                                   long totalCooldown) {
        String text;
        int color;

        if (cooldownEnd == 0 || currentTime >= cooldownEnd) {
            // 充能完成：显示 100%
            text = prefix + "100%";
            color = 0x00FF00; // 绿色
        } else {
            // 计算已充能的时间
            long elapsed = currentTime - rCooldownStart;

            // 计算充能进度百分比（已充能的比例）
            int percent = Math.min((int) (elapsed * 100 / totalCooldown), 99);

            text = String.format("%s%d%%", prefix, percent);
            color = 0xFFFF00; // 黄色
        }

        int x = windowWidth - textRenderer.getWidth(text) - 10;
        drawContext.drawTextWithShadow(textRenderer, text, x, y, color);
    }
}

