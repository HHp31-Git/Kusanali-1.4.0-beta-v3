package com.kusanali.event.special_item;

import com.kusanali.specialitem.Client;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;

@Environment(EnvType.CLIENT)
public class ClientGammaOverride {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    /** 保存玩家原来的 gamma 值 */
    private static double originalGamma = 0.0;

    /** 是否正在使用伪夜视 */
    private static boolean isActive = false;

    /** 伪夜视的目标 gamma 值 */
    private static final double MAX_GAMMA = 15.0;

    /** 标记是否需要初始化 gamma */
    private static boolean needsInit = true;

    /** 防闪烁阈值：只有当 gamma 值与目标值差距大于此值时才会调整 */
    private static final double FLICKER_THRESHOLD = 0.1;

    /** 上一次设置的 gamma 值 */
    private static double lastSetGamma = -1;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            boolean hasHelmet = player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof Client;

            if (hasHelmet && !isActive) {
                // 戴上头盔：保存原始值，立即设置到最大值
                originalGamma = client.options.getGamma().getValue();
                client.options.getGamma().setValue(MAX_GAMMA);
                lastSetGamma = MAX_GAMMA;
                isActive = true;
                needsInit = false;
            } else if (!hasHelmet && isActive) {
                // 摘下头盔：恢复原始值
                client.options.getGamma().setValue(originalGamma);
                lastSetGamma = originalGamma;
                isActive = false;
                needsInit = true;
            }

            // 如果处于激活状态，仅在 gamma 偏离时修正
            if (isActive) {
                double currentGamma = client.options.getGamma().getValue();
                // 只有当 gamma 明显偏离目标时才修正，防止闪烁
                if (Math.abs(currentGamma - MAX_GAMMA) > FLICKER_THRESHOLD) {
                    client.options.getGamma().setValue(MAX_GAMMA);
                    lastSetGamma = MAX_GAMMA;
                }
            }
        });
    }

    /**
     * 强制设置 gamma 值（用于 V 键开关同步）
     */
    public static void forceSetGamma(double value) {
        CLIENT.options.getGamma().setValue(value);
        lastSetGamma = value;
    }

    /**
     * 获取当前 gamma 值
     */
    public static double getGamma() {
        return CLIENT.options.getGamma().getValue();
    }

    /**
     * 重置 gamma 到默认值
     */
    public static void resetGamma() {
        CLIENT.options.getGamma().setValue(0.0);
        lastSetGamma = 0.0;
        isActive = false;
    }
}
