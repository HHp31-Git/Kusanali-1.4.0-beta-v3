package com.kusanali.register;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeySet {
    public static KeyBinding ACTIVATE_FLOAT_DREAM;
    public static KeyBinding E_FLOAT_DREAM;

    public static void register() {
        ACTIVATE_FLOAT_DREAM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kusanali.activate_float_dream", // 翻译键
                InputUtil.Type.KEYSYM, // 键盘按键
                GLFW.GLFW_KEY_R, // 默认键位R
                "category.kusanali.abilities" // 按键分类
        ));
        E_FLOAT_DREAM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kusanali.e_float_dream", // 翻译键
                InputUtil.Type.KEYSYM, // 键盘按键
                GLFW.GLFW_KEY_E, // 默认键位R
                "category.kusanali.abilities" // 按键分类
        ));
    }
}
