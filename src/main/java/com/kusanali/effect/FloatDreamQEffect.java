package com.kusanali.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FloatDreamQEffect extends StatusEffect {
    public FloatDreamQEffect() {
        super(StatusEffectCategory.HARMFUL, 0xADD8E6); // 浅蓝色表示魔法伤害
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 每5秒（100游戏刻）触发一次伤害
        return duration % 100 == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // 造成1点魔法伤害
        entity.damage(entity.getDamageSources().magic(), 1.0F);
    }
}
