package com.kusanali.specialitem.combats;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class Client extends ArmorItem {
    public Client(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.kusanali.client.tooltip_1"));
    }
}
