package com.kusanali.effect.middle;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FreezingEffect extends StatusEffect {
    public FreezingEffect() {
        super(StatusEffectCategory.HARMFUL, 0x87CEEB);
    }
    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity,  attributes, amplifier);
        entity.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        100,
                        9,
                        false,
                        true,
                        true
                )
        );
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
