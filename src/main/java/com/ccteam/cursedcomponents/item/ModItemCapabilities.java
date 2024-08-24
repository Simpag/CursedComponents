package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.item.base.InventoryItem;
import com.ccteam.cursedcomponents.item.base.ItemStackEnergyStorage;
import com.ccteam.cursedcomponents.item.base.ItemStackInventory;
import com.ccteam.cursedcomponents.item.base.PoweredItem;
import com.ccteam.cursedcomponents.item.data_component.ModDataComponents;
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

        event.registerItem(
                Capabilities.ItemHandler.ITEM,
                (itemStack, context) -> {
                    int size = 0;
                    int slotLimit = 0;

                    if (itemStack.getItem() instanceof InventoryItem ii) {
                        size = ii.getSize();
                        slotLimit = ii.getSlotSizeLimit();
                    }
                    return new ItemStackInventory(itemStack, ModDataComponents.ITEM_INVENTORY.get(), size, slotLimit);
                },
                ModItems.DIMENSIONAL_QUARRY_ITEM_FILTER.get()
        );
    }
}
