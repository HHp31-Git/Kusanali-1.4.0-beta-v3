package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Set;

public class ElementEntityAttackEvent {

    /** 传递后效果的持续时间：10 秒 = 200 tick */
    private static final int TRANSFER_DURATION = 200;

    /** 只检测这三种元素效果 */
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
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(
                (target, source, amount) -> {

                    // 1. 只处理服务端
                    if (target.getWorld().isClient) {
                        return true;
                    }

                    // 2. 获取攻击者
                    if (!(source.getAttacker() instanceof LivingEntity attacker)) {
                        return true; // 非生物攻击（如摔落、岩浆）不处理
                    }

                    // 3. 遍历攻击者身上的效果
                    for (StatusEffectInstance effect : attacker.getStatusEffects()) {
                        StatusEffect type = effect.getEffectType();

                        // 4. 只处理 Pyro / Hydro / Cyro
                        if (!TRANSFERABLE_ELEMENTS.contains(type)) {
                            continue;
                        }

                        // 5. 只处理无限时长
                        if (effect.getDuration() != -1) {
                            continue;
                        }

                        // 6. 给被攻击者附加 10 秒该效果
                        target.addStatusEffect(
                                new StatusEffectInstance(
                                        type,
                                        TRANSFER_DURATION,   // 10s
                                        0,                   // 等级 I
                                        false,               // 不显示粒子
                                        true,                // 显示图标
                                        true                 // 显示于 HUD
                                )
                        );
                    }
                    return true;
                }
        );
    }
}
