package com.kusanali.server;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SeedSignRender {
    private static int tickCounter = 0;
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;
            tickCounter++;

            client.world.getEntities().forEach(entity -> {
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.hasStatusEffect(ModEffects.SEED_SIGN)) {
                        renderCloverParticles(client, livingEntity, tickCounter);
                    }
                }
            });
        });
    }

    private static void renderCloverParticles(MinecraftClient client, @NotNull LivingEntity entity, int tick) {
        // 计算实体身体中部位置
        double centerY = entity.getY() + entity.getHeight() / 2;

        // 圆形轨迹参数
        int particleCount = 8;
        double radius = 0.8;
        double baseAngle = (tick % 360) * Math.PI / 180.0; // 基础旋转角度

        for (int i = 0; i < particleCount; i++) {
            // 计算每个粒子的角度偏移
            double angle = baseAngle + (2 * Math.PI * i) / particleCount;

            // 计算粒子在圆形轨迹上的位置
            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);

            // 计算粒子在实体身体中部的最终位置
            double particleX = entity.getX() + xOffset;
            double particleY = centerY;
            double particleZ = entity.getZ() + zOffset;

            // 垂直方向的轻微波动
            double verticalOffset = 0.1 * Math.sin(angle * 2 + baseAngle * 4);
            particleY += verticalOffset;

            // 生成绿色粒子
            Objects.requireNonNull(client.world).addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    particleX, particleY, particleZ,
                    0.0, 0.0, 0.0
            );


            if (i % 2 == 0) {
                double innerRadius = radius * 0.6;
                double innerXOffset = innerRadius * Math.cos(angle);
                double innerZOffset = innerRadius * Math.sin(angle);

                client.world.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        entity.getX() + innerXOffset,
                        centerY + verticalOffset * 0.5,
                        entity.getZ() + innerZOffset,
                        0.0, 0.0, 0.0
                );
            }
        }
    }
}
