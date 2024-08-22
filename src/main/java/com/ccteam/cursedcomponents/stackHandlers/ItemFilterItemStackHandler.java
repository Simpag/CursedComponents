package com.ccteam.cursedcomponents.stackHandlers;

import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemFilterItemStackHandler extends ItemStackHandler {
    public ItemFilterItemStackHandler(int size) {
        super(size);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
