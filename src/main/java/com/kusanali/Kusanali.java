package com.kusanali;

import com.kusanali.event.ClientEvent;
import com.kusanali.event.CorollaEvent;
import com.kusanali.register.ModItemGroup;
import com.kusanali.register.ModItems;
import com.kusanali.register.ModSounds;
import com.kusanali.specialitem.FloatDream;
import com.kusanali.world.LootTableModify;
import com.kusanali.world.dimension.Dream_1ChuckGen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Kusanali implements ModInitializer {

	public static final String MOD_ID = "kusanali";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<World> PINK_SKY_WORLD =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(MOD_ID, "dream_di_1"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
        ModItemGroup.initialize();
        ModItems.initialize();
        ModSounds.register();

        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        ClientEvent.register();
        LootTableModify.modify();

        ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);

        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "dream_di_1"), Dream_1ChuckGen.CODEC);
    }
    private void onWorldTick(ServerWorld world) {
        if (world.getRegistryKey().equals(PINK_SKY_WORLD)) {
            world.setTimeOfDay(6000); // 正午
        }
    }
}