package com.kusanali.register;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup KUSANALI = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.ARANAS_FLOWER))
            .displayName(Text.translatable("itemGroup.kusanali"))
            .entries((context, entries) -> {
                entries.add(ModItems.FLOAT_DREAM);
                entries.add(ModItems.COROLLA);
                entries.add(ModItems.CLIENT);
                entries.add(ModItems.ARANAS_FLOWER);
                entries.add(ModItems.BLESSED_BENCH);
                entries.add(ModItems.BLESSED_SEED);
                entries.add(ModItems.CANDIED_NUT);
                entries.add(ModItems.HALVAMAZE);
                entries.add(ModItems.IVE_NEVER_FORGOTTEN);
                entries.add(ModItems.AJILENAKH);
            })
            .build();
    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, new Identifier("tutorial", "test_group"), KUSANALI);
    }
}
