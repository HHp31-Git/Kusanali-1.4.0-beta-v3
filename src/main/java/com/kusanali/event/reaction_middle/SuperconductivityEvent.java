package com.kusanali.event.reaction_middle;

import com.kusanali.effect.middel.SuperconductivityEffect;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public class SuperconductivityEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(
                (entity, source, amount) -> {
                    SuperconductivityEffect.onDamage(entity, source, amount);
                    return true; // 不拦截原版
                }
        );
    }
}
