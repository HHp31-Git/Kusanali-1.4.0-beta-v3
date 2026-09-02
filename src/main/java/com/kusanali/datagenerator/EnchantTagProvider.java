package com.kusanali.datagenerator;

import com.kusanali.register.ModEnchants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static com.kusanali.Kusanali.MOD_ID;

public class EnchantTagProvider extends FabricTagProvider<Enchantment> {
    public EnchantTagProvider(FabricDataOutput output,
                              CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ENCHANTMENT, registriesFuture);
    }
    public static final TagKey<Enchantment> ELEMENT_WEAPON =
            TagKey.of(RegistryKeys.ENCHANTMENT, new Identifier(MOD_ID, "element_weapon"));
    public static final TagKey<Enchantment> ELEMENT_BOW =
            TagKey.of(RegistryKeys.ENCHANTMENT, new Identifier(MOD_ID, "element_bow"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ELEMENT_WEAPON)
                .add(ModEnchants.ANEMO_ENCHANT)
                .add(ModEnchants.GEO_ENCHANT)
                .add(Enchantments.FIRE_ASPECT)
                .add(ModEnchants.HYDRO_ENCHANT)
                .add(ModEnchants.DENDRO_ENCHANT)
                .add(ModEnchants.CYRO_ENCHANT)
                .add(ModEnchants.ELECTRO_ENCHANT);
        getOrCreateTagBuilder(ELEMENT_BOW)
                .add(ModEnchants.ANEMO_ENCHANT_BOW)
                .add(ModEnchants.GEO_ENCHANT_BOW)
                .add(Enchantments.FLAME)
                .add(ModEnchants.HYDRO_ENCHANT_BOW)
                .add(ModEnchants.DENDRO_ENCHANT_BOW)
                .add(ModEnchants.CYRO_ENCHANT_BOW)
                .add(ModEnchants.ELECTRO_ENCHANT_BOW);
    }
}
