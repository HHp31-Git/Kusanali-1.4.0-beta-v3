package com.kusanali.register;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;

public class ModTrades {
    public static void register() {
        TradeOfferHelper.registerWanderingTraderOffers(1, factories ->
                factories.add((entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, 2),
                new ItemStack(ModItems.AJILENAKH, 1),
                12, 10, 0.05F)));
        TradeOfferHelper.registerWanderingTraderOffers(1, factories ->
                factories.add((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 2),
                        new ItemStack(ModBlocks.AJI_SAPLING, 1),
                        12, 10, 0.05F)));
        TradeOfferHelper.registerWanderingTraderOffers(1, factories ->
                factories.add((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 4),
                        new ItemStack(ModBlocks.PADISARAH, 1),
                        5, 20, 0.05F)));
        TradeOfferHelper.registerWanderingTraderOffers(1, factories ->
                factories.add((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModBlocks.SUMIRU_ROSE, 1),
                        12, 10, 0.05F)));
        TradeOfferHelper.registerWanderingTraderOffers(1, factories ->
                factories.add((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.EMERALD_SLIVER, 1),
                        9, 10, 0.05F)));
        TradeOfferHelper.registerWanderingTraderOffers(1, factories ->
                factories.add((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 3),
                        new ItemStack(ModItems.EMERALD_FRAGMENT, 1),
                        4, 10, 0.05F)));
    }
}
