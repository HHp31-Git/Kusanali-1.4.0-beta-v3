package com.kusanali.event;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.List;

public class SnowBallCyroEvent {
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (Entity entity : world.iterateEntities()) {
                if (!(entity instanceof SnowballEntity snowball)) continue;

                // 雪球自身的碰撞箱
                Box box = snowball.getBoundingBox().expand(0.2);

                // 查找命中的生物
                List<LivingEntity> targets = world.getEntitiesByClass(
                        LivingEntity.class,
                        box,
                        target -> target != snowball.getOwner()
                );

                if (!targets.isEmpty()) {
                    LivingEntity target = targets.get(0);

                    target.addStatusEffect(
                            new StatusEffectInstance(
                                    ModEffects.CYRO,
                                    200,
                                    0,
                                    false,
                                    true,
                                    true
                            )
                    );

                    snowball.discard();
                }
            }
        });
    }
}
