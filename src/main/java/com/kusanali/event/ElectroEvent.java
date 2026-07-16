package com.kusanali.event;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;

public class ElectroEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.isOf(DamageTypes.LIGHTNING_BOLT)) {
                entity.addStatusEffect(new StatusEffectInstance(
                        ModEffects.ELECTRO,
                        200,
                        0,
                        false,
                        true,
                        true
                ));
            }
            return true;
        });
    }
}
