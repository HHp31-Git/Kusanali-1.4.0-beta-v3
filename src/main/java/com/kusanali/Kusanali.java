package com.kusanali;

import com.kusanali.event.BedTelepotEvent;
import com.kusanali.event.ClientEvent;
import com.kusanali.event.CorollaEvent;
import com.kusanali.register.*;
import com.kusanali.server.FloatDreamServer;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.world.LootTableModify;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Kusanali implements ModInitializer {

	public static final String MOD_ID = "kusanali";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");
        ModItemGroup.initialize();
        ModItems.initialize();
        ModBlocks.initialize();
        ModSounds.register();
        ModPaintings.init();
        ModEffects.register();

        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        ClientEvent.register();
        LootTableModify.modify();
        BedTelepotEvent.register();

        FloatDreamServer.register();
    }
}