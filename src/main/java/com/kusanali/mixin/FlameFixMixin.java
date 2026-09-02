package com.kusanali.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class FlameFixMixin {
    @Inject(method = "onStoppedUsing", at = @At("TAIL"))
    private void kusanali_flame(ItemStack stack, World world, LivingEntity user,
                                int remainingUseTicks, CallbackInfo ci) {
        if (world.isClient) return;
        int flame = EnchantmentHelper.getLevel(Enchantments.FLAME, stack);
        if (flame <= 0) return;
        float pull = BowItem.getPullProgress(stack.getMaxUseTime() - remainingUseTicks);
        if (pull < 1.0f) {
            for (Entity e : world.getEntitiesByClass(PersistentProjectileEntity.class,
                    user.getBoundingBox().expand(2),
                    p -> p.getOwner() != null &&
                            p.getOwner().equals(user) && p.age <= 2)) {
                e.setFireTicks(0);
                break;
            }
        }
    }
}
