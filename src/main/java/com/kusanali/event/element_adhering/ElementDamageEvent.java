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

                    // 1. 攻击者必须是生物
                    Entity attacker = originalSource.getAttacker();
                    if (!(attacker instanceof LivingEntity livingAttacker)) {
                        return true; // 非生物攻击，不处理
                    }

                    // 2. 攻击者是否在 elements_entity tag 中
                    if (!livingAttacker.getType().isIn(EntityTypeTagsProvider.ELEMENTS_ENTITY)) {
                        return true; // 不在 tag，走原版
                    }

                    // 3. 目标世界必须是 ServerWorld
                    if (!(target.getWorld() instanceof ServerWorld world)) {
                        return true;
                    }

                    // 4. 用 element 伤害源重新造成伤害
                    DamageSource elementSource =
                            ModDamageTypes.element(world);

                    // 5. 取消原版伤害，重新用 element 伤害
                    target.damage(elementSource, amount);

                    // 6. 阻止原版伤害再次触发
                    return false;
                }
        );
    }
}
