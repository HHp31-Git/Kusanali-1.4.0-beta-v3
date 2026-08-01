package com.kusanali.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kusanali.Kusanali.MOD_ID;

@Environment(EnvType.CLIENT)
public class ClientVisionOverlay {

    private static final Identifier TOP_BORDER_TEXTURE =
            new Identifier(MOD_ID, "textures/gui/client_top.png");
    private static final Identifier DOWN_BORDER_TEXTURE =
            new Identifier(MOD_ID, "textures/gui/client_down.png");

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    // ---------- 开关与过渡
    private static boolean enabled = false;

    // 淡入淡出
    private static float visionAlpha = 0.0F;
    private static final float FADE_SPEED = 0.0278F;

    // 颜色（ARGB）
    private static final int TINT = 0x0A1A18;
    private static final int GLOW = 0x22FF66;
    private static final int PARTICLE = 0x66FF88;


    // 粒子
    private static final Random RAND = new Random();
    private static final List<Particle> PARTICLES = new ArrayList<>();
    private static final int MAX_PARTICLES = 30;

    public static void init() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            PARTICLES.add(new Particle());
            PARTICLES.get(i).respawn(CLIENT.getWindow().getScaledWidth(), CLIENT.getWindow().getScaledHeight());
        }
    }

    public static void setEnabled(boolean b) {
        enabled = b;
    }

    public static void register() {
        HudRenderCallback.EVENT.register(ClientVisionOverlay::render);
    }

    private static void render(DrawContext ctx, float tickDelta) {
        // 淡入淡出
        if (enabled && visionAlpha < 1.0F) visionAlpha = Math.min(1.0F, visionAlpha + FADE_SPEED);
        if (!enabled && visionAlpha > 0.0F) visionAlpha = Math.max(0.0F, visionAlpha - FADE_SPEED);
        if (visionAlpha <= 0.0F) return;

        int w = CLIENT.getWindow().getScaledWidth();
        int h = CLIENT.getWindow().getScaledHeight();
        float a = visionAlpha;
        System.currentTimeMillis();

        // 全局染屏（深青绿，半透明）
        ctx.fill(0, 0, w, h, blendTint(0.1F * a));

        // 左上角渐变
        for (int i = 0; i < 5; i++) {
            int alpha = (int)(a * 100 * (1 - i * 0.2f));
            ctx.fill(0, i, i + 1, i + 1, argb(GLOW, alpha));
        }

        // 右上角渐变
        for (int i = 0; i < 5; i++) {
            int alpha = (int)(a * 100 * (1 - i * 0.2f));
            ctx.fill(w - i - 1, i, w - i, i + 1, argb(GLOW, alpha));
        }

        // 左下角渐变
        for (int i = 0; i < 5; i++) {
            int alpha = (int)(a * 100 * (1 - i * 0.2f));
            ctx.fill(0, h - i - 1, i + 1, h - i, argb(GLOW, alpha));
        }

        // 右下角渐变
        for (int i = 0; i < 5; i++) {
            int alpha = (int)(a * 100 * (1 - i * 0.2f));
            ctx.fill(w - i - 1, h - i - 1, w - i, h - i, argb(GLOW, alpha));
        }

        // 漂浮孢子
        for (Particle p : PARTICLES) {
            p.update(w, h);  // 传递屏幕宽度
            int pa = (int) (a * p.alpha * 200);
            ctx.fill(p.x, p.y, p.x + p.size, p.y + p.size, argb(PARTICLE, pa));
        }
    }


    // 粒子
    private static class Particle {
        int x, y, size, life, maxLife;
        float alpha;

        void respawn(int w, int h) {
            // 计算中央区域边界
            int centerX1 = w / 4;
            int centerX2 = 3 * w / 4;

            // 只在左右边缘生成
            boolean isLeftSide = RAND.nextBoolean();
            if (isLeftSide) {
                x = RAND.nextInt(centerX1);
            } else {
                x = centerX2 + RAND.nextInt(w - centerX2);
            }

            // y 位置随机分布在整个屏幕高度
            y = RAND.nextInt(h);

            // 调整生命周期
            maxLife = 60 + RAND.nextInt(100);
            life = RAND.nextInt(maxLife);
            alpha = RAND.nextFloat();
            updateSize(w);
        }


        void update(int w, int h) {
            // 保持向下移动
            y -= 1;
            life++;

            // 重生条件
            if (life > maxLife || y < -10) {
                respawn(w, h);
            }

            // 透明度变化
            alpha = 0.3F + 0.7F * MathHelper.sin(life * 0.1F);
            updateSize(w);
        }



        private void updateSize(int w) {
            // 计算到边缘的距离
            float distToLeftEdge = x;
            float distToRightEdge = w - x;
            float minDistToEdge = Math.min(distToLeftEdge, distToRightEdge);

            // 根据距离调整大小
            float sizeRatio = 1.0f - (minDistToEdge / (w / 2.0f));
            size = 1 + (int)(sizeRatio * 2);
            size = MathHelper.clamp(size, 1, 3);
        }


    }


    private static int argb(int rgb, int a) {
        return (MathHelper.clamp(a, 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static int blendTint(float a) {
        return argb(TINT, (int) (a * 255));
    }
}
