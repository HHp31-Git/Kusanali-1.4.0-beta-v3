package com.kusanali.event.special_item;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class TribbleReactionEvent {
    /** 反应冷却时间（90 tick = 4.5 秒） */
    private static final int REACTION_COOLDOWN = 90;

    /** 记录实体上次触发反应的时间 */
    private static final Map<UUID, Long> LAST_REACTION_TIME = new WeakHashMap<>();

    /** 反应伤害 */
    private static final float REACTION_DAMAGE = 2.0f;

    public static void register() {

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getWorld().isClient()) return true;
            if (!entity.hasStatusEffect(ModEffects.TRIBBLE)) return true;
            if (!source.isIn(DamageTypeTagProvider.REACTION)) return true;

            // 冷却检查：每 4.5 秒最多触发一次
            long now = entity.getWorld().getTime();
            long lastReaction = LAST_REACTION_TIME.getOrDefault(entity.getUuid(), 0L);
            if (now - lastReaction < REACTION_COOLDOWN) {
                return true;
            }

            // 更新冷却时间
            LAST_REACTION_TIME.put(entity.getUuid(), now);

            World world = entity.getWorld();
            entity.damage(ModDamageTypes.element(world), REACTION_DAMAGE);

            return true;
        });
    }
}
