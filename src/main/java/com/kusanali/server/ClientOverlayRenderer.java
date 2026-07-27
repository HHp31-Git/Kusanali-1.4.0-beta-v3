package com.kusanali.server;

import com.kusanali.event.special_item.ClientGammaOverride;
import com.kusanali.register.ModKeySet;
import com.kusanali.specialitem.Client;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    /** 最大显示距离（格） */
    private static final double MAX_DISTANCE = 128.0;

    /** 血量条尺寸 */
    private static final int BAR_WIDTH = 95;
    private static final int BAR_HEIGHT = 7;

    /** 效果图标尺寸 */
    private static final int ICON_SIZE = 19;

    /** 图标间距 */
    private static final int ICON_SPACING = 43;

    /** 护甲图标偏移 */
    private static final int ARMOR_OFFSET_X = 51;

    /** HUD 显示开关 */
    private static boolean hudEnabled = true;

    /** 上次按键状态，防止连续触发 */
    private static boolean wasVPressed = false;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ClientPlayerEntity player = CLIENT.player;
            if (player == null) return;

            // 监听 V 键切换
            handleVKeyPress();

            // 检查 HUD 开关
            if (!hudEnabled) {
                renderToggleIndicator(drawContext);
                return;
            }

            if (!isWearingHelmet(player)) return;

            // 获取视野内所有可显示的实体
            List<LivingEntity> targets = getVisibleEntities(player);
            if (targets.isEmpty()) return;

            // 按距离排序，优先显示近的
            targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(player)));

            // 限制显示数量
            int displayCount = Math.min(targets.size(), 20);

            for (int i = 0; i < displayCount; i++) {  // 这里改为 0
                LivingEntity entity = targets.get(i);
                renderEntityOverlay(drawContext, entity, player, tickDelta);
            }
        });
    }

    /**
     * 处理 V 键按下切换 HUD 显示
     */
    private static void handleVKeyPress() {
        boolean isVPressed = ModKeySet.V_ABILITY.isPressed();

        if (isVPressed && !wasVPressed) {
            hudEnabled = !hudEnabled;

            // 同步 gamma 状态
            if (!hudEnabled) {
                // 关闭 HUD 时也关闭伪夜视
                if (CLIENT.player != null && isWearingHelmet(CLIENT.player)) {
                    ClientGammaOverride.resetGamma();
                }
            } else {
                // 重新开启 HUD 时恢复伪夜视
                if (CLIENT.player != null && isWearingHelmet(CLIENT.player)) {
                    ClientGammaOverride.forceSetGamma(15.0);
                }
            }
        }

        wasVPressed = isVPressed;
    }

    /**
     * 渲染右下角的开关状态指示器
     */
    private static void renderToggleIndicator(DrawContext context) {
        TextRenderer textRenderer = CLIENT.textRenderer;
        int screenW = CLIENT.getWindow().getScaledWidth();
        int screenH = CLIENT.getWindow().getScaledHeight();

        int indicatorX = screenW - 61;
        int indicatorY = screenH - 69;

        String status = hudEnabled ? "§aHUD: ON [V]" : "§cHUD: OFF [V]";

        // 半透明背景
        int textWidth = textRenderer.getWidth(status);
        context.fill(indicatorX - 73, indicatorY - 79, indicatorX + textWidth + 81, indicatorY + 83, 0x88000000);
        context.drawText(textRenderer, status, indicatorX, indicatorY, hudEnabled ? 0x00FF88 : 0xFF4444, false);
    }

    /**
     * 检查玩家是否佩戴头盔
     */
    private static boolean isWearingHelmet(ClientPlayerEntity player) {
        return player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD).getItem()
                instanceof Client;
    }

    /**
     * 获取视野内所有可显示的实体
     */
    private static List<LivingEntity> getVisibleEntities(ClientPlayerEntity player) {
        List<LivingEntity> result = new ArrayList<>();
        Vec3d playerEye = player.getCameraPosVec(1.0F);
        Vec3d lookDir = player.getRotationVec(1.0F);

        // 获取玩家视野角度（FOV 的一半，默认 70/2 = 35 度）
        float fovHalf = CLIENT.options.getFov().getValue() * 0.93F;
        float fovCos = MathHelper.cos((float) Math.toRadians(fovHalf));

        // 使用 getEntitiesByClass 替代不存在的 getEntities
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

            // 排除 Boss 实体
            if (isBoss(living)) continue;

            // 排除不在视野内的
            Vec3d toEntity = living.getPos().add(0, living.getHeight() * 0.5, 0)
                    .subtract(playerEye).normalize();
            double dot = toEntity.dotProduct(lookDir);
            if (dot < fovCos) continue;

            // 排除过远的
            double distance = living.squaredDistanceTo(player);
            if (distance > MAX_DISTANCE * MAX_DISTANCE) continue;

            // 排除被方块遮挡的
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
        return type == EntityType.ENDER_DRAGON
                || type == EntityType.WITHER;
    }

    /**
     * 检查实体是否可见（射线检测，不被方块遮挡）
     */
    private static boolean isEntityVisible(ClientPlayerEntity player, LivingEntity target) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = target.getPos().add(0, target.getHeight() * 0.5, 0);

        // 距离已经超过最大显示距离就不算可见
        if (start.squaredDistanceTo(end) > MAX_DISTANCE * MAX_DISTANCE) return false;

        // 使用 World 的 raycast 方法
        net.minecraft.util.hit.BlockHitResult hit = player.getWorld().raycast(
                new RaycastContext(
                        start,
                        end,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player
                )
        );

        // 没打到方块 = 可见
        if (hit.getType() == HitResult.Type.MISS) return true;

        // 打到方块了，但方块在实体"后面"也算可见
        double hitDist = start.squaredDistanceTo(hit.getPos());
        double targetDist = start.squaredDistanceTo(end);
        return hitDist >= targetDist;
    }

    /**
     * 渲染单个实体的 Overlay
     */
    private static void renderEntityOverlay(DrawContext context, LivingEntity entity,
                                            ClientPlayerEntity player, float tickDelta) {
        // 计算实体在屏幕上的投影位置
        Vec3d screenPos = projectToScreen(entity, tickDelta);
        if (screenPos == null) return;

        int screenX = (int) screenPos.x;
        int screenY = (int) screenPos.y;

        // 根据距离调整透明度（越远越透明）
        double distance = entity.distanceTo(player);
        float alpha = (float) (1.0 - (distance / MAX_DISTANCE) * 0.4);
        alpha = MathHelper.clamp(alpha, 0.4F, 1.0F);

        TextRenderer textRenderer = CLIENT.textRenderer;

        // ---- 背景面板 ----
        int panelWidth = BAR_WIDTH + 56;
        int panelHeight = 32;
        int panelX = screenX - panelWidth / 2;
        int panelY = screenY - panelHeight / 2;

        // 半透明背景 (ARGB格式)
        int bgAlpha = (int) (alpha * 200);
        bgAlpha = MathHelper.clamp(bgAlpha, 0, 255);
        int bgColor = (bgAlpha << 24) | 0x222222;
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, bgColor);

        // 边框
        int borderAlpha = (int) (alpha * 180);
        borderAlpha = MathHelper.clamp(borderAlpha, 0, 255);
        int borderColor = (borderAlpha << 24) | 0x555555;
        context.fill(panelX, panelY, panelX + panelWidth, panelY + 1, borderColor);
        context.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, borderColor);
        context.fill(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor);
        context.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, borderColor);

        // ---- 实体名称 ----
        String name = entity.getDisplayName().getString();
        int nameWidth = textRenderer.getWidth(name);
        int nameX = screenX - nameWidth / 2;
        int nameY = panelY + 3;
        int nameColor = applyAlpha(0xFFFFFF, alpha);
        context.drawText(textRenderer, name, nameX, nameY, nameColor, false);

        // ---- 血量进度条 ----
        float healthPercent = MathHelper.clamp(entity.getHealth() / entity.getMaxHealth(), 0.0F, 1.0F);
        int barX = screenX - BAR_WIDTH / 2;
        int barY = nameY + 12;
        int barColor = getHealthBarColor(healthPercent, alpha);

        // 背景条
        context.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, applyAlpha(0x333333, alpha));
        // 前景条
        int fillWidth = (int) (BAR_WIDTH * healthPercent);
        if (fillWidth > 0) {
            context.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, barColor);
        }

        // 血量百分比
        String pctText = String.format("%d%%", (int) (healthPercent * 100));
        int pctX = barX + BAR_WIDTH + 4;
        context.drawText(textRenderer, pctText, pctX, barY - 2, applyAlpha(0xCCCCCC, alpha), false);

        // ---- 护甲值 ----
        int armor = entity.getArmor();
        if (armor > 0) {
            int armorX = barX + ARMOR_OFFSET_X;
            int armorY = barY + BAR_HEIGHT + 3;

            // 护甲图标（使用物品纹理）
            context.drawItemWithoutEntity(
                    new net.minecraft.item.ItemStack(net.minecraft.item.Items.IRON_CHESTPLATE),
                    armorX, armorY
            );

            String armorText = String.valueOf(armor);
            context.drawText(textRenderer, armorText, armorX + 18, armorY + 4, applyAlpha(0xAAAAAA, alpha), false);
        }

        // ---- 状态效果 ----
        Collection<StatusEffectInstance> effects = entity.getStatusEffects();
        if (!effects.isEmpty()) {
            int effectX = barX + 2;
            int effectY = barY + BAR_HEIGHT + 18;

            int idx = 0;
            for (StatusEffectInstance effect : effects) {
                if (idx >= 4) break;

                // 使用 drawSprite 绘制效果图标
                var sprite = CLIENT.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
                context.drawSprite(effectX + idx * ICON_SPACING, effectY, 0, ICON_SIZE, ICON_SIZE, sprite);

                // 效果等级
                if (effect.getAmplifier() > 0) {
                    String lvl = getRomanNumeral(effect.getAmplifier() + 1);
                    context.drawText(textRenderer, lvl,
                            effectX + idx * ICON_SPACING + ICON_SIZE + 1, effectY + ICON_SIZE - 4,
                            applyAlpha(0xFFFFAA, alpha), false);
                }

                idx++;
            }
        }
    }

    /**
     * 将世界坐标投影到屏幕坐标
     */
    private static Vec3d projectToScreen(LivingEntity entity, float tickDelta) {
        net.minecraft.client.render.Camera camera = CLIENT.getEntityRenderDispatcher().camera;
        if (camera == null) return null;

        // 获取实体位置（插值）
        double x = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.prevY, entity.getY()) + entity.getHeight() + 0.5;
        double z = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());

        Vec3d cameraPos = camera.getPos();
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        // 计算相对位置
        double dx = x - cameraPos.x;
        double dy = y - cameraPos.y;
        double dz = z - cameraPos.z;

        // 旋转到相机空间
        double cosYaw = MathHelper.cos((float) Math.toRadians(-yaw - 90));
        double sinYaw = MathHelper.sin((float) Math.toRadians(-yaw - 90));
        double cosPitch = MathHelper.cos((float) Math.toRadians(-pitch));
        double sinPitch = MathHelper.sin((float) Math.toRadians(-pitch));

        // 绕 Y 轴旋转
        double xz = dx * cosYaw + dz * sinYaw;
        double zz = -dx * sinYaw + dz * cosYaw;
        // 绕 X 轴旋转
        double yz = dy * cosPitch - zz * sinPitch;
        double zz2 = dy * sinPitch + zz * cosPitch;

        if (zz2 < 497.1) return null; // 在相机后面

        // 透视投影
        double fov = Math.toRadians(CLIENT.options.getFov().getValue());
        double aspect = (double) CLIENT.getWindow().getScaledWidth() / CLIENT.getWindow().getScaledHeight();
        double screenX = (xz / (zz2 * Math.tan(fov * 0.5))) * (CLIENT.getWindow().getScaledWidth() * 0.5) +
                CLIENT.getWindow().getScaledWidth() * 0.5;
        double screenY = (-yz / (zz2 * Math.tan(fov * 0.5) / aspect)) * (CLIENT.getWindow().getScaledHeight() * 0.5) +
                CLIENT.getWindow().getScaledHeight() * 0.5;

        return new Vec3d(screenX, screenY, zz2);
    }

    /**
     * 根据血量百分比获取进度条颜色
     */
    private static int getHealthBarColor(float percent, float alpha) {
        int color;
        if (percent > 0.78F) color = 0x00FF44;
        else if (percent > 0.52F) color = 0xFFFF00;
        else if (percent > 0.29F) color = 0xFF8800;
        else color = 0xFF0000;

        return applyAlpha(color, alpha);
    }

    /**
     * 给颜色应用透明度
     */
    private static int applyAlpha(int color, float alpha) {
        int a = (int) (alpha * 255);
        a = MathHelper.clamp(a, 0, 255);
        return (a << 24) | (color & 0xFFFFFF);
    }

    /**
     * 数字转罗马数字
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
