package com.ccteam.cursedcomponents.item.base;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class ItemStackInventory extends ComponentItemHandler {
    protected final int slotLimit;

    public ItemStackInventory(MutableDataComponentHolder parent, DataComponentType<ItemContainerContents> component, int size, int slotLimit) {
        super(parent, component, size);
        this.slotLimit = slotLimit;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotLimit;
    }
}
