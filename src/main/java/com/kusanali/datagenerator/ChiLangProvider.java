package com.kusanali.datagenerator;

import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ChiLangProvider extends FabricLanguageProvider {
    public ChiLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.ARANAS_FLOWER, "兰那罗之花");
        translationBuilder.add(ModItems.BLESSED_SEED, "祝福之种");
        translationBuilder.add(ModItems.BLESSED_BENCH, "祝福之枝");
        translationBuilder.add(ModItems.FLOAT_DREAM, "千夜浮梦");
        translationBuilder.add(ModItems.CANDIED_NUT, "枣椰蜜糖");
        translationBuilder.add(ModItems.HALVAMAZE, "哈瓦玛玛兹");
        translationBuilder.add(ModItems.COROLLA, "兰那罗花冠");
        translationBuilder.add(ModItems.IVE_NEVER_FORGOTTEN, "我不曾忘记");
        translationBuilder.add(ModItems.CLIENT, "虚空终端");
        translationBuilder.add(ModItems.AJILENAKH, "枣椰");

        translationBuilder.add(ModItems.ANEMO_ITEM, "风元素");
        translationBuilder.add(ModItems.CYRO_ITEM, "冰元素");
        translationBuilder.add(ModItems.ELECTRO_ITEM, "雷元素");
        translationBuilder.add(ModItems.GEO_ITEM, "岩元素");
        translationBuilder.add(ModItems.HYDRO_ITEM, "水元素");
        translationBuilder.add(ModItems.PYRO_ITEM, "火元素");
        translationBuilder.add(ModItems.DENDRO_ITEM, "草元素");

        translationBuilder.add(ModBlocks.AJI_LEAVES, "枣椰树叶");
        translationBuilder.add(ModBlocks.AJI_LOG, "枣椰原木");
        translationBuilder.add(ModBlocks.AJI_PLANKS, "枣椰木板");
        translationBuilder.add(ModBlocks.AJI_SAPLING, "枣椰树苗");
        translationBuilder.add(ModBlocks.AJI_WOOD, "枣椰木");
        translationBuilder.add(ModBlocks.STRIPPED_AJI_LOG, "去皮枣椰原木");
        translationBuilder.add(ModBlocks.STRIPPED_AJI_WOOD, "去皮枣椰木");
        translationBuilder.add(ModBlocks.SUMIRU_ROSE, "须弥蔷薇");

        translationBuilder.add("painting.kusanali.nhd_1.title", "2024贺图");
        translationBuilder.add("painting.kusanali.nhd_2.title", "2023贺图");
        translationBuilder.add("painting.kusanali.nhd_3.title", "2025贺图");

        translationBuilder.add("painting.kusanali.nhd_1.author", "Mihoyo");
        translationBuilder.add("painting.kusanali.nhd_2.author", "Mihoyo");
        translationBuilder.add("painting.kusanali.nhd_3.author", "Mihoyo");

        translationBuilder.add(ModItems.IVE_NEVER_FORGOTTEN.getTranslationKey() + ".desc", "I've Never Forgotten");


        translationBuilder.add("itemGroup.kusanali", "吉祥草之音");
        translationBuilder.add("item.kusanali.float_dream.tooltip_1",
                "浮映千夜之梦的灯盏，苍翠的光中流溢着遥世的歌");
        translationBuilder.add("item.kusanali.float_dream.tooltip_2",
                "可对处于燃烧状态的敌人造成额外伤害");
        translationBuilder.add("item.kusanali.float_dream.tooltip_4",
                "10 重击伤害");
        translationBuilder.add("item.kusanali.float_dream.tooltip_3",
                "7 普通攻击伤害");
        translationBuilder.add("item.kusanali.halvamaze.tooltip_1",
                "据说玛兹在须弥有「智慧」的意思，如此精致玲珑的「智慧」，着实令人无法抗拒");

        translationBuilder.add("key.kusanali.activate_float_dream", "心景幻成");
        translationBuilder.add("key.kusanali.e_float_dream", "所识遍记");
        translationBuilder.add("effect.kusanali.magic_damage", "摩耶之殿");
        translationBuilder.add("effect.kusanali.tribble", "灭净三业");
        translationBuilder.add("category.kusanali.abilities", "武器能力-千夜浮梦");
        translationBuilder.add("effect.kusanali.pyro", "火元素附着");
        translationBuilder.add("effect.kusanali.hydro", "水元素附着");
        translationBuilder.add("effect.kusanali.anemo", "风元素附着");
        translationBuilder.add("effect.kusanali.electro", "雷元素附着");
        translationBuilder.add("effect.kusanali.dendro", "草元素附着");
        translationBuilder.add("effect.kusanali.cryo", "冰元素附着");
        translationBuilder.add("effect.kusanali.geo", "岩元素附着");
        translationBuilder.add("effect.kusanali.freezing", "冻结");
        translationBuilder.add("effect.kusanali.superconductive", "超导");
        translationBuilder.add("effect.kusanali.electrify", "感电");
        translationBuilder.add("effect.kusanali.burning", "燃烧");
        translationBuilder.add("effect.kusanali.intensify", "激化");

        translationBuilder.add("death_attack.element", "%1$s 被元素攻击击杀");
        translationBuilder.add("death.attack.element.player",   "%1$s 被元素攻击击杀");
        translationBuilder.add("death_attack.reaction_type_1", "%1$s 死于增幅反应");
        translationBuilder.add("death.attack.reaction_type_1.player",   "%1$s 死于增幅反应");
        translationBuilder.add("death_attack.reaction_type_2", "%1$s 死于剧变反应");
        translationBuilder.add("death.attack.reaction_type_2.player",   "%1$s 死于剧变反应");
        translationBuilder.add("death_attack.reaction_type_3", "%1$s 死于结晶反应");
        translationBuilder.add("death.attack.reaction_type_3.player",   "%1$s 死于结晶反应");
        translationBuilder.add("death_attack.reaction_type_4", "%1$s 死于激化反应");
        translationBuilder.add("death.attack.reaction_type_4.player",   "%1$s 死于激化反应");
        translationBuilder.add("death_attack.reaction_type_5", "%1$s 死于月反应");
        translationBuilder.add("death.attack.reaction_type_5.player",   "%1$s 死于月反应");
        translationBuilder.add("death_attack.reaction_type_6", "%1$s 死于星反应");
        translationBuilder.add("death.attack.reaction_type_6.player",   "%1$s 死于星反应");
    }
}
