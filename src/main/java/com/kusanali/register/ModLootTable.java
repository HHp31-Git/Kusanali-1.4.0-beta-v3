package com.kusanali.register;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTable {
    public static final Identifier JUNGLE_TEMPLE =
            new Identifier("minecraft", "chest/jungle_temple");
    public static final Identifier END_CITY =
            new Identifier("minecraft", "chest/end_city_treasure");
    public static final Identifier ANCIENT_CITY =
            new Identifier("minecraft", "chest/ancient_city");
    public static final Identifier SHIPWRECK_TREASURE =
            new Identifier("minecraft", "chest/shipwreck_treasure");
    public static final Identifier SHIPWRECK_MAP =
            new Identifier("minecraft", "chest/shipwreck_map");
    public static final Identifier SHIPWRECK_SUPPLY =
            new Identifier("minecraft", "chest/shipwreck_supply");
    public static final Identifier UNDERWATER_RUIN_BIG =
            new Identifier("minecraft", "chest/underwater_ruin_big");
    public static final Identifier UNDERWATER_RUIN_SMALL =
            new Identifier("minecraft", "chest/underwater_ruin_small");
    public static void register() {
        LootTableEvents.MODIFY.register(
                (resourceManager, lootManager,
                 identifier, builder, lootTableSource) -> {
                    if (JUNGLE_TEMPLE.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.9F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 5.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (JUNGLE_TEMPLE.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.8F))
                                .with(ItemEntry.builder(ModItems.IVE_NEVER_FORGOTTEN))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (END_CITY.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.8F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (ANCIENT_CITY.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.8F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (SHIPWRECK_TREASURE.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.8F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (SHIPWRECK_SUPPLY.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.4F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (SHIPWRECK_MAP.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.4F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (UNDERWATER_RUIN_BIG.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.7F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (UNDERWATER_RUIN_SMALL.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.4F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)));
                        builder.pool(poolBuilder.build());
                    }
                }
        );
    }
}
