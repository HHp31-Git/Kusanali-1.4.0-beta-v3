package com.kusanali.effect;


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SeedSignEffect extends StatusEffect {
    public SeedSignEffect() {
        super(StatusEffectCategory.HARMFUL, 0x00FF00);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 5 == 0;
    }
}
