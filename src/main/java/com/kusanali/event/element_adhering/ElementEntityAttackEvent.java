package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Set;

public class ElementEntityAttackEvent {

    private static final Set<StatusEffect> TRANSFERABLE_ELEMENTS = Set.of(
            ModEffects.GEO,
            ModEffects.PYRO,
            ModEffects.CYRO,
            ModEffects.ELECTRO,
            ModEffects.ANEMO,
            ModEffects.HYDRO,
            ModEffects.DENDRO
    );

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((target, source, amount) -> {
            // 只处理服务端
            if (target.getWorld().isClient()) return true;

            // 获取攻击者
            if (!(source.getAttacker() instanceof LivingEntity attacker)) return true;

            // 遍历攻击者身上的效果
            for (StatusEffectInstance effect : attacker.getStatusEffects()) {
                StatusEffect type = effect.getEffectType();
                if (!TRANSFERABLE_ELEMENTS.contains(type)) continue;

                // 只处理无限时长
                if (effect.getDuration() != -1) continue;

                // 目标已有该元素且剩余时间充足，跳过
                StatusEffectInstance existing = target.getStatusEffect(type);
                if (existing != null && existing.getDuration() > 80) continue;

                // 给被攻击者附加 10 秒该效果
                target.addStatusEffect(
                        new StatusEffectInstance(
                                type,
                                200,
                                0,
                                false,
                                true,
                                true
                        )
                );
            }
            return true;
        });
    }
}
