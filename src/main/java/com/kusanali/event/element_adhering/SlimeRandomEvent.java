package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.random.Random;

public class SlimeRandomEvent {

    private static final StatusEffect[] ELEMENTS = {
            ModEffects.GEO,
            ModEffects.PYRO,
            ModEffects.CYRO,
            ModEffects.ELECTRO,
            ModEffects.ANEMO,
            ModEffects.HYDRO,
            ModEffects.DENDRO
    };

    private static final String INIT_TAG = "kusanali:element_init";

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (Entity entity : world.iterateEntities()) {
                if (!(entity instanceof SlimeEntity slime)) continue;

                // 检查是否已经拥有任何元素效果
                boolean hasElement = false;
                for (StatusEffect effect : ELEMENTS) {
                    if (slime.hasStatusEffect(effect)) {
                        hasElement = true;
                        break;
                    }
                }

                // 如果没有元素效果，则添加
                if (!hasElement) {
                    int seed = entity.getUuid().hashCode();
                    Random random = Random.create(seed);
                    StatusEffect effect = ELEMENTS[random.nextInt(ELEMENTS.length)];

                    slime.addStatusEffect(
                            new StatusEffectInstance(
                                    effect,
                                    -1, // 无限时长
                                    0,
                                    false,
                                    true,
                                    true
                            )
                    );
                }
            }
        })
    ;}
}
