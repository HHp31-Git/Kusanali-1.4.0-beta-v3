package com.kusanali.event;

import com.kusanali.register.ModEffects;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public class TribbleEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.hasStatusEffect(ModEffects.TRIBBLE)) {
                entity.setHealth(entity.getHealth() + amount);
                entity.damage(source, amount + 2.0f);
                return false;
            }
            return true;
        });
    }
}
