package com.kusanali.register;

import com.kusanali.Kusanali;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> ELEMENT =
            //元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element"));

    public static final RegistryKey<DamageType> REACTION_TYPE_1 =
            //增幅反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_1"));

    public static final RegistryKey<DamageType> REACTION_TYPE_2 =
            //剧变反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_2"));

    public static final RegistryKey<DamageType> REACTION_TYPE_3 =
            //结晶反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_3"));

    public static final RegistryKey<DamageType> REACTION_TYPE_4 =
            //激化反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_4"));

    public static final RegistryKey<DamageType> REACTION_TYPE_5 =
            //月反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_5"));

    public static final RegistryKey<DamageType> REACTION_TYPE_6 =
            //星反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_6"));

    public static DamageSource element(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT));
    }
    public static DamageSource reaction_type_1(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_1));
    }
    public static DamageSource reaction_type_2(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_2));
    }
    public static DamageSource reaction_type_3(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_3));
    }
    public static DamageSource reaction_type_4(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_4));
    }
    public static DamageSource reaction_type_5(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_5));
    }public static DamageSource reaction_type_6(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_6));
    }

    public static void register() {}
}
