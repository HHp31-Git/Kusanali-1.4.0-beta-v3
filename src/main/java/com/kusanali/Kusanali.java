package com.kusanali;

import com.kusanali.entity.other.SeedEffectTracker;
import com.kusanali.event.element_adhering.*;
import com.kusanali.event.element_enchant.ElementBowEnchantEvent;
import com.kusanali.event.element_enchant.ElementEnchantEvent;
import com.kusanali.event.element_enchant.EnchantBowSetting;
import com.kusanali.event.element_reaction.*;
import com.kusanali.event.element_enchant.EnchantWeaponSetting;
import com.kusanali.event.item_getter.EmeraldGetter;
import com.kusanali.event.reaction_status.SuperconductivityEvent;
import com.kusanali.event.special_item.*;
import com.kusanali.register.*;
import com.kusanali.server.*;
import com.kusanali.specialitem.combats.FloatDream;
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
        //注册类
        ModEffects.register();
        ModDamageTypes.register();
        ModItems.initialize();
        ModBlocks.initialize();
        ModItemGroup.initialize();
        ModSounds.register();
        ModPaintings.init();
        ModLootTable.register();
        ModTrades.register();
        ModEnchants.register();
        //特殊物品行为类
        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        TribbleEvent.register();
        TribbleReactionEvent.register();
        //元素附着类
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
        ElementDamageEvent.register();
        ElementEnchantEvent.onInitialize();
        //元素反应类
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
        //附魔设置类
        EnchantWeaponSetting.register();
        ElementBowEnchantEvent.onInitialize();
        EnchantBowSetting.register();
        //世界生成类
        ModWorldGeneration.register();
        AjiLeavesDrop.register();
        //物品类
        EmeraldGetter.register();
        //HUD类
        FloatDreamHander.register();
        ClientHander.register();
        ClientOverlayRenderer.register();
        ClientMessageOverlay.register();
        ClientVisionOverlay.register();
        //树木去皮注册
        StrippableBlockRegistry.register(ModBlocks.AJI_LOG, ModBlocks.STRIPPED_AJI_LOG);
        StrippableBlockRegistry.register(ModBlocks.AJI_WOOD, ModBlocks.STRIPPED_AJI_WOOD);
        //可燃烧方块注册
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_SAPLING, 60, 100);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_AJI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_AJI_WOOD, 5, 5);
        //生物注册
        ServerTickEvents.END_WORLD_TICK.register(SeedEffectTracker::tickAll);

        LOGGER.info("All settings done");
    }
}