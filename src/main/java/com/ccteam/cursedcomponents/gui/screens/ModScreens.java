package com.ccteam.cursedcomponents.gui.screens;

import com.ccteam.cursedcomponents.gui.containers.ModContainers;
import com.ccteam.cursedcomponents.gui.screens.custom.DimensionalQuarryScreen;
import com.ccteam.cursedcomponents.gui.screens.custom.ItemFilterScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ModScreens {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModContainers.DIMENSIONAL_QUARRY_CONTAINER.get(),
                DimensionalQuarryScreen::new
        );
        event.register(
                ModContainers.ITEM_FILTER_CONTAINER.get(),
                ItemFilterScreen::new
        );
    }
}
