package com.kusanali.server;

import com.kusanali.register.ModKeySet;
import com.kusanali.specialitem.combats.Client;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.EquipmentSlot;

@Environment(EnvType.CLIENT)
public class ClientOverlayRenderer {
    private static boolean wasWearingHelmet = false;
    /** 伪夜视目标 gamma 值 */
    private static final double GAMMA_MAX = 15.0;

    /** 默认 gamma 值 */
    private static final double GAMMA_DEFAULT = 0.0;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    /** HUD 显示开关 */
    private static boolean hudEnabled = false;

    /** 上次 V 键状态（上升沿检测） */
    private static boolean wasVPressed = false;

    /** gamma 原始值备份 */
    private static double originalGamma = 0.0;

    /** 是否正在使用伪夜视 */
    private static boolean gammaActive = false;

    /** 是否被外部强制禁用 gamma */
    private static boolean gammaExternallyDisabled = false;

    /** Gamma 过渡起始值 */
    private static double gammaStart = 0.0;

    /** Gamma 过渡目标值 */
    private static double gammaTarget = 0.0;

    /** Gamma 过渡开始时间 */
    private static long gammaTransitionStart = 0;

    /** Gamma 过渡持续时间 */
    private static final long GAMMA_TRANSITION_DURATION = 900; // 0.9 秒

    /** 是否正在过渡 */
    private static boolean gammaTransitioning = false;

    /** 内边距 */
    private static final int INDICATOR_PAD = 4;

    /** 阴影偏移 */
    private static final int INDICATOR_SHADOW_OFF = 1;

    /** 十字装饰臂长 */
    private static final int INDICATOR_CROSS_ARM = 3;

    /** 十字装饰线宽 */
    private static final int INDICATOR_CROSS_W = 1;

    /** 外发光厚度 */
    private static final int INDICATOR_GLOW_T = 1;

    /** 虚线步长 */
    private static final int INDICATOR_DOTTED_STEP = 3;

    /** 淡绿半透明背景 */
    private static final int INDICATOR_BG_COLOR = 0x0A2218;

    /** 主绿边缘 */
    private static final int INDICATOR_EDGE_COLOR = 0x55FF66;

    /** 暗绿边缘 */
    private static final int INDICATOR_EDGE_DARK = 0x228844;

    /** 外层柔和绿光晕 */
    private static final int INDICATOR_GLOW_COLOR = 0x22FF66;

    /** 十字装饰 */
    private static final int INDICATOR_CROSS_COLOR = 0x66FF77;

    /** 虚线边饰 */
    private static final int INDICATOR_DOTTED_COLOR = 0x44EE66;

    /** HUD 开启文字颜色 */
    private static final int INDICATOR_ON_COLOR = 0x88FF44;

    /** HUD 关闭文字颜色 */
    private static final int INDICATOR_OFF_COLOR = 0xFF6644;

    /** V键冷却结束时间 */
    private static long vCooldownEnd = 0;

    /** V键冷却持续时间 */
    private static final long V_COOLDOWN_DURATION = 600;

