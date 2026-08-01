package com.kusanali.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class ClientVisionOverlay {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    // ---------- 开关与过渡
    private static boolean enabled = false;

    // 淡入淡出
    private static float visionAlpha = 0.0F;
    private static final float FADE_SPEED = 0.05F;

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
        ctx.fill(0, 0, w, h, blendTint(0.2F * a));

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
        float centerX;

        Particle() {
        }

        void respawn(int w, int h) {
            centerX = w / 2.0f;
            // 粒子仅在左右两侧生成
            x = RAND.nextBoolean() ? 0 : w - 1;
            y = RAND.nextInt(h);
            maxLife = 60 + RAND.nextInt(100);
            life = RAND.nextInt(maxLife);
            alpha = RAND.nextFloat();
            updateSize();
        }

        void update(int w, int h) {
            y -= 1;
            life++;
            if (life > maxLife || y < -10) {
                respawn(w, h);
            }
            alpha = 0.3F + 0.7F * MathHelper.sin(life * 0.1F);
            updateSize();
        }

        private void updateSize() {
            // 计算与中心的距离比例（0-1）
            float distanceFromCenter = Math.abs(x - centerX) / centerX;
            // 距离中心越近，粒子越小（1-3像素）
            size = 1 + (int)(distanceFromCenter * 2);
        }
    }

    private static int argb(int rgb, int a) {
        return (MathHelper.clamp(a, 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static int blendTint(float a) {
        return argb(TINT, (int) (a * 255));
    }
}
