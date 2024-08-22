package com.ccteam.cursedcomponents.data_component.custom;

import com.ccteam.cursedcomponents.item.custom.ItemFilter;
import com.ccteam.cursedcomponents.stack_handler.ItemFilterItemStackHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public record ItemFilterData(CompoundTag inventoryTag) {
    public ItemFilterItemStackHandler getInventory(HolderLookup.Provider provider) {
        ItemFilterItemStackHandler inv = new ItemFilterItemStackHandler(ItemFilter.FILTER_SIZE);

        if (this.inventoryTag != null && !this.inventoryTag.isEmpty())
            inv.deserializeNBT(provider, this.inventoryTag);

        return inv;
    }
}
