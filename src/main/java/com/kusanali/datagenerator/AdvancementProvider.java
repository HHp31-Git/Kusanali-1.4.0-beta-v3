package com.kusanali.datagenerator;

import com.kusanali.Kusanali;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class AdvancementProvider extends FabricAdvancementProvider {
    public AdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement rootAdvancement = Advancement.Builder.create()
                .display(
                        ModItems.ARANAS_FLOWER,
                        Text.literal("吉祥草之音"),
                        Text.literal("For the Lesser Lord Kusanali!"),
                        new Identifier("textures/gui/advancements/backgrounds/root.png"), // 使用的背景图片
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.recipe(new Identifier(Kusanali.MOD_ID, "blessed_seed")
                ))
                .criterion("got_flower", InventoryChangedCriterion.Conditions.items(ModItems.ARANAS_FLOWER))
                .build(consumer, Kusanali.MOD_ID + "/root");
        Advancement gotFrAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        ModItems.AJILENAKH,
                        Text.literal("枣椰"),
                        Text.literal("沙漠之实"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.recipe(new Identifier(Kusanali.MOD_ID, "candied_nut")))
                .criterion("got_aji", InventoryChangedCriterion.Conditions.items(ModItems.AJILENAKH))
                .build(consumer, Kusanali.MOD_ID + "/got_aji");
        Advancement gotCanAjAdvancement = Advancement.Builder.create().parent(gotFrAdvancement)
                .display(
                        ModItems.CANDIED_NUT,
                        Text.literal("枣椰蜜糖"),
                        Text.literal("SWEET!!"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.recipe(new Identifier(Kusanali.MOD_ID, "halvamaze")))
                .criterion("got_can", InventoryChangedCriterion.Conditions.items(ModItems.CANDIED_NUT))
                .build(consumer, Kusanali.MOD_ID + "/got_can");
        Advancement gotAjiAdvancement = Advancement.Builder.create().parent(gotCanAjAdvancement)
                .display(
                        ModItems.HALVAMAZE,
                        Text.literal("哈瓦玛玛兹"),
                        Text.literal("玲珑的「智慧」"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(1500))
                .criterion("got_hav", InventoryChangedCriterion.Conditions.items(ModItems.HALVAMAZE))
                .build(consumer, Kusanali.MOD_ID + "/got_hav");
        Advancement gotCoAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        ModItems.COROLLA,
                        Text.literal("花冠"),
                        Text.literal("致兰那罗的同行者"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.recipe(new Identifier(Kusanali.MOD_ID, "float_dream")))
                .criterion("got_co", InventoryChangedCriterion.Conditions.items(ModItems.COROLLA))
                .build(consumer, Kusanali.MOD_ID + "/got_co");
        Advancement gotCiAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        ModItems.CLIENT,
                        Text.literal("虚空终端"),
                        Text.literal("统合人民之智慧"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(500))
                .criterion("got_cli", InventoryChangedCriterion.Conditions.items(ModItems.CLIENT))
                .build(consumer, Kusanali.MOD_ID + "/got_cli");
        Advancement gotAthFlAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        ModItems.FLOAT_DREAM,
                        Text.literal("千夜浮梦"),
                        Text.literal("苍翠的光中流溢着遥世的歌"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.recipe(new Identifier(Kusanali.MOD_ID, "ive_never_forgotten")))
                .criterion("got_fl", InventoryChangedCriterion.Conditions.items(ModItems.FLOAT_DREAM))
                .build(consumer, Kusanali.MOD_ID + "/got_fl");
        Advancement gotIveFlAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        ModItems.IVE_NEVER_FORGOTTEN,
                        Text.literal("我不曾忘记"),
                        Text.literal("致千树之王"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(1500))
                .criterion("got_ive", InventoryChangedCriterion.Conditions.items(ModItems.IVE_NEVER_FORGOTTEN))
                .build(consumer, Kusanali.MOD_ID + "/got_ive");
        Advancement gotTroupeSwordAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                .display(
                        ModItems.TROUPE_SWORD,
                        Text.literal("镀金旅团"),
                        Text.literal("沙中斗士之刃"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(200))
                .criterion("got_ts", InventoryChangedCriterion.Conditions.items(ModItems.TROUPE_SWORD))
                .build(consumer, Kusanali.MOD_ID + "/got_ts");
    }
}
