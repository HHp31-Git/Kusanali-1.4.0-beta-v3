package com.kusanali.event.element_adhering;

import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

import java.util.Set;

public class SlimeRandomEvent {
    /** 可被随机到的元素效果 */
    private static final StatusEffect[] ELEMENTS = {
            ModEffects.GEO,
            ModEffects.PYRO,
            ModEffects.CYRO,
            ModEffects.ELECTRO,
            ModEffects.ANEMO,
            ModEffects.HYDRO,
            ModEffects.DENDRO
    };

    private static final Set<Entity> PROCESSED =
            java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());

    /** 用于避免重复赋予 */
    private static final Set<Entity> SPAWNED_SLIMES =
            java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld)) return;

            for (Entity entity : world.iterateEntities()) {
                if (!(entity instanceof SlimeEntity slime)) continue;

                // 只处理刚加入世界的史莱姆
                if (!PROCESSED.add(slime)) continue;

                Random random = slime.getRandom();
                StatusEffect effect = ELEMENTS[random.nextInt(ELEMENTS.length)];

                slime.addStatusEffect(
                        new StatusEffectInstance(
                                effect, -1,
                                0,
                                false,
                                true,
                                true
                        )
                );
            }
        });
    }
}
