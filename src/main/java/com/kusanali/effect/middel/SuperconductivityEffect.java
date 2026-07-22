package com.kusanali.effect.middel;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;

public class SuperconductivityEffect extends StatusEffect {
    public SuperconductivityEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7FBFFF);
    }

    private static final TagKey<DamageType> PHISICAL =
            DamageTypeTagProvider.PHYSICAL;

    public static void onDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity.getWorld() instanceof ServerWorld)) return;

        // 持有超导效果
        if (!entity.hasStatusEffect(com.kusanali.register.ModEffects.SUPERCONDUCTIVITY)) {
            return;
        }

        // 使用 Tag 判断物理伤害
        if (!source.isIn(PHISICAL)) {
            return;
        }

        // 追加物理易伤伤害
        entity.setHealth(entity.getHealth() - 2.0f);
        if (entity.getHealth() <= 0f) {
            entity.setHealth(0f);
            entity.onDeath(source);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
