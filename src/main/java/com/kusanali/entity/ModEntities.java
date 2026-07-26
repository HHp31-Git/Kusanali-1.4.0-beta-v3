package com.kusanali.entity;

import com.kusanali.entity.custom.DendroSeedEntity;
import com.kusanali.entity.custom.SeedProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<DendroSeedEntity> DENDRO_SEED =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "dendro_seed"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, DendroSeedEntity::new)
                            .dimensions(EntityDimensions.fixed(0.75f, 0.875f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(1)
                            .build()
            );
    public static final EntityType<SeedProjectileEntity> SEED_PROJECTILE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier("kusanali", "seed_projectile"),
                    FabricEntityTypeBuilder.create(
                                    SpawnGroup.MISC,
                                    SeedProjectileEntity::new
                            )
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(20)
                            .build()
            );
}
