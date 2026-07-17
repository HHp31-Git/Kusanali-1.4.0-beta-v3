package com.kusanali.register;

import com.kusanali.effect.*;
import com.kusanali.effect.elements.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final StatusEffect MAGIC_DAMAGE = new FloatDreamQEffect(); //心景幻成-附加伤害
    public static final StatusEffect TRIBBLE = new TribbleEffect(); //灭净三业
    public static final StatusEffect ELECTRO = new ElectroEffect(); //雷元素附着
    public static final StatusEffect DENDRO = new DendroEffect(); //草元素附着
    public static final StatusEffect PYRO = new PyroEffect(); //火元素附着
    public static final StatusEffect HYDRO = new HydroEffect(); //水元素附着
    public static final StatusEffect CYRO = new CyroEffect(); //冰元素附着
    public static final StatusEffect ANEMO = new AnemoEffect(); //风元素附着
    public static final StatusEffect GEO = new GeoEffect(); //岩元素附着 /

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "magic_damage"), MAGIC_DAMAGE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "tribble"), TRIBBLE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "electro"), ELECTRO);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "dendro"), DENDRO);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "pyro"), PYRO);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "hydro"), HYDRO);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "cyro"), CYRO);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "anemo"), ANEMO);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("kusanali", "geo"), GEO);
    }
}