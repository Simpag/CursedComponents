package com.ccteam.cursedcomponents.gui.containers;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.gui.containers.custom.DimensionalQuarryContainer;
import com.ccteam.cursedcomponents.gui.containers.custom.ItemFilterContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, CursedComponentsMod.MOD_ID);

    public static final Supplier<MenuType<DimensionalQuarryContainer>> DIMENSIONAL_QUARRY_CONTAINER = CONTAINERS.register(
            "dimensional_quarry_container",
            () -> new MenuType<>(DimensionalQuarryContainer::new, FeatureFlags.DEFAULT_FLAGS)
    );
    public static final Supplier<MenuType<ItemFilterContainer>> ITEM_FILTER_CONTAINER = CONTAINERS.register(
            "item_filter_container",
            () -> new MenuType<>(ItemFilterContainer::new, FeatureFlags.DEFAULT_FLAGS)
    );

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
