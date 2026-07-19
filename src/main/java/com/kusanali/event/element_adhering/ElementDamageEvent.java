package com.kusanali.event.element_adhering;

import com.kusanali.datagenerator.EntityTypeTagsProvider;
import com.kusanali.register.ModDamageTypes;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

public class ElementDamageEvent {
    public static void register() {

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(
                (target, originalSource, amount) -> {
                    Entity attacker = originalSource.getAttacker();
                    if (!(attacker instanceof LivingEntity livingAttacker)) {
                        return true;
                    }
                    if (!livingAttacker.getType().isIn(EntityTypeTagsProvider.ELEMENTS_ENTITY)) {
                        return true;
                    }
                    if (!(target.getWorld() instanceof ServerWorld world)) {
                        return true;
                    }
                    DamageSource elementSource =
                            ModDamageTypes.element(world);
                    target.damage(elementSource, amount);
                    return false;
                }
        );
    }
}
