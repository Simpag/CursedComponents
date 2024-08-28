package com.ccteam.cursedcomponents.gui.screen;

import com.ccteam.cursedcomponents.gui.container.ModContainers;
import com.ccteam.cursedcomponents.gui.screen.custom.DimensionalQuarryItemFilterScreen;
import com.ccteam.cursedcomponents.gui.screen.custom.DimensionalQuarryScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ModScreens {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModContainers.DIMENSIONAL_QUARRY_CONTAINER.get(),
                DimensionalQuarryScreen::new
        );
        event.register(
                ModContainers.DIMENSIONAL_QUARRY_ITEM_FILTER_CONTAINER.get(),
                DimensionalQuarryItemFilterScreen::new
        );
    }
}
