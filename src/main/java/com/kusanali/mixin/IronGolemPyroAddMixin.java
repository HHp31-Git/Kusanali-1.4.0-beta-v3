package com.kusanali.mixin;

import com.kusanali.register.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemPyroAddMixin {

    @Inject(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
            at = @At("TAIL")
    )
    private void kusanali$addPermanentPyro(
            net.minecraft.entity.EntityType<? extends MagmaCubeEntity> type,
            World world,
            CallbackInfo ci
    ) {
        IronGolemEntity self = (IronGolemEntity) (Object) this;

        self.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.GEO,
                        -1,   // 无限
                        0,
                        false, // 不显示粒子
                        true,  // 显示图标
                        true
                )
        );
    }
}