package com.kusanali.datagenerator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public DamageTypeTagProvider(FabricDataOutput output,
                                 CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, net.minecraft.registry.RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    public static final TagKey<DamageType> PHYSICAL =
            TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("kusanali", "physical"));
    public static final TagKey<DamageType> REACTION =
            TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("kusanali", "reaction"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(PHYSICAL)
                .add(new Identifier("minecraft", "mob_attack"))
                .add(new Identifier("minecraft", "player_attack"))
                .add(new Identifier("minecraft", "arrow"))
                .add(new Identifier("minecraft", "trident"));
        getOrCreateTagBuilder(REACTION)
                .add(new Identifier("kusanali", "reaction_type_1"))
                .add(new Identifier("kusanali", "reaction_type_2"))
                .add(new Identifier("kusanali", "reaction_type_3"))
                .add(new Identifier("kusanali", "reaction_type_4"))
                .add(new Identifier("kusanali", "reaction_type_5"))
                .add(new Identifier("kusanali", "reaction_type_6"));
    }
}
