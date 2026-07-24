package com.kusanali.event.element_adhering;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import com.kusanali.register.ModDamageTypes;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class ElementDamageEvent {
    /** 防重入标记 */
    private static final Set<UUID> REENTERING = Collections.newSetFromMap(new WeakHashMap<>());

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((target, originalSource, amount) -> {
            // 只处理服务端
            if (target.getWorld().isClient()) return true;

            Entity attacker = originalSource.getAttacker();
            if (!(attacker instanceof LivingEntity livingAttacker)) return true;

            // 只有元素生物的攻击才转换伤害类型
            if (!livingAttacker.getType().isIn(EntityTypeTagsProvider.ELEMENTS_ENTITY)) return true;

            if (!(target.getWorld() instanceof ServerWorld world)) return true;

            // 防重入
            UUID uuid = target.getUuid();
            if (REENTERING.contains(uuid)) return true;

            REENTERING.add(uuid);
            try {
                // 使用元素伤害源造成伤害
                DamageSource elementSource = ModDamageTypes.element(world);
                target.damage(elementSource, amount);
            } finally {
                world.getServer().execute(() -> REENTERING.remove(uuid));
            }

            // 返回 false 取消原伤害，避免双重伤害
            return false;
        });
    }
}
