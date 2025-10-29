package com.kusanali.register;

import com.kusanali.effect.FloatDreamQEffect;
import com.kusanali.effect.SeedSignEffect;
import com.kusanali.effect.TribbleEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final StatusEffect MAGIC_DAMAGE = new FloatDreamQEffect();
    public static final StatusEffect TRIBBLE = new TribbleEffect();
    public static final StatusEffect SEED_SIGN = new SeedSignEffect();

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "magic_damage"), MAGIC_DAMAGE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "tribble"), TRIBBLE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "seed_sign"), SEED_SIGN);
    }
}
