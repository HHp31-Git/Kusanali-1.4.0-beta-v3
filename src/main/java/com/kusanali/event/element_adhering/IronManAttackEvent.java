package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.IronGolemEntity;

public class IronManAttackEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getWorld().isClient()) return true;

            if (!(source.getAttacker() instanceof IronGolemEntity)) return true;

            // 已有 Geo 且剩余时间充足，跳过
            StatusEffectInstance existing = entity.getStatusEffect(ModEffects.GEO);
            if (existing != null && existing.getDuration() > 120) return true;

            entity.addStatusEffect(
                    new StatusEffectInstance(
                            ModEffects.GEO,
                            200,
                            0,
                            false,
                            true,
                            true
                    )
            );

            return true;
        });
    }
}
