package com.kusanali.register;

import com.kusanali.effect.FloatDreamQEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final StatusEffect MAGIC_DAMAGE = new FloatDreamQEffect();

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "magic_damage"), MAGIC_DAMAGE);
    }
}
