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
    public static final Identifier JUNGLE_DISPENSER =
            new Identifier("minecraft", "chest/jungle_temple_dispenser");
    public static final Identifier END_CITY =
            new Identifier("minecraft", "chest/end_city_treasure");
    public static void register() {
        LootTableEvents.MODIFY.register(
                (resourceManager, lootManager,
                 identifier, builder, lootTableSource) -> {
                    if (JUNGLE_DISPENSER.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.9F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)));
                        builder.pool(poolBuilder.build());
                    }
                    if (JUNGLE_TEMPLE.equals(identifier)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.4F))
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)));
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
                }
        );
    }
}
