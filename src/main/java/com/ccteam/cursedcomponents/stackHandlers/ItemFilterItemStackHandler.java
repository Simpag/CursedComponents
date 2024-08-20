package com.ccteam.cursedcomponents.stackHandlers;

import com.ccteam.cursedcomponents.block.ModBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemFilterItemStackHandler extends ItemStackHandler {
    public ItemFilterItemStackHandler(int size) {
        super(size);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if (!this.stacks.get(slot).isEmpty()) {
            this.stacks.set(slot, ItemStack.EMPTY);
            onContentsChanged(slot);
        }

        return ItemStack.EMPTY;
    }
}
