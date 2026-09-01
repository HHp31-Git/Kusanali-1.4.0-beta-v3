package com.kusanali.register;

import com.kusanali.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.kusanali.Kusanali.MOD_ID;

public class ModEnchants {
    public static final Enchantment ANEMO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "anemo_enchant"),
            new Anemo()
    );
    public static final Enchantment GEO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "geo_enchant"),
            new Geo()
    );
    public static final Enchantment ELECTRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "electro_enchant"),
            new Electro()
    );
    public static final Enchantment DENDRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "dendro_enchant"),
            new Dendro()
    );
    public static final Enchantment HYDRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "hydro_enchant"),
            new Hydro()
    );
    public static final Enchantment PYRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "pyro_enchant"),
            new Pyro()
    );
    public static final Enchantment CYRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "cyro_enchant"),
            new Cyro()
    );
    public static void register() {}
}
