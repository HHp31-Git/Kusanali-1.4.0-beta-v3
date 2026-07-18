package com.kusanali.specialitem.elements;

import com.kusanali.register.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class PyroItem extends Item {
    public PyroItem(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getWorld().isClient) {
            return ActionResult.PASS;
        }
        if (entity instanceof LivingEntity) {
            entity.addStatusEffect(new StatusEffectInstance(ModEffects.PYRO, 300, 0));
        }
        return ActionResult.SUCCESS;
    }
}
