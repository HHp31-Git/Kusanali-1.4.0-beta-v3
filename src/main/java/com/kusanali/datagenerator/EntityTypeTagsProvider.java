package com.kusanali.datagenerator;

import com.kusanali.Kusanali;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagsProvider extends FabricTagProvider.EntityTypeTagProvider {


    public EntityTypeTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup>
            completableFuture) {
        super(output, completableFuture);
    }
    public static final TagKey<EntityType<?>> ELEMENTS_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "elements_entity"));
    public static final TagKey<EntityType<?>> PYRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "pyro_entity"));
    public static final TagKey<EntityType<?>> CYRO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "cyro_entity"));
    public static final TagKey<EntityType<?>> GEO_ENTITY =
            TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Kusanali.MOD_ID, "geo_entity"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(PYRO_ENTITY)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.BLAZE)
                .add(EntityType.STRIDER);

        getOrCreateTagBuilder(CYRO_ENTITY)
                .add(EntityType.SNOW_GOLEM);

        getOrCreateTagBuilder(GEO_ENTITY)
                .add(EntityType.IRON_GOLEM);

        getOrCreateTagBuilder(ELEMENTS_ENTITY)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.BLAZE)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.STRIDER);
    }
}