    public static void register() {
        // 注册 HUD 渲染
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ClientPlayerEntity player = CLIENT.player;
            if (player == null) return;
            handleVKeyPress();
            if (!hudEnabled) {
                renderToggleIndicator(drawContext);
                return;
            }
            isWearingHelmet(player);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            updateGammaTransition();
            if (gammaExternallyDisabled) return;
            boolean hasHelmet = isWearingHelmet(player);
            if (hasHelmet && !gammaActive) {
                originalGamma = client.options.getGamma().getValue();
                setGammaDirectly(GAMMA_MAX);
                gammaActive = true;
            } else if (!hasHelmet && gammaActive) {
                setGammaDirectly(originalGamma);
                gammaActive = false;
            }
        });
    }

    /**
     * 检测 V 键上升沿，切换 HUD 并联动 Gamma
     */
    private static void handleVKeyPress() {
        boolean isVPressed = ModKeySet.V_ABILITY.isPressed();
        ClientPlayerEntity player = CLIENT.player;
        long currentTime = System.currentTimeMillis();

        // 检测头盔穿戴状态变化
        boolean isWearing = player != null && isWearingHelmet(player);

        // 当刚戴上头盔时，重置状态
        if (isWearing && !wasWearingHelmet) {
            if (hudEnabled) {
                setGammaDirectly(GAMMA_MAX);
                gammaExternallyDisabled = false;
                gammaActive = true;
            } else {
                setGammaDirectly(GAMMA_DEFAULT);
                gammaExternallyDisabled = true;
                gammaActive = false;
            }
        }

        // 当摘下头盔时，重置伪夜视状态
        if (!isWearing && wasWearingHelmet) {
            setGammaDirectly(GAMMA_DEFAULT);
            gammaExternallyDisabled = false;
            gammaActive = false;
        }

        wasWearingHelmet = isWearing;

        // 只有佩戴头盔时才响应V键
        if (isVPressed && !wasVPressed) {
            // 检查是否佩戴头盔
            if (player == null || !isWearingHelmet(player)) {
                wasVPressed = true;
                return;
            }

            if (currentTime < vCooldownEnd) {
                wasVPressed = true;
                return;
            }

            hudEnabled = !hudEnabled;

            vCooldownEnd = currentTime + V_COOLDOWN_DURATION;

            if (hudEnabled) {
                ClientMessageOverlay.trigger("§b§l愿吾得以聆听神明的智慧之声");
            }
            ClientVisionOverlay.setEnabled(hudEnabled);

            if (hudEnabled) {
                setGammaDirectly(GAMMA_MAX);
                gammaExternallyDisabled = false;
                gammaActive = true;
            } else {
                setGammaDirectly(GAMMA_DEFAULT);
                gammaExternallyDisabled = true;
                gammaActive = false;
            }
        }

        wasVPressed = isVPressed;
    }

    /**
     * 过渡式设置 gamma（0.6 秒平滑过渡）
     */
    private static void setGammaDirectly(double value) {
        gammaStart = getCurrentGamma();
        gammaTarget = value;
        gammaTransitionStart = System.currentTimeMillis();
        gammaTransitioning = true;
    }

    /**
     * 获取当前 gamma 实际值
     */
    private static double getCurrentGamma() {
        try {
            java.lang.reflect.Field gammaField = GameOptions.class.getDeclaredField("gamma");
            gammaField.setAccessible(true);
            Object simpleOption = gammaField.get(CLIENT.options);

            java.lang.reflect.Field valueField;
            try {
                valueField = simpleOption.getClass().getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                valueField = simpleOption.getClass().getDeclaredField("internalValue");
            }
            valueField.setAccessible(true);
            return (double) valueField.get(simpleOption);
        } catch (Exception e) {
            return CLIENT.options.getGamma().getValue();
        }
    }

    /**
     * 渲染右下角 HUD 开关指示器
     */
    private static void renderToggleIndicator(DrawContext context) {
        ClientPlayerEntity player = CLIENT.player;
        if (player == null || !isWearingHelmet(player)) {
            return;
        }

        TextRenderer textRenderer = CLIENT.textRenderer;
        int screenW = CLIENT.getWindow().getScaledWidth();
        int screenH = CLIENT.getWindow().getScaledHeight();

        String status = hudEnabled ? "§aHUD: ON [V]" : "§cHUD: OFF [V]";
        int textWidth = textRenderer.getWidth(status);

        // 右下角位置
        int x = screenW - textWidth - 10;
        int y = screenH - 10;

        // 固定 alpha（常亮显示）
        int alpha = 255;

        int x0 = x - INDICATOR_PAD;
        int y0 = y - INDICATOR_PAD;
        int x1 = x + textWidth + INDICATOR_PAD;
        int y1 = y + textRenderer.fontHeight + INDICATOR_PAD;

        // ---- 微弱阴影层 ----
        context.fill(
                x0 + INDICATOR_SHADOW_OFF, y0 + INDICATOR_SHADOW_OFF,
                x1 + INDICATOR_SHADOW_OFF, y1 + INDICATOR_SHADOW_OFF,
                (alpha << 24)
        );

        // ---- 淡绿半透明背景 ----
        // 修改：使用正确的ARGB格式，设置背景alpha为128（半透明）
        context.fill(x0, y0, x1, y1, (128 << 24) | INDICATOR_BG_COLOR);

        // ---- 细绿边缘（上、左用亮绿） ----
        int edgeColor = (alpha << 24) | INDICATOR_EDGE_COLOR;
        context.fill(x0, y0, x1, y0 + 1, edgeColor);       // 上
        context.fill(x0, y0, x0 + 1, y1, edgeColor);       // 左

        // ---- 细绿边缘（下、右用暗绿） ----
        int edgeDarkColor = (alpha << 24) | INDICATOR_EDGE_DARK;
        context.fill(x0, y1 - 1, x1, y1, edgeDarkColor);   // 下
        context.fill(x1 - 1, y0, x1, y1, edgeDarkColor);   // 右

        // ---- 外层柔和绿光晕 ----
        int glowColor = (alpha << 24) | INDICATOR_GLOW_COLOR;
        context.fill(x0 - INDICATOR_GLOW_T, y0 - INDICATOR_GLOW_T - 1, x1 + INDICATOR_GLOW_T, y0 - INDICATOR_GLOW_T, glowColor);
        context.fill(x0 - INDICATOR_GLOW_T, y1 + INDICATOR_GLOW_T, x1 + INDICATOR_GLOW_T, y1 + INDICATOR_GLOW_T + 1, glowColor);
        context.fill(x0 - INDICATOR_GLOW_T - 1, y0 - INDICATOR_GLOW_T, x0 - INDICATOR_GLOW_T, y1 + INDICATOR_GLOW_T, glowColor);
        context.fill(x1 + INDICATOR_GLOW_T, y0 - INDICATOR_GLOW_T, x1 + INDICATOR_GLOW_T + 1, y1 + INDICATOR_GLOW_T, glowColor);

        // ---- 虚线/点线边饰 ----
        int dottedColor = (alpha << 24) | INDICATOR_DOTTED_COLOR;
        drawDottedLineH(context, x0 + 4, y0 - 2, x1 - 4, dottedColor);   // 上虚线
        drawDottedLineH(context, x0 + 4, y1 + 1, x1 - 4, dottedColor);   // 下虚线
        drawDottedLineV(context, x0 - 2, y0 + 4, y1 - 4, dottedColor);   // 左虚线
        drawDottedLineV(context, x1 + 1, y0 + 4, y1 - 4, dottedColor);   // 右虚线

        // ---- 四角十字装饰 ----
        int cc = (alpha << 24) | INDICATOR_CROSS_COLOR;

        // 左上
        drawCross(context, x0, y0, cc);
        // 右上
        drawCross(context, x1 - 1, y0, cc);
        // 左下
        drawCross(context, x0, y1 - 1, cc);
        // 右下
        drawCross(context, x1 - 1, y1 - 1, cc);

        // ---- 文字 ----
        int textColor = hudEnabled ? INDICATOR_ON_COLOR : INDICATOR_OFF_COLOR;
        context.drawText(textRenderer, status, x, y, textColor, false);
    }

    /**
     * 检查是否佩戴头盔
     */
    private static boolean isWearingHelmet(ClientPlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem()
                instanceof Client;
    }
    /**
     * 每帧更新 gamma 过渡
     */
    private static void updateGammaTransition() {
        if (!gammaTransitioning) return;

        long now = System.currentTimeMillis();
        long elapsed = now - gammaTransitionStart;
        double t = Math.min((double) elapsed / GAMMA_TRANSITION_DURATION, 1.0);

        // 使用 easeOutCubic 缓动，过渡更自然
        double eased = 1.0 - Math.pow(1.0 - t, 3);
        double current = gammaStart + (gammaTarget - gammaStart) * eased;

        // 直接写入 gamma
        writeGammaRaw(current);

        if (t >= 1.0) {
            gammaTransitioning = false;
        }
    }

    /**
     * 不经过过渡，直接写入 gamma 值
     */
    private static void writeGammaRaw(double value) {
        try {
            java.lang.reflect.Field gammaField = GameOptions.class.getDeclaredField("gamma");
            gammaField.setAccessible(true);
            Object simpleOption = gammaField.get(CLIENT.options);

            java.lang.reflect.Field valueField;
            try {
                valueField = simpleOption.getClass().getDeclaredField("value");
            } catch (NoSuchFieldException e) {
                valueField = simpleOption.getClass().getDeclaredField("internalValue");
            }
            valueField.setAccessible(true);
            valueField.set(simpleOption, value);
        } catch (Exception ignored) {}
    }

    /**
     * 绘制四角十字装饰
     */
    private static void drawCross(DrawContext context, int cx, int cy, int color) {
        context.fill(cx - INDICATOR_CROSS_ARM, cy - INDICATOR_CROSS_W + 1, cx + INDICATOR_CROSS_ARM + 1, cy + 1, color);
        context.fill(cx - INDICATOR_CROSS_W + 1, cy - INDICATOR_CROSS_ARM, cx + 1, cy + INDICATOR_CROSS_ARM + 1, color);
    }

    /**
     * 绘制水平虚线
     */
    private static void drawDottedLineH(DrawContext context, int x0, int y, int x1, int color) {
        for (int i = x0; i < x1; i += INDICATOR_DOTTED_STEP) {
            context.fill(i, y, i + 1, y + 1, color);
        }
    }


    /**
     * 绘制垂直虚线
     */
    private static void drawDottedLineV(DrawContext context, int x, int y0, int y1, int color) {
        for (int i = y0; i < y1; i += INDICATOR_DOTTED_STEP) {
            context.fill(x, i, x + 1, i + 1, color);
        }
    }
}
