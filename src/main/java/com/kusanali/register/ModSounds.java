package com.kusanali.register;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent IVE_NEVER_FORGOTTEN = registerSound("ive_never_forgotten");
    public static SoundEvent registerSound(String sound) {
        Identifier identifier = new Identifier("kusanali", sound);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
    public static void register() {
    }
}
