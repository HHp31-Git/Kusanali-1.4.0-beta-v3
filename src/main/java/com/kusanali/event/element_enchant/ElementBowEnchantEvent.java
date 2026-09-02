package com.kusanali.event.element_enchant;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModEnchants;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ElementBowEnchantEvent {
    public static void onInitialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            World world = entity.getEntityWorld();
            if (world.isClient()) {
                return true;
            }
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity livingEntity) {
                ItemStack stack = livingEntity.getMainHandStack();
                if (stack.hasEnchantments()) {
                    if (EnchantmentHelper.getLevel(ModEnchants.ANEMO_ENCHANT_BOW, stack) > 0) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.ANEMO,
                                5, 0, false, false));
                    }
                    if (EnchantmentHelper.getLevel(ModEnchants.GEO_ENCHANT_BOW, stack) > 0) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.GEO,
                                5, 0, false, false));
                    }
                    if (EnchantmentHelper.getLevel(ModEnchants.ELECTRO_ENCHANT_BOW, stack) > 0) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.ELECTRO,
                                125, 0, true, true));
                    }
                    if (EnchantmentHelper.getLevel(ModEnchants.DENDRO_ENCHANT_BOW, stack) > 0) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.DENDRO,
                                125, 0, true, true));
                    }
                    if (EnchantmentHelper.getLevel(ModEnchants.HYDRO_ENCHANT_BOW, stack) > 0) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.HYDRO,
                                125, 0, true, true));
                    }
                    if (EnchantmentHelper.getLevel(ModEnchants.CYRO_ENCHANT_BOW, stack) > 0) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.CYRO,
                                125, 0, true, true));
                    }
                }
            }
            return true;
        });
    }
}
