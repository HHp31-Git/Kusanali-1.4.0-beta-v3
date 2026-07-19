package com.kusanali;

import com.kusanali.event.element_adhering.*;
import com.kusanali.event.element_reaction.*;
import com.kusanali.event.reaction_middle.SuperconductivityEvent;
import com.kusanali.event.special_item.AjiLeavesDrop;
import com.kusanali.event.special_item.ClientEvent;
import com.kusanali.event.special_item.CorollaEvent;
import com.kusanali.event.special_item.TribbleEvent;
import com.kusanali.register.*;
import com.kusanali.server.FloatDreamHander;
import com.kusanali.specialitem.FloatDream;
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
        ModDamageTypes.register();
        ModLootTable.register();

        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        ClientEvent.register();
        TribbleEvent.register();

        ElectroEvent.register();
        HydroEvent.register();
        PyroEvent.register();
        IronManAttackEvent.register();
        SnowBallCyroEvent.register();
        DripstoneGeoEvent.register();
        ElementEntityAttackEvent.register();
        SlimeRandomEvent.register();

        EvaporationEvent.register();
        MeltEvent.register();
        SuperconductivityEvent.register();
        SuperconductivityEventMain.register();
        DiffusionEvent.register();
        OverloadEvent.register();
        FreezingEvent.register();
        IceSpawnEvent.register();
        IceBreakEvent.register();

        ElementDamageEvent.register();

        AjiLeavesDrop.register();

        FloatDreamHander.register();

        ModWorldGeneration.register();

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