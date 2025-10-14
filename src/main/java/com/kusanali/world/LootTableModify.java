package com.kusanali.world;

import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class LootTableModify {
    private static final Identifier JUNGLE_TEMPLE =
            new Identifier("minecraft","chests/jungle_temple");
    private static final Identifier JUNGLE_TEMPLE_DISPENSER =
            new Identifier("minecraft","chests/jungle_temple_dispenser");
    private static final Identifier WITHER =
            new Identifier("minecraft", "entities/wither");
    private static final Identifier ENDER_DRAGON =
            new Identifier("minecraft", "entities/ender_dragon");
    private static final Identifier ELDER_GUARDIAN =
            new Identifier("minecraft", "entities/elder_guardian");
    private static final Identifier WARDEN =
            new Identifier("minecraft", "entities/warden");

    public static void modify(){
        LootTableEvents.MODIFY.register(
                (resourceManager, lootManager, id, tableBuilder, source) -> {
                    if (JUNGLE_TEMPLE.equals(id)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.3f))
                                .with(ItemEntry.builder(ModItems.BLESSED_SEED))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
                        tableBuilder.pool(poolBuilder);
                    }
                    if (JUNGLE_TEMPLE.equals(id)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.2f))
                                .with(ItemEntry.builder(ModItems.IVE_NEVER_FORGOTTEN))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
                        tableBuilder.pool(poolBuilder);
                    }
                    if (JUNGLE_TEMPLE_DISPENSER.equals(id)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.5f))
                                .with(ItemEntry.builder(ModItems.BLESSED_SEED))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
                        tableBuilder.pool(poolBuilder);
                    }
                    if (JUNGLE_TEMPLE_DISPENSER.equals(id)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.8f))
                                .with(ItemEntry.builder(ModItems.IVE_NEVER_FORGOTTEN))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
                        tableBuilder.pool(poolBuilder);
                    }
                    if (JUNGLE_TEMPLE_DISPENSER.equals(id)){
                        LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(0.5f))
                                .with(ItemEntry.builder(ModItems.BLESSED_BENCH))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
                        tableBuilder.pool(poolBuilder);
                    }
                    if (WITHER.equals(id)){
                        LootPool.Builder diamondPoolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(1.0F).build())
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)).build());
                        tableBuilder.pool(diamondPoolBuilder.build());
                    }
                    if (ENDER_DRAGON.equals(id)){
                        LootPool.Builder diamondPoolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(1.0F).build())
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)).build());
                        tableBuilder.pool(diamondPoolBuilder.build());
                    }
                    if (ELDER_GUARDIAN.equals(id)){
                        LootPool.Builder diamondPoolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(1.0F).build())
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)).build());
                        tableBuilder.pool(diamondPoolBuilder.build());
                    }
                    if (WARDEN.equals(id)){
                        LootPool.Builder diamondPoolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(1.0F).build())
                                .with(ItemEntry.builder(ModItems.ARANAS_FLOWER))
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)).build());
                        tableBuilder.pool(diamondPoolBuilder.build());
                    }
                });
    }
}
