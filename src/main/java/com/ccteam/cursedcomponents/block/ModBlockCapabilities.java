package com.ccteam.cursedcomponents.block;

import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModBlockCapabilities {
    public static void registerBlockCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK, // capability to register for
                ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), // block entity type to register for
                (entity, side) -> entity.getInventory()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(),
                (entity, side) -> entity.getEnergy()
        );
    }
}
