package com.kusanali.mixin;

import com.kusanali.register.ModEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(SlimeEntity.class)
public abstract class SlimeSplitMixin {
    /**
     * 临时保存父体效果，供子体读取
     */
    @Unique
    private static final ThreadLocal<Collection<StatusEffectInstance>> KUSANALI_PARENT_EFFECTS =
            ThreadLocal.withInitial(() -> null);
    /**
     * 在分裂前保存父体效果
     */
    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void kusanali$captureEffectsOnSplit(CallbackInfo ci) {
        SlimeEntity self = (SlimeEntity) (Object) this;

        if (!self.getWorld().isClient) {
            KUSANALI_PARENT_EFFECTS.set(self.getStatusEffects());
        }
    }

    /**
     * 在构造新 SlimeEntity 后，复制效果
     */
    @Inject(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
            at = @At("TAIL")
    )
    private void kusanali$applyEffectsToChild(
            EntityType<? extends SlimeEntity> type,
            World world,
            CallbackInfo ci
    ) {
        SlimeEntity child = (SlimeEntity) (Object) this;

        Collection<StatusEffectInstance> parentEffects =
                KUSANALI_PARENT_EFFECTS.get();

        if (parentEffects != null) {
            for (StatusEffectInstance effect : parentEffects) {
                StatusEffect statusEffect = effect.getEffectType();

                // 只继承你关心的元素效果
                if (statusEffect == ModEffects.GEO
                        || statusEffect == ModEffects.PYRO
                        || statusEffect == ModEffects.CYRO
                        || statusEffect == ModEffects.HYDRO
                        || statusEffect == ModEffects.ELECTRO
                        || statusEffect == ModEffects.ANEMO
                        || statusEffect == ModEffects.DENDRO) {


                    child.addStatusEffect(
                            new StatusEffectInstance(
                                    statusEffect,
                                    effect.getDuration(),
                                    effect.getAmplifier(),
                                    effect.isAmbient(),
                                    effect.shouldShowParticles(),
                                    effect.shouldShowIcon()
                            )
                    );
                }
            }

            // 清空 ThreadLocal，防止污染下一次分裂
            KUSANALI_PARENT_EFFECTS.remove();
        }
    }
}
