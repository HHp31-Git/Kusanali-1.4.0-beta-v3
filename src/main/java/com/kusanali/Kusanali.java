package com.kusanali;

import com.kusanali.event.*;
import com.kusanali.register.*;
import com.kusanali.server.FloatDreamHander;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.world.LootTableModify;
import com.kusanali.world.feature.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Kusanali implements ModInitializer {

	public static final String MOD_ID = "kusanali";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("For the Lesser Lord Kusanali");


        ModItemGroup.initialize();
        ModItems.initialize();
        ModBlocks.initialize();
        ModSounds.register();
        ModPaintings.init();
        ModEffects.register();

        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        ClientEvent.register();
        BedTelepotEvent.register();
        TribbleEvent.register();

        AjiLeavesDrop.register();

        FloatDreamHander.register();

        ModWorldGeneration.register();

        LootTableModify.modify();

        StrippableBlockRegistry.register(ModBlocks.AJI_LOG, ModBlocks.STRIPPED_AJI_LOG);
        StrippableBlockRegistry.register(ModBlocks.AJI_WOOD, ModBlocks.STRIPPED_AJI_WOOD);

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_SAPLING, 60, 100);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_AJI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_AJI_WOOD, 5, 5);
    }
}