package com.kusanali.server;

import com.kusanali.specialitem.combats.Client;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientHander {
    private static final Set<UUID> HELMET_PLAYERS = new HashSet<>();

    public static void register() {
        // 每 tick 检查头盔佩戴状态
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID uuid = player.getUuid();
                boolean hasHelmet = player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof Client;

                if (hasHelmet && !HELMET_PLAYERS.contains(uuid)) {
                    HELMET_PLAYERS.add(uuid);
                } else if (!hasHelmet) {
                    // 当摘下头盔时，同步关闭效果
                    if (HELMET_PLAYERS.contains(uuid)) {
                        HELMET_PLAYERS.remove(uuid);
                        // 发送包通知客户端关闭效果
                        ClientVisionOverlay.setEnabled(false);
                    }
                }
            }
        });
    }
}
