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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientOverlayRenderer {

    // ============================================================
    //                        常量配置
    // ============================================================

    /** 最大显示距离（格） */
    private static final double MAX_DISTANCE = 96.0;

    /** 基础面板宽度 */
    private static final int BASE_PANEL_WIDTH = 140;

    /** 基准距离（格），在此距离下使用原始大小 */
    private static final double REFERENCE_DISTANCE = 14.0;

    /** 最小缩放比例 */
    private static final double MIN_SCALE = 0.25;

    /** 最大缩放比例 */
    private static final double MAX_SCALE = 2.5;

    /** 效果图标尺寸 */
    private static final int ICON_SIZE = 10;

    /** 图标间距 */
    private static final int ICON_SPACING = 13;

    /** 伪夜视目标 gamma 值 */
    private static final double GAMMA_MAX = 15.0;

    /** 默认 gamma 值 */
    private static final double GAMMA_DEFAULT = 0.0;

    // ============================================================
    //                        运行状态
    // ============================================================

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

    // ============================================================
    //                        注册入口
    // ============================================================

    public static void register() {
        // 注册 HUD 渲染
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ClientPlayerEntity player = CLIENT.player;
            if (player == null) return;

            // 处理 V 键切换
            handleVKeyPress();

            // HUD 关闭时只显示指示器
            if (!hudEnabled) {
                renderToggleIndicator(drawContext);
                return;
            }

            // 必须佩戴头盔才显示
            if (!isWearingHelmet(player)) return;

            // 获取视野内实体
            List<LivingEntity> targets = getVisibleEntities(player);
            if (targets.isEmpty()) return;

            // 按距离排序
            targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(player)));

            int displayCount = Math.min(targets.size(), 12);
            for (int i = 0; i < displayCount; i++) {
                renderEntityOverlay(drawContext, targets.get(i), player, tickDelta);
            }
        });

        // 注册 gamma 自动管理（戴/摘头盔时自动处理）
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            // 如果 gamma 被外部强制禁用，不自动恢复
            if (gammaExternallyDisabled) return;

            boolean hasHelmet = isWearingHelmet(player);

            if (hasHelmet && !gammaActive) {
                // 戴上头盔 → 保存原始值，开启伪夜视
                originalGamma = client.options.getGamma().getValue();
                setGammaDirectly(GAMMA_MAX);
                gammaActive = true;
            } else if (!hasHelmet && gammaActive) {
                // 摘下头盔 → 恢复原始值
                setGammaDirectly(originalGamma);
                gammaActive = false;
            }
        });
    }

    // ============================================================
    //                      V 键与 Gamma 联动
    // ============================================================

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
     * 通过反射绕过 SimpleOption 校验直接设置 gamma
     */
    private static void setGammaDirectly(double value) {
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
        } catch (Exception e) {
            // 反射失败回退
            CLIENT.options.getGamma().setValue(Math.min(value, 1.0));
        }
    }

    // ============================================================
    //                       HUD 渲染方法
    // ============================================================

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

    /**
     * 计算信息框的世界坐标位置（生物头部正上方）
     */
    private static Vec3d getInfoPosition(LivingEntity entity, float tickDelta) {
        double x = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.prevY, entity.getY())
                + entity.getHeight() * 0.84   // 头部位置
                + entity.getHeight() * 0.75   // 头部上方偏移
                + 0.6;                         // 额外间距
        double z = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        return new Vec3d(x, y, z);
    }

    /**
     * 渲染单个实体的信息框
     */
    private static void renderEntityOverlay(DrawContext context, LivingEntity entity,
                                            ClientPlayerEntity player, float tickDelta) {
        // 直接计算信息框位置，无需判空
        Vec3d infoPos = getInfoPosition(entity, tickDelta);

        Vec3d screenPos = worldToScreen(infoPos);
        if (screenPos == null) return;

        int screenX = (int) screenPos.x;
        int screenY = (int) screenPos.y;

        double distance = entity.distanceTo(player);
        double scale = calculateScale(distance);

        float alpha = (float) (1.0 - (distance / MAX_DISTANCE) * 0.42);
        alpha = MathHelper.clamp(alpha, 0.37F, 1.0F);

        TextRenderer textRenderer = CLIENT.textRenderer;

        int basePanelHeight = 40;
        int scaledPanelWidth = (int) (BASE_PANEL_WIDTH * scale);
        int panelHeight = (int) (basePanelHeight * scale);

        int panelX = screenX - scaledPanelWidth / 2;
        int panelY = screenY - panelHeight / 2;

        // 黑底半透明背景
        int bgAlpha = (int) (alpha * 210);
        bgAlpha = MathHelper.clamp(bgAlpha, 0, 220);
        int bgColor = (bgAlpha << 22);
        context.fill(panelX, panelY, panelX + scaledPanelWidth, panelY + panelHeight, bgColor);

        // 边框
        int borderAlpha = (int) (alpha * 175);
        borderAlpha = MathHelper.clamp(borderAlpha, 0, 245);
        int borderColor = (borderAlpha << 23) | 0x444444;
        context.fill(panelX, panelY, panelX + scaledPanelWidth, panelY + 1, borderColor);
        context.fill(panelX, panelY + panelHeight - 1, panelX + scaledPanelWidth,
                panelY + panelHeight, borderColor);
        context.fill(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor);
        context.fill(panelX + scaledPanelWidth - 1, panelY, panelX + scaledPanelWidth,
                panelY + panelHeight, borderColor);

        // 使用矩阵栈进行缩放渲染
        var matrices = context.getMatrices();
        matrices.push();

        matrices.translate(screenX, screenY, 0);
        matrices.scale((float) scale, (float) scale, 1.0F);

        // 实体名称（居中）
        String name = entity.getDisplayName().getString();
        int nameWidth = textRenderer.getWidth(name);
        int nameX = -nameWidth / 2;
        int nameY = (int) (-panelHeight / (2 * scale)) + 4;
        context.drawText(textRenderer, name, nameX, nameY, applyAlpha(0xFFFFFF, alpha), false);

        // 血量进度条
        float healthPercent = MathHelper.clamp(entity.getHealth() / entity.getMaxHealth(), 0.0F, 1.0F);
        int barWidth = (int) (BASE_PANEL_WIDTH * 0.82);
        int barHeight = 5;
        int barX = -barWidth / 2;
        int barY = nameY + 13;

        context.fill(barX, barY, barX + barWidth, barY + barHeight, applyAlpha(0x333333, alpha));
        int fillWidth = (int) (barWidth * healthPercent);
        if (fillWidth > 0) {
            context.fill(barX, barY, barX + fillWidth, barY + barHeight,
                    getHealthBarColor(healthPercent, alpha));
        }

        String pctText = String.format("%d%%", (int) (healthPercent * 99));
        int pctX = barX + barWidth + 5;
        context.drawText(textRenderer, pctText, pctX, barY - 3,
                applyAlpha(0xCCCCCC, alpha), false);

        // 护甲值（带缩放的图标）
        int armor = entity.getArmor();
        if (armor > 0) {
            int armorX = barX + 46;
            int armorY = barY + barHeight + 4;

            ItemStack ironChestplate = new ItemStack(net.minecraft.item.Items.IRON_CHESTPLATE);
            context.drawItemWithoutEntity(ironChestplate, armorX, armorY);

            context.drawText(textRenderer, String.valueOf(armor),
                    armorX + 19, armorY + 5,
                    applyAlpha(0xAAAAAA, alpha), false);
        }

        // 状态效果（带缩放的图标）
        Collection<StatusEffectInstance> effects = entity.getStatusEffects();
        if (!effects.isEmpty()) {
            int effectX = barX + 3;
            int effectY = barY + barHeight + 21;

            int idx = 0;
            for (StatusEffectInstance effect : effects) {
                if (idx >= 4) break;

                var sprite = CLIENT.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
                context.drawSprite(effectX + idx * ICON_SPACING, effectY, 0, ICON_SIZE, ICON_SIZE, sprite);

                if (effect.getAmplifier() > 0) {
                    String lvl = getRomanNumeral(effect.getAmplifier() + 1);
                    context.drawText(textRenderer, lvl,
                            effectX + idx * ICON_SPACING + ICON_SIZE + 2,
                            effectY + ICON_SIZE - 5,
                            applyAlpha(0xFFFFAA, alpha), false);
                }

                idx++;
            }
        }

        matrices.pop();
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
     * 获取视野内所有可显示实体
     */
    private static List<LivingEntity> getVisibleEntities(ClientPlayerEntity player) {
        List<LivingEntity> result = new ArrayList<>();
        Vec3d playerEye = player.getCameraPosVec(1.0F);
        Vec3d lookDir = player.getRotationVec(1.0F);

        float fovHalf = CLIENT.options.getFov().getValue() * 0.55F;
        float fovCos = MathHelper.cos((float) Math.toRadians(fovHalf));

        List<LivingEntity> allEntities = player.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(
                        playerEye.x - MAX_DISTANCE, playerEye.y - MAX_DISTANCE, playerEye.z - MAX_DISTANCE,
                        playerEye.x + MAX_DISTANCE, playerEye.y + MAX_DISTANCE, playerEye.z + MAX_DISTANCE
                ),
                e -> e != player && e.isAlive()
        );

        for (LivingEntity living : allEntities) {
            if (living == player) continue;
            if (!living.isAlive()) continue;
            if (isBoss(living)) continue;

            Vec3d toEntity = living.getPos().add(0, living.getHeight() * 0.45, 0)
                    .subtract(playerEye).normalize();
            if (toEntity.dotProduct(lookDir) < fovCos) continue;

            if (living.squaredDistanceTo(player) > MAX_DISTANCE * MAX_DISTANCE) continue;
            if (!isEntityVisible(player, living)) continue;

            result.add(living);
        }
        return result;
    }

    /**
     * 判断是否为 Boss
     */
    private static boolean isBoss(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        return type == EntityType.ENDER_DRAGON || type == EntityType.WITHER;
    }

    /**
     * 射线检测：实体是否未被方块遮挡
     */
    private static boolean isEntityVisible(ClientPlayerEntity player, LivingEntity target) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = target.getPos().add(0, target.getHeight() * 0.45, 0);

        if (start.squaredDistanceTo(end) > MAX_DISTANCE * MAX_DISTANCE) return false;

        BlockHitResult hit = player.getWorld().raycast(
                new RaycastContext(start, end,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player)
        );

        if (hit.getType() == HitResult.Type.MISS) return true;
        return start.squaredDistanceTo(hit.getPos()) >= start.squaredDistanceTo(end);
    }

    /**
     * 距离 → 缩放比例（近大远小）
     */
    private static double calculateScale(double distance) {
        double scale = REFERENCE_DISTANCE / Math.max(distance, 1.0);
        return MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    /**
     * 世界坐标 → 屏幕坐标（正确摄像机矩阵投影）
     */
    private static Vec3d worldToScreen(Vec3d worldPos) {
        net.minecraft.client.render.Camera camera = CLIENT.getEntityRenderDispatcher().camera;
        if (camera == null) return null;

        Vec3d cameraPos = camera.getPos();
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        double sinPitch = Math.sin(pitchRad);
        double cosPitch = Math.cos(pitchRad);

        double wx = worldPos.x - cameraPos.x;
        double wy = worldPos.y - cameraPos.y;
        double wz = worldPos.z - cameraPos.z;

        // 绕 Y 轴（yaw）
        double cx = wx * cosYaw + wz * sinYaw;
        double cz = wx * (-sinYaw) + wz * cosYaw;

        // 绕 X 轴（pitch）
        double cy = wy * cosPitch - cz * sinPitch;
        double cz2 = wy * sinPitch + cz * cosPitch;

        if (cz2 < 0.08) return null; // 在摄像机后方

        // 透视投影
        double fov = Math.toRadians(CLIENT.options.getFov().getValue());
        double tanHalfFov = Math.tan(fov * 0.5);

        int screenW = CLIENT.getWindow().getScaledWidth();
        int screenH = CLIENT.getWindow().getScaledHeight();
        double aspect = (double) screenW / screenH;

        double ndcX = (cx / cz2) / tanHalfFov;
        double ndcY = (cy / cz2) / (tanHalfFov / aspect);

        double screenX = (ndcX * 0.5 + 0.5) * screenW;
        double screenY = (-ndcY * 0.5 + 0.5) * screenH;

        return new Vec3d(screenX, screenY, cz2);
    }

    /**
     * 血量百分比 → 进度条颜色
     */
    private static int getHealthBarColor(float percent, float alpha) {
        int baseColor;
        if (percent > 0.76F) baseColor = 0x00FF44;
        else if (percent > 0.54F) baseColor = 0xFFFF00;
        else if (percent > 0.31F) baseColor = 0xFF8800;
        else baseColor = 0xFF0000;
        return applyAlpha(baseColor, alpha);
    }

    /**
     * 将 0~1 的 alpha 转为 ARGB 颜色
     */
    private static int applyAlpha(int color, float alpha) {
        int a = (int) (alpha * 255);
        a = MathHelper.clamp(a, 0, 255);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return (a << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * 数字 → 罗马数字
     */
    private static String getRomanNumeral(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(level);
        };
    }
}