package com.kusanali.event.element_enchant;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Set;

import static com.kusanali.Kusanali.MOD_ID;

public class EnchantWeaponSetting {
    private static final Set<String> ELEMENTAL_WEAPON_IDS = Set.of(
            MOD_ID + ":anemo_enchant",
            MOD_ID + ":geo_enchant",
            MOD_ID + ":electro_enchant",
            MOD_ID + ":dendro_enchant",
            MOD_ID + ":hydro_enchant",
            MOD_ID + ":pyro_enchant",
            MOD_ID + ":cyro_enchant"
    );
    public static void register() {
        UseItemCallback.EVENT.register(
                (PlayerEntity player, World world, net.minecraft.util.Hand hand) -> {
                    if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));
                    ItemStack stack = player.getStackInHand(hand);
                    enforceSingleElemental(stack);
                    return TypedActionResult.pass(stack);
                }
        );
    }

    private static void enforceSingleElemental(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasEnchantments()) return;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("Enchantments", 9)) return;

        NbtList old = nbt.getList("Enchantments", 10);
        if (old.size() <= 1) return;

        String kept = null;
        boolean changed = false;
        NbtList fresh = new NbtList();

        for (int i = 0; i < old.size(); i++) {
            NbtCompound e = old.getCompound(i);
            String id = e.getString("id");
            if (ELEMENTAL_WEAPON_IDS.contains(id)) {
                if (kept == null) {
                    kept = id;
                    fresh.add(e.copy());
                } else if (id.equals(kept)) {
                    fresh.add(e.copy());
                } else {
                    changed = true;
                }
            } else {
                fresh.add(e.copy());
            }
        }

        if (changed) {
            nbt.put("Enchantments", fresh);
            if (fresh.isEmpty()) stack.removeSubNbt("Enchantments");
        }
    }
}
