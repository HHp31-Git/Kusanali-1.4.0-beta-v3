package com.kusanali;

import com.kusanali.event.ClientEvent;
import com.kusanali.event.CorollaEvent;
import com.kusanali.register.*;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.world.LootTableModify;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Kusanali implements ModInitializer {

	public static final String MOD_ID = "kusanali";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
        ModItemGroup.initialize();
        ModItems.initialize();
        ModBlocks.initialize();
        ModSounds.register();
        ModPaintings.init();

        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        ClientEvent.register();
        LootTableModify.modify();

    }
}