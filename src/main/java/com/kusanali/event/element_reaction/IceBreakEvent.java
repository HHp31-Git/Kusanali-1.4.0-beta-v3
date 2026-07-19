package com.kusanali.event.element_reaction;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public class IceBreakEvent {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            // 只在服务端处理
            if (entity.getWorld().isClient()) return true;

            // 必须有 Freezing 效果
            if (!entity.hasStatusEffect(ModEffects.FREEZING)) return true;

            // 必须是物理伤害（使用已注册的 PHYSIC tag）
            if (!source.isIn(DamageTypeTagProvider.PHYSICAL)) return true;

            // 造成 4 点额外伤害
            entity.damage(source, 4.0f);

            // 清除 Freezing 效果
            entity.removeStatusEffect(ModEffects.FREEZING);

            return true;
        });
    }
}
