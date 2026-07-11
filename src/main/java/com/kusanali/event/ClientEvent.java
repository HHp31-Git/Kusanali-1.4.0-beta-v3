package com.kusanali.event;

import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientEvent {
    //虚空终端-夜视效果
    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                boolean isWearingHelmet = player.getInventory().getArmorStack(3).getItem() == ModItems.CLIENT;
                StatusEffectInstance currentLuck = player.getStatusEffect(StatusEffects.NIGHT_VISION);
                if (isWearingHelmet) {
                    if (currentLuck == null) {
                        player.addStatusEffect(new StatusEffectInstance(
                                StatusEffects.NIGHT_VISION,
                                -1,
                                1,
                                true,
                                true
                        ));
                    }
                } else {
                    if (currentLuck != null && currentLuck.getDuration() == -1) {
                        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                    }
                }
            }
        });
    }
}
