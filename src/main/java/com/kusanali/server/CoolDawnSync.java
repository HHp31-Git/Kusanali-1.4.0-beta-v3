package com.kusanali.server;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class CoolDawnSync {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("kusanali", "cooldown_update"),
                (client, handler, buf, responseSender) -> {
                    int serverCooldown = buf.readInt();
                    client.execute(() -> {
                        // 更新客户端冷却显示
                        FloatDreamHud.cooldown = serverCooldown;
                    });
                });
    }
}
