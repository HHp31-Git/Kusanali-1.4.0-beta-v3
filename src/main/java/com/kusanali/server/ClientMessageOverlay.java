package com.kusanali.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ClientMessageOverlay {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    // ============================================================
    //                    动画常量
    // ============================================================

    private static final long FADE_IN_DURATION = 500;
    private static final long HOLD_DURATION = 200;
    private static final long FADE_OUT_DURATION = 500;

    // ============================================================
    //                    颜色常量
    // ============================================================

    /** 淡绿半透明背景 */
    private static final int BG_COLOR = 0x0A2218;

    /** 主绿边缘（亮但柔和） */
    private static final int EDGE_COLOR = 0x55FF66;

    /** 暗绿边缘（立体感） */
    private static final int EDGE_DARK = 0x228844;

    /** 外层柔和绿光晕 */
    private static final int GLOW_COLOR = 0x22FF66;

    /** 四角十字装饰（亮绿） */
    private static final int CROSS_COLOR = 0x66FF77;

    /** 虚线/点线边饰 */
    private static final int DOTTED_COLOR = 0x44EE66;

    /** 文字草绿色 */
    private static final int TEXT_COLOR = 0x88FF44;

    // ============================================================
    //                    动画状态
    // ============================================================

    private enum FadePhase {
        NONE,
        FADE_IN,
        HOLD,
        FADE_OUT
    }

    private static FadePhase fadePhase = FadePhase.NONE;
    private static long phaseStartTime = 0;
    private static String currentMessage = "";

    // ============================================================
    //                    布局常量
    // ============================================================

    /** 左边距 */
    private static final int MARGIN_LEFT = 6;

    /** 上边距（左上角） */
    private static final int MARGIN_TOP = 10;

    /** 内边距 */
    private static final int PAD = 2;

    /** 阴影偏移 */
    private static final int SHADOW_OFF = 1;

    /** 十字装饰臂长 */
    private static final int CROSS_ARM = 3;

    /** 十字装饰线宽 */
    private static final int CROSS_W = 1;

    /** 外发光厚度 */
    private static final int GLOW_T = 1;

    /** 虚线步长 */
    private static final int DOTTED_STEP = 3;

    // ============================================================
    //                    注册入口
    // ============================================================

    public static void register() {
        HudRenderCallback.EVENT.register(ClientMessageOverlay::renderMessage);
    }

    /**
     * 触发消息显示
     *
     * @param text 支持 Minecraft 颜色代码
     */
    public static void trigger(String text) {
        currentMessage = text;
        fadePhase = FadePhase.FADE_IN;
        phaseStartTime = System.currentTimeMillis();
    }

    // ============================================================
    //                    渲染方法
    // ============================================================

    private static void renderMessage(DrawContext context, float tickDelta) {
        if (fadePhase == FadePhase.NONE) return;

        long now = System.currentTimeMillis();
        long elapsed = now - phaseStartTime;

        float alpha = 0.0F;

        switch (fadePhase) {
            case FADE_IN:
                alpha = (float) Math.min((double) elapsed / FADE_IN_DURATION, 1.0);
                if (elapsed >= FADE_IN_DURATION) {
                    alpha = 1.0F;
                    fadePhase = FadePhase.HOLD;
                    phaseStartTime = now;
                }
                break;

            case HOLD:
                alpha = 1.0F;
                if (elapsed >= HOLD_DURATION) {
                    fadePhase = FadePhase.FADE_OUT;
                    phaseStartTime = now;
                }
                break;

            case FADE_OUT:
                alpha = (float) Math.max(1.0 - (double) elapsed / FADE_OUT_DURATION, 0.0);
                if (elapsed >= FADE_OUT_DURATION) {
                    alpha = 0.0F;
                    fadePhase = FadePhase.NONE;
                }
                break;

            default:
                break;
        }

        if (alpha <= 0.0F) return;

        TextRenderer textRenderer = CLIENT.textRenderer;

        String text = currentMessage;
        int textWidth = textRenderer.getWidth(text);

        // 左上角位置
        int x = MARGIN_LEFT;
        int y = MARGIN_TOP;

        // 各层 alpha
        int bgAlpha = MathHelper.clamp((int) (alpha * 90), 0, 255);
        int edgeAlpha = MathHelper.clamp((int) (alpha * 180), 0, 255);
        int edgeDarkAlpha = MathHelper.clamp((int) (alpha * 130), 0, 255);
        int glowAlpha = MathHelper.clamp((int) (alpha * 100), 0, 255);
        int dottedAlpha = MathHelper.clamp((int) (alpha * 150), 0, 255);
        int shadowAlpha = MathHelper.clamp((int) (alpha * 50), 0, 255);

        int x0 = x - PAD;
        int y0 = y - PAD;
        int x1 = x + textWidth + PAD;
        int y1 = y + textRenderer.fontHeight + PAD;

        // ---- 微弱阴影层 ----
        context.fill(
                x0 + SHADOW_OFF, y0 + SHADOW_OFF,
                x1 + SHADOW_OFF, y1 + SHADOW_OFF,
                (shadowAlpha << 24)
        );

        // ---- 淡绿半透明背景 ----
        context.fill(x0, y0, x1, y1, (bgAlpha << 24) | BG_COLOR);

        // ---- 细绿边缘（上、左用亮绿） ----
        int edgeColor = (edgeAlpha << 24) | EDGE_COLOR;
        context.fill(x0, y0, x1, y0 + 1, edgeColor);       // 上
        context.fill(x0, y0, x0 + 1, y1, edgeColor);       // 左

        // ---- 细绿边缘（下、右用暗绿） ----
        int edgeDarkColor = (edgeDarkAlpha << 24) | EDGE_DARK;
        context.fill(x0, y1 - 1, x1, y1, edgeDarkColor);   // 下
        context.fill(x1 - 1, y0, x1, y1, edgeDarkColor);   // 右

        // ---- 外层柔和绿光晕 ----
        int glowColor = (glowAlpha << 24) | GLOW_COLOR;
        context.fill(x0 - GLOW_T, y0 - GLOW_T - 1, x1 + GLOW_T, y0 - GLOW_T, glowColor);        // 上
        context.fill(x0 - GLOW_T, y1 + GLOW_T, x1 + GLOW_T, y1 + GLOW_T + 1, glowColor);      // 下
        context.fill(x0 - GLOW_T - 1, y0 - GLOW_T, x0 - GLOW_T, y1 + GLOW_T, glowColor);      // 左
        context.fill(x1 + GLOW_T, y0 - GLOW_T, x1 + GLOW_T + 1, y1 + GLOW_T, glowColor);      // 右

        // ---- 虚线/点线边饰 ----
        int dottedColor = (dottedAlpha << 24) | DOTTED_COLOR;
        drawDottedLineH(context, x0 + 4, y0 - 2, x1 - 4, dottedColor);   // 上虚线
        drawDottedLineH(context, x0 + 4, y1 + 1, x1 - 4, dottedColor);   // 下虚线
        drawDottedLineV(context, x0 - 2, y0 + 4, y1 - 4, dottedColor);   // 左虚线
        drawDottedLineV(context, x1 + 1, y0 + 4, y1 - 4, dottedColor);   // 右虚线

        // ---- 四角十字装饰 ----
        int cc = ((int) (alpha * 200) << 24) | CROSS_COLOR;

        // 左上
        drawCross(context, x0, y0, cc);
        // 右上
        drawCross(context, x1 - 1, y0, cc);
        // 左下
        drawCross(context, x0, y1 - 1, cc);
        // 右下
        drawCross(context, x1 - 1, y1 - 1, cc);

        // ---- 文字（草绿色，带 alpha） ----
        int textColor = applyAlpha(alpha);
        context.drawText(textRenderer, text, x, y, textColor, false);
    }

    // ============================================================
    //                    辅助绘制方法
    // ============================================================

    /**
     * 绘制四角十字装饰
     */
    private static void drawCross(DrawContext context, int cx, int cy, int color) {
        context.fill(cx - CROSS_ARM, cy - CROSS_W + 1, cx + CROSS_ARM + 1, cy + 1, color);
        context.fill(cx - CROSS_W + 1, cy - CROSS_ARM, cx + 1, cy + CROSS_ARM + 1, color);
    }

    /**
     * 绘制水平虚线
     */
    private static void drawDottedLineH(DrawContext context, int x0, int y, int x1, int color) {
        for (int i = x0; i < x1; i += DOTTED_STEP) {
            context.fill(i, y, i + 1, y + 1, color);
        }
    }

    /**
     * 绘制垂直虚线
     */
    private static void drawDottedLineV(DrawContext context, int x, int y0, int y1, int color) {
        for (int i = y0; i < y1; i += DOTTED_STEP) {
            context.fill(x, i, x + 1, i + 1, color);
        }
    }

    /**
     * 将 0~1 的 alpha 转为 ARGB 颜色
     */
    private static int applyAlpha(float alpha) {
        int a = MathHelper.clamp((int) (alpha * 255), 0, 255);
        return (a << 24) | (ClientMessageOverlay.TEXT_COLOR & 0xFFFFFF);
    }
}

