package com.kusanali.event;

import com.kusanali.register.ModEffects;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public class TribbleEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.hasStatusEffect(ModEffects.TRIBBLE)) {
                // 直接修改生命值而不是再次调用damage方法
                float newHealth = entity.getHealth() - (amount + 2.0f);
                entity.setHealth(Math.max(0, newHealth));
                return false;  // 阻止原始伤害
            }
            return true;
        });
    }
}
