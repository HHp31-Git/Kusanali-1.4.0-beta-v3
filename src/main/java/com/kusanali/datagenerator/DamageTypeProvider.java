package com.kusanali.datagenerator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class DamageTypeProvider extends FabricDynamicRegistryProvider {
    public DamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        for (int i = 1; i <= 6; i++) {
            Identifier id = new Identifier("kusanali", "reaction_type_" + i);
            RegistryKey<DamageType> key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id);
            entries.add(key, new DamageType(
                    "reaction",
                    0.0f
            ));
        }
    }

    @Override
    public String getName() {
        return "Damage Types";
    }
}
