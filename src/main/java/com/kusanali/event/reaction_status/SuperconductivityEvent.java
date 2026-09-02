package com.kusanali.event.reaction_status;

import com.kusanali.datagenerator.DamageTypeTagProvider;
import com.kusanali.register.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class SuperconductivityEvent {
    public static void register() {
        final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.getWorld().isClient()) return true;

            // 防重入
            if (REENTERING.contains(entity.getUuid())) return true;

            // 检查是否有 Superconductivity 效果 + 反应伤害
            if (entity.hasStatusEffect(ModEffects.SUPERCONDUCTIVITY)
                    && source.isIn(DamageTypeTagProvider.REACTION)) {

                // 获取 server 引用，避免后续 NPE 警告
                var server = entity.getWorld().getServer();
                if (server == null) return true;

                // 标记防重入
                REENTERING.add(entity.getUuid());
                try {
                    float dmg = 2.0f;
                    entity.setHealth(entity.getHealth() - dmg);

                    if (entity.getHealth() <= 0) {
                        entity.setHealth(0);
                        entity.onDeath(source);
                    }
                } finally {
                    server.execute(() -> REENTERING.remove(entity.getUuid()));
                }
            }
            return true;
        });
    }
}
