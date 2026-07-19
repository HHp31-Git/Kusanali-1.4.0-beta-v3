package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Box;

public class ElectroEvent {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.isOf(DamageTypes.LIGHTNING_BOLT)) {
                // 以被雷劈的实体为中心，半径 1.5 格
                Box area = new Box(
                        entity.getX() - 1.5,
                        entity.getY() - 1.5,
                        entity.getZ() - 1.5,
                        entity.getX() + 1.5,
                        entity.getY() + 1.5,
                        entity.getZ() + 1.5
                );

                // 获取范围内所有活着的生物
                for (LivingEntity target : entity.getWorld().getEntitiesByClass(
                        LivingEntity.class,
                        area,
                        LivingEntity::isAlive
                )) {
                    target.addStatusEffect(new StatusEffectInstance(
                            ModEffects.ELECTRO,
                            200,
                            0,
                            false,
                            true,
                            true
                    ));
                }
            }
            return true;
        });
    }
}
