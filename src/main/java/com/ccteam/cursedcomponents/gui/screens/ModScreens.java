package com.ccteam.cursedcomponents.gui.screens;

import com.ccteam.cursedcomponents.gui.containers.ModContainers;
import com.ccteam.cursedcomponents.gui.screens.custom.DimensionalQuarryScreen;
import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

public class ModScreens {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModContainers.DIMENSIONAL_QUARRY_CONTAINER.get(),
                DimensionalQuarryScreen::new
        );
    }
}
