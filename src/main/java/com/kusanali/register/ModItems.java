package com.kusanali.register;

import com.kusanali.specialitem.Client;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.specialitem.Halvamaze;
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
            register("ive_never_forgotten", new MusicDiscItem(15,
                    ModSounds.IVE_NEVER_FORGOTTEN,
                    new Item.Settings()
                            .rarity(Rarity.RARE)
                            .maxCount(1),255
            ));
    public static final Item COROLLA =
            register("corolla", new ArmorItem(ModArmorMaterial.COROLLA, ArmorItem.Type.HELMET,
                    new Item.Settings()
                            .rarity(Rarity.EPIC)));
    public static final Item CLIENT =
            register("client", new Client(ModArmorMaterial.CLIENT, Client.Type.HELMET,
                    new Item.Settings()
                            .rarity(Rarity.RARE)));
    public static final Item FLOAT_DREAM =
            register("float_dream", new FloatDream(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.EPIC)
                    .maxDamage(1027)));
    public static final Item CANDIED_NUT =
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
            register("ajilenakh", new Item(new Item.Settings()
                    .maxCount(64)
                    .food(new FoodComponent.Builder()
                            .hunger(3)
                            .saturationModifier(0.3f)
                            .build())));
    public static final Item HALVAMAZE =
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
            register("aranas_flower", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.EPIC)));
    public static final Item BLESSED_BENCH =
            register("blessed_bench", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)));
    public static final Item BLESSED_SEED =
            register("blessed_seed", new Item(new Item.Settings()
                    .maxCount(64)
                    .rarity(Rarity.RARE)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("kusanali", name), item);
    }

    public static void initialize() {
    }
}
