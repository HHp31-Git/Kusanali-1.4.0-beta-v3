package com.kusanali.mixin;

import com.kusanali.register.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class WitchAttackMixin {

    @Unique
    private static final StatusEffect[] ELEMENTS = {
            ModEffects.GEO,
            ModEffects.PYRO,
            ModEffects.CYRO,
            ModEffects.ELECTRO,
            ModEffects.ANEMO,
            ModEffects.HYDRO,
            ModEffects.DENDRO
    };

    @Inject(
            method = "onEntityHit",
            at = @At("HEAD")
    )
    private void kusanali$witchArrowElement(EntityHitResult hitResult, CallbackInfo ci) {
        ProjectileEntity self = (ProjectileEntity) (Object) this;

        // 只处理箭
        if (!(self instanceof ArrowEntity arrow)) {
            return;
        }

        // 必须是女巫扔的
        Entity owner = arrow.getOwner();
        if (!(owner instanceof WitchEntity)) {
            return;
        }

        // 命中的必须是活体生物
        Entity hitEntity = hitResult.getEntity();
        if (!(hitEntity instanceof LivingEntity target)) {
            return;
        }

        // 客户端不处理
        if (self.getWorld().isClient) {
            return;
        }

        Random random = self.getWorld().getRandom();
        StatusEffect effect = ELEMENTS[random.nextInt(ELEMENTS.length)];

        target.addStatusEffect(
                new StatusEffectInstance(
                        effect,
                        200,
                        0,
                        false,
                        true,
                        true
                )
        );
    }
}
