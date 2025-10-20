package com.kusanali.datagenerator;

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

        translationBuilder.add("painting.kusanali.nhd_1.title", "2024贺图");
        translationBuilder.add("painting.kusanali.nhd_2.title", "2023贺图");

        translationBuilder.add("painting.kusanali.nhd_1.author", "Mihoyo");
        translationBuilder.add("painting.kusanali.nhd_2.author", "Mihoyo");

        translationBuilder.add(ModItems.IVE_NEVER_FORGOTTEN.getTranslationKey() + ".desc", "I've Never Forgotten");

        translationBuilder.add("dimension.kusanali.dream_di_1", "梦境之地");

        translationBuilder.add("itemGroup.kusanali", "吉祥草之音");
        translationBuilder.add("item.kusanali.float_dream.tooltip_1",
                "浮映千夜之梦的灯盏，苍翠的光中流溢着遥世的歌");
        translationBuilder.add("item.kusanali.float_dream.tooltip_2",
                "可对处于燃烧状态的敌人造成额外伤害");
        translationBuilder.add("item.kusanali.float_dream.tooltip_4",
                "8 重击伤害");
        translationBuilder.add("item.kusanali.float_dream.tooltip_3",
                "6 普通攻击伤害");
        translationBuilder.add("item.kusanali.halvamaze.tooltip_1",
                "据说玛兹在须弥有「智慧」的意思，如此精致玲珑的「智慧」，着实令人无法抗拒");

        translationBuilder.add("key.kusanali.activate_float_dream", "心景幻成");
        translationBuilder.add("key.kusanali.e_float_dream", "所识遍记");
        translationBuilder.add("effect.kusanali.magic_damage", "摩耶之殿");
        translationBuilder.add("category.kusanali.abilities", "武器能力-千夜浮梦");
    }
}
