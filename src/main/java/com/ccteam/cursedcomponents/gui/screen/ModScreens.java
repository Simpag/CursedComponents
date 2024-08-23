package com.ccteam.cursedcomponents.gui.screen;

import com.ccteam.cursedcomponents.gui.container.ModContainers;
import com.ccteam.cursedcomponents.gui.screen.custom.DimensionalQuarryScreen;
import com.ccteam.cursedcomponents.gui.screen.custom.ItemFilterScreen;
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
