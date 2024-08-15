package com.ccteam.cursedcomponents.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinds {

    public static final Lazy<KeyMapping> LUCKY_ANIMAL_DISMOUNT = Lazy.of(() ->
            new KeyMapping(
                    "key.cursedcomponents.lucky_animal_dismount",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_H,
                    "key.categories.misc"
                    ));

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(LUCKY_ANIMAL_DISMOUNT.get());
    }
}
