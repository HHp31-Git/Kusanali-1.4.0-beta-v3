package com.kusanali.server;

import com.kusanali.register.ModKeySet;
import com.kusanali.specialitem.Client;
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
    /** 伪夜视目标 gamma 值 */
    private static final double GAMMA_MAX = 15.0;

    /** 默认 gamma 值 */
    private static final double GAMMA_DEFAULT = 0.0;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    /** HUD 显示开关 */
    private static boolean hudEnabled = true;

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
    private static final long GAMMA_TRANSITION_DURATION = 600; // 0.6 秒

    /** 是否正在过渡 */
    private static boolean gammaTransitioning = false;

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

        if (isVPressed && !wasVPressed) {
            hudEnabled = !hudEnabled;

            ClientPlayerEntity player = CLIENT.player;
            if (player != null && isWearingHelmet(player)) {
                if (!hudEnabled) {
                    // 关闭 HUD → 关闭伪夜视
                    setGammaDirectly(GAMMA_DEFAULT);
                    gammaExternallyDisabled = true;
                    gammaActive = false;
                } else {
                    // 开启 HUD → 恢复伪夜视
                    setGammaDirectly(GAMMA_MAX);
                    gammaExternallyDisabled = false;
                    gammaActive = true;
                }
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
        TextRenderer textRenderer = CLIENT.textRenderer;
        int screenW = CLIENT.getWindow().getScaledWidth();
        int screenH = CLIENT.getWindow().getScaledHeight();

        String status = hudEnabled ? "§aHUD: ON [V]" : "§cHUD: OFF [V]";
        int textWidth = textRenderer.getWidth(status);

        int indicatorX = screenW - textWidth - 10;
        int indicatorY = screenH - 36;

        context.fill(indicatorX - 6, indicatorY - 3,
                indicatorX + textWidth + 6, indicatorY + 14, 0x88000000);
        context.drawText(textRenderer, status, indicatorX, indicatorY,
                hudEnabled ? 0x00FF88 : 0xFF4444, false);
    }
    // ============================================================
    //                      工具方法
    // ============================================================

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

        // 直接写入 gamma（不走 setGammaDirectly 的备份逻辑）
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
}