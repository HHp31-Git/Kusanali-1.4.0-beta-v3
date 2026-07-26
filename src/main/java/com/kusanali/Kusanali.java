package com.kusanali;

import com.kusanali.entity.other.SeedEffectTracker;
import com.kusanali.event.element_adhering.*;
import com.kusanali.event.element_reaction.*;
import com.kusanali.event.reaction_middle.SuperconductivityEvent;
import com.kusanali.event.special_item.*;
import com.kusanali.register.*;
import com.kusanali.server.FloatDreamHander;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.world.feature.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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

        ModEffects.register();
        ModDamageTypes.register();
        ModItems.initialize();
        ModBlocks.initialize();
        ModItemGroup.initialize();
        ModSounds.register();
        ModPaintings.init();
        ModLootTable.register();

        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        ClientEvent.register();
        TribbleEvent.register();
        TribbleReactionEvent.register();

        ElectroEvent.register();
        HydroEvent.register();
        PyroEvent.register();
        IronManAttackEvent.register();
        SnowBallCyroEvent.register();
        DripstoneGeoEvent.register();
        ElementEntityAttackEvent.register();
        SlimeRandomEvent.register();
        CyroSnowEvent.register();
        RainingHydroEvent.register();

        EvaporationEvent.register();
        MeltEvent.register();
        SuperconductivityEvent.register();
        SuperconductivityEventMain.register();
        DiffusionEvent.register();
        OverloadEvent.register();
        FreezingEvent.register();
        IceSpawnEvent.register();
        IceBreakEvent.register();
        ElectrifyEvent.register();
        DendroBurnEvent.register();
        BoomEvent.register();
        CrystallizationEvent.register();
        IntensifyEvent.register();
        SuperIntensifyEvent.register();
        GrowingIntensifyEvent.register();

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

        ServerTickEvents.END_WORLD_TICK.register(SeedEffectTracker::tickAll);
    }
}