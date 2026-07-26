package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class EngLangProvider extends FabricLanguageProvider {
    public EngLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.ARANAS_FLOWER, "Aranas Flower");
        translationBuilder.add(ModItems.BLESSED_SEED, "Blessed Seed");
        translationBuilder.add(ModItems.BLESSED_BENCH, "Blessed Bench");
        translationBuilder.add(ModItems.FLOAT_DREAM, "A Thousand Floating Dreams");
        translationBuilder.add(ModItems.CANDIED_NUT, "Candied Ajilenakh Nut");
        translationBuilder.add(ModItems.HALVAMAZE, "Halvamaze");
        translationBuilder.add(ModItems.COROLLA, "Aranas Corolla");
        translationBuilder.add(ModItems.IVE_NEVER_FORGOTTEN, "I've Never Forgotten");
        translationBuilder.add(ModItems.CLIENT, "Void Client");
        translationBuilder.add(ModItems.AJILENAKH, "ajilenakh nut");

        translationBuilder.add(ModItems.ANEMO_ITEM, "anemo item");
        translationBuilder.add(ModItems.CYRO_ITEM, "cyro item");
        translationBuilder.add(ModItems.ELECTRO_ITEM, "electro item");
        translationBuilder.add(ModItems.GEO_ITEM, "geo item");
        translationBuilder.add(ModItems.HYDRO_ITEM, "hydro item");
        translationBuilder.add(ModItems.PYRO_ITEM, "pyro item");
        translationBuilder.add(ModItems.DENDRO_ITEM, "dendro item");

        translationBuilder.add(ModBlocks.AJI_LEAVES, "ajilenakh leaves");
        translationBuilder.add(ModBlocks.AJI_LOG, "ajilenakh log");
        translationBuilder.add(ModBlocks.AJI_PLANKS, "ajilenakh planks");
        translationBuilder.add(ModBlocks.AJI_SAPLING, "ajilenakh sapling");
        translationBuilder.add(ModBlocks.AJI_WOOD, "ajilenakh wood");
        translationBuilder.add(ModBlocks.STRIPPED_AJI_LOG, "stripped ajilenakh log");
        translationBuilder.add(ModBlocks.STRIPPED_AJI_WOOD, "stripped ajilenakh wood");
        translationBuilder.add(ModBlocks.SUMIRU_ROSE, "sumiru rose");

        translationBuilder.add("painting.kusanali.nhd_1.title", "2024Birthday Painting");
        translationBuilder.add("painting.kusanali.nhd_3.title", "2025Birthday Painting");
        translationBuilder.add("painting.kusanali.nhd_2.title", "2023Birthday Painting");

        translationBuilder.add("painting.kusanali.nhd_1.author", "Mihoyo");
        translationBuilder.add("painting.kusanali.nhd_2.author", "Mihoyo");
        translationBuilder.add("painting.kusanali.nhd_3.author", "Mihoyo");

        translationBuilder.add(ModItems.IVE_NEVER_FORGOTTEN.getTranslationKey() + ".desc", "I've Never Forgotten");

        translationBuilder.add("itemGroup.kusanali", "kusanali");
        translationBuilder.add("item.kusanali.float_dream.tooltip_1",
                "The lamps reflecting the dreams of a thousand nights spill out songs from a faraway world in their green light.");
        translationBuilder.add("item.kusanali.float_dream.tooltip_2",
                "Deals extra damage to enemies that are on fire.");
        translationBuilder.add("item.kusanali.float_dream.tooltip_4",
                "10 Impact Damage");
        translationBuilder.add("item.kusanali.float_dream.tooltip_3",
                "7 Normal Attack Damage");
        translationBuilder.add("item.kusanali.halvamaze.tooltip_1",
                "It is said that 'Maz' in Sumiru means 'wisdom' Such exquisite and delicate 'wisdom' is truly irresistible");

        translationBuilder.add("key.kusanali.activate_float_dream", "mind's vision creates illusions");
        translationBuilder.add("key.kusanali.e_float_dream", "All that is known and recorded");
        translationBuilder.add("effect.kusanali.magic_damage", "Hall of Maya");
        translationBuilder.add("effect.kusanali.tribble", "Tribble");
        translationBuilder.add("category.kusanali.abilities", "Ability-Float Dream");
        translationBuilder.add("effect.kusanali.pyro", "Pyro Adhering");
        translationBuilder.add("effect.kusanali.hydro", "Hydro Adhering");
        translationBuilder.add("effect.kusanali.anemo", "Anemo Adhering");
        translationBuilder.add("effect.kusanali.electro", "Electro Adhering");
        translationBuilder.add("effect.kusanali.dendro", "Dendro Adhering");
        translationBuilder.add("effect.kusanali.cryo", "Cryo Adhering");
        translationBuilder.add("effect.kusanali.geo", "Geo Adhering");
        translationBuilder.add("effect.kusanali.freezing", "Freezing");
        translationBuilder.add("effect.kusanali.superconductive", "Superconductive");
        translationBuilder.add("effect.kusanali.electrify", "Electrify");
        translationBuilder.add("effect.kusanali.burning", "Burning");
        translationBuilder.add("effect.kusanali.intensify", "Intensify");


        translationBuilder.add("death.attack.element",          "%1$s was consumed by elemental power");
        translationBuilder.add("death.attack.element.player",   "%1$s was consumed by elemental power");
        translationBuilder.add("death_attack.reaction_type_1", "%1$s was consumed by amplification reaction");
        translationBuilder.add("death.attack.reaction_type_1.player",   "%1$s was consumed by amplification reaction");
        translationBuilder.add("death_attack.reaction_type_2", "%1$s was consumed by dramatic reaction");
        translationBuilder.add("death.attack.reaction_type_2.player",   "%1$s was consumed by dramatic reaction");
        translationBuilder.add("death_attack.reaction_type_3", "%1$s was consumed by crystallization reaction");
        translationBuilder.add("death.attack.reaction_type_3.player",   "%1$s was consumed by crystallization reaction");
        translationBuilder.add("death_attack.reaction_type_4", "%1$s was consumed by intensified reaction");
        translationBuilder.add("death.attack.reaction_type_4.player",   "%1$s was consumed by intensified reaction");
        translationBuilder.add("death_attack.reaction_type_5", "%1$s was consumed by moon reaction");
        translationBuilder.add("death.attack.reaction_type_5.player",   "%1$s was consumed by moon reaction");
        translationBuilder.add("death_attack.reaction_type_6", "%1$s was consumed by star reaction");
        translationBuilder.add("death.attack.reaction_type_6.player",   "%1$s was consumed by star reaction");
    }
}
