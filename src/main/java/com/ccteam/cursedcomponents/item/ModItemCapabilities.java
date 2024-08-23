package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.energy_storage.ItemStackEnergyStorage;
import com.ccteam.cursedcomponents.item.custom.SpongeOnStick;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModItemCapabilities {
    public static void registerItemCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (itemStack, context) -> {
                    int capacity = 0;
                    int usage = 0;
                    if (itemStack.getItem() instanceof SpongeOnStick) {
                        capacity = SpongeOnStick.MAX_ENERGY;
                        usage = SpongeOnStick.ENERGY_USAGE;
                    }
                    return new ItemStackEnergyStorage(capacity, usage, itemStack);
                },
                ModItems.SPONGE_ON_STICK.get()
        );
    }
}
