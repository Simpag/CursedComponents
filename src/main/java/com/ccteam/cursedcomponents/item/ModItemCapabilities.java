package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.energy_storage.ItemStackEnergyStorage;
import com.ccteam.cursedcomponents.item.interfaces.PoweredItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModItemCapabilities {
    public static void registerItemCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (itemStack, context) -> {
                    int capacity = 0;
                    int usage = 0;
                    if (itemStack.getItem() instanceof PoweredItem pi) {
                        capacity = pi.getCapacity();
                        usage = pi.getEnergyUse();
                    }
                    return new ItemStackEnergyStorage(capacity, usage, itemStack);
                },
                // Add items that implement PoweredItem here
                ModItems.SPONGE_ON_STICK.get()
        );
    }
}
