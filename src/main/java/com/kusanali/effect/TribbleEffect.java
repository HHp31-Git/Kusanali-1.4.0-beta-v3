package com.kusanali.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class TribbleEffect extends StatusEffect {
    public TribbleEffect() {
        super(StatusEffectCategory.HARMFUL, 0x00FF00);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
