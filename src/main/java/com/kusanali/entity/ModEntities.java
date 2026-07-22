package com.kusanali.entity;

import com.kusanali.entity.custom.DendroSeedEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<DendroSeedEntity> DENDRO_SEED =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "dendro_seed"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, DendroSeedEntity::new)
                            .dimensions(EntityDimensions.fixed(0.5f,0.5f)).build());
}
