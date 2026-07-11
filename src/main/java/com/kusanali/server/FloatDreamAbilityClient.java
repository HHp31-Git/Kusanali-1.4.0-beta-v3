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

    // ==================== 冷却状态变量 ====================
    // 记录技能冷却结束的绝对时间戳（毫秒），由服务端同步过来
    public static long rCooldownEnd = 0;
    public static long gCooldownEnd = 0;

    // 记录技能释放时的基准时间戳（毫秒），用于动态计算总冷却时长
    // 解决服务端动态修改冷却时间（如G技能采集花朵缩短冷却）导致客户端进度条百分比错位的问题
    public static long rCooldownStart = 0;
    public static long gCooldownStart = 0;

    /**
     * 注册客户端事件监听器
     * 包含：按键触发检测、服务端冷却时间同步接收
     */
    public static void register() {
        // 注册 HUD 渲染事件，用于在屏幕上绘制冷却UI
        HudRenderCallback.EVENT.register(new FloatDreamAbilityClient());

        // 注册客户端 Tick 事件，每帧检测按键输入
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 检测 R 键（心景幻成）是否按下
            if (ModKeySet.ACTIVATE_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    // 向服务端发送激活 R 技能的空数据包
                    ClientPlayNetworking.send(new Identifier("kusanali", "activate_r_ability"), PacketByteBufs.create());
                }
            }

            // 检测 G 键（所识遍记）是否按下
            if (ModKeySet.E_FLOAT_DREAM.wasPressed()) {
                if (isFloatDreamHeld(client.player)) {
                    // 向服务端发送激活 G 技能的空数据包
                    ClientPlayNetworking.send(new Identifier("kusanali", "activate_g_ability"), PacketByteBufs.create());
                }
            }
        });

        // ==================== 接收服务端冷却同步网络包 ====================

        // 接收 R 技能冷却更新
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "r_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong(); // 读取服务端发来的结束时间戳
                    client.execute(() -> {
                        rCooldownEnd = serverCooldownEnd;
                        rCooldownStart = System.currentTimeMillis(); // 记录收到包的当前时间作为起始时间
                    });
                });

        // 接收 G 技能冷却更新
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "g_cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    long serverCooldownEnd = buf.readLong();
                    client.execute(() -> {
                        gCooldownEnd = serverCooldownEnd;
                        gCooldownStart = System.currentTimeMillis(); // 记录收到包的当前时间作为起始时间
                    });
                });
    }

    /**
     * 检测玩家当前是否手持 FloatDream 物品
     * 支持主手或副手持有
     */
    private static boolean isFloatDreamHeld(net.minecraft.entity.player.PlayerEntity player) {
        if (player == null) return false;
        // 同时检查主手和副手，实现双手可用功能
        return player.getMainHandStack().getItem() == ModItems.FLOAT_DREAM
                || player.getOffHandStack().getItem() == ModItems.FLOAT_DREAM;
    }

    /**
     * HUD 渲染逻辑
     * 当手持 FloatDream 时，在屏幕右上角渲染技能冷却状态
     */
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 如果玩家未持有该物品，则不渲染 HUD
        if (!isFloatDreamHeld(client.player)) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        long currentTime = System.currentTimeMillis();
        int windowWidth = client.getWindow().getScaledWidth();

        int baseY = 10; // 距离屏幕顶部 10 像素，避免被 F3 调试信息遮挡
        int lineHeight = textRenderer.fontHeight + 4; // 行间距额外增加 4 像素

        // 动态计算总冷却时长：结束时间 - 开始时间
        // 这样无论服务端将 G 技能冷却设为 11秒、5秒 还是 3秒，客户端进度条都能精准适配
        long gTotal = gCooldownEnd - gCooldownStart;
        long rTotal = rCooldownEnd - rCooldownStart;

        // 渲染 G 键技能冷却（在上方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY,
                "所识遍记 ", gCooldownEnd, currentTime, gTotal);

        // 渲染 R 键技能冷却（在下方）
        renderCooldownHud(drawContext, textRenderer, windowWidth, baseY + lineHeight,
                "心景幻成 ", rCooldownEnd, currentTime, rTotal);
    }

    /**
     * 绘制单个技能的冷却 HUD
     *
     * @param drawContext  绘图上下文
     * @param textRenderer 文本渲染器
     * @param windowWidth  窗口宽度，用于右对齐
     * @param y            当前渲染的 Y 坐标
     * @param prefix       技能名称前缀（如 "所识遍记 "）
     * @param cooldownEnd  冷却结束的时间戳
     * @param currentTime  当前时间戳
     * @param totalCooldown 该次技能释放的总冷却时长（毫秒）
     */
    private void renderCooldownHud(DrawContext drawContext, TextRenderer textRenderer,
                                   int windowWidth, int y, String prefix,
                                   long cooldownEnd, long currentTime, long totalCooldown) {
        String text;
        int color;

        // 判断冷却是否已完成
        if (cooldownEnd == 0 || currentTime >= cooldownEnd) {
            text = prefix + "充能完成";
            color = 0x00FF00; // 充能完成显示绿色
        } else {
            long remaining = cooldownEnd - currentTime; // 剩余冷却时间

            // 防御性编程：防止除零异常，并确保进度百分比不超过 100%
            int percent = totalCooldown > 0 ? Math.min((int) (remaining * 100 / totalCooldown), 100) : 0;

            text = String.format("%s%d%%", prefix, percent);
            color = 0xFFFF00; // 冷却中显示黄色
        }

        // 计算右对齐的 X 坐标（距离屏幕右侧边缘 10 像素）
        int x = windowWidth - textRenderer.getWidth(text) - 10;

        // 绘制带阴影的文本，增强在复杂背景下的可读性
        drawContext.drawTextWithShadow(textRenderer, text, x, y, color);
    }
}

