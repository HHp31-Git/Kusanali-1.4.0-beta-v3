package com.kusanali.register;

import com.kusanali.specialitem.Client;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.specialitem.Halvamaze;
import com.kusanali.specialitem.elements.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item IVE_NEVER_FORGOTTEN =
            //唱片-我不曾忘记
            register("ive_never_forgotten", new MusicDiscItem(15,
                    ModSounds.IVE_NEVER_FORGOTTEN,
                    new Item.Settings()
                            .rarity(Rarity.RARE)
                            .maxCount(1),255
            ));

    public static final Item COROLLA =
            //兰那罗花冠
            register("corolla", new ArmorItem(ModArmorMaterial.COROLLA, ArmorItem.Type.HELMET,
                    new Item.Settings()
                            .rarity(Rarity.EPIC)));

    public static final Item CLIENT =
            //虚空终端
            register("client", new Client(ModArmorMaterial.CLIENT, Client.Type.HELMET,
                    new Item.Settings()
                            .rarity(Rarity.RARE)));

    public static final Item FLOAT_DREAM =
            //千夜浮梦
            register("float_dream", new FloatDream(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.EPIC)
                    .maxDamage(1027)));

    public static final Item CANDIED_NUT =
            //枣椰蜜糖
            register("candied_nut", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)
                    .food(new FoodComponent.Builder()
                            .hunger(5)
                            .saturationModifier(0.5f)
                            .alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 0),1.0f)
                            .build())));

    public static final Item AJILENAKH =
            //枣椰
            register("ajilenakh", new Item(new Item.Settings()
                    .maxCount(64)
                    .food(new FoodComponent.Builder()
                            .hunger(3)
                            .saturationModifier(0.3f)
                            .build())));

    public static final Item HALVAMAZE =
            //哈瓦玛玛兹
            register("halvamaze", new Halvamaze(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.EPIC)
                    .food(new FoodComponent.Builder()
                            .hunger(10)
                            .alwaysEdible()
                            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 3),1.0f)
                            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0),1.0f)
                            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1200, 0),1.0f)
                            .build())));

    public static final Item ARANAS_FLOWER =
            //兰那罗之花
            register("aranas_flower", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.EPIC)));

    public static final Item BLESSED_BENCH =
            //祝福之枝
            register("blessed_bench", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)));

    public static final Item BLESSED_SEED =
            //祝福之种
            register("blessed_seed", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)));

    public static final Item ELECTRO_ITEM =
            //雷元素
            register("electro_item", new ElectroItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));
    public static final Item DENDRO_ITEM =
            //草元素
            register("dendro_item", new DendroItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));
    public static final Item PYRO_ITEM =
            //火元素
            register("pyro_item", new PyroItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));
    public static final Item GEO_ITEM =
            //岩元素
            register("geo_item", new GeoItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));
    public static final Item CYRO_ITEM =
            //冰元素
            register("cyro_item", new CyroItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));
    public static final Item ANEMO_ITEM =
            //风元素
            register("anemo_item", new AnemoItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));
    public static final Item HYDRO_ITEM =
            //水元素
            register("hydro_item", new HydroItem(new Item.Settings()
                    .rarity(Rarity.EPIC)));

    public static final Item AKASA_CORE=
            //终端核心
            register("akasa_core", new Item(new Item.Settings()
                    .maxCount(1)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("kusanali", name), item);
    }

    public static void initialize() {
    }
}
