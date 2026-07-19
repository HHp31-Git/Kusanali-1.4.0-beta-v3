package com.kusanali.event.element_reaction;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.WeakHashMap;

public class FreezingEvent {

    private static final WeakHashMap<ServerPlayerEntity, Boolean> PREV_TRY_JUMP =
            new WeakHashMap<>();

    public static void register() {

        // 阻止被冻结的生物造成伤害
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.getAttacker() instanceof LivingEntity attacker) {
                return !attacker.hasStatusEffect(ModEffects.FREEZING);
            }
            return true;
        });

        // 阻止被冻结的玩家使用物品
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : world.getPlayers()) {

                if (!player.hasStatusEffect(ModEffects.FREEZING)) continue;

                // 停止物品使用
                player.stopUsingItem();

                if (player.getVelocity().y > 0) {
                    player.setVelocity(
                            player.getVelocity().x,
                            0,
                            player.getVelocity().z
                    );
                    player.velocityModified = true;
                }

                // 阻止跳跃
                boolean tryingToJump =
                        player.getVelocity().y > 0.1
                                && !player.isOnGround();

                boolean wasTrying = PREV_TRY_JUMP.getOrDefault(player, false);

                if (tryingToJump && !wasTrying) {
                    reduceFreezing(player);
                }

                PREV_TRY_JUMP.put(player, tryingToJump);
            }
        });
    }

    private static void reduceFreezing(ServerPlayerEntity player) {
        StatusEffectInstance freezing = player.getStatusEffect(ModEffects.FREEZING);
        if (freezing == null) return;

        int currentDuration = freezing.getDuration();

        if (currentDuration <= 14) {
            // 剩余时间不足 0.7 秒 → 直接清除
            player.removeStatusEffect(ModEffects.FREEZING);
        } else {
            int newDuration = currentDuration - 14;
            player.addStatusEffect(
                    new StatusEffectInstance(
                            ModEffects.FREEZING,
                            newDuration,
                            freezing.getAmplifier(),
                            freezing.isAmbient(),
                            freezing.shouldShowParticles(),
                            freezing.shouldShowIcon()
                    )
            );
        }
    }
}
