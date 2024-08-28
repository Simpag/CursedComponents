package com.ccteam.cursedcomponents.item.base;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public interface InventoryItem {
    int getSize();

    int getSlotSizeLimit();

    IItemHandler getInventory(ItemStack thisStack);

    ItemStack getStackInSlot(ItemStack thisStack, int slot);

    ItemStack insertItem(ItemStack thisStack, int slot, ItemStack toInsertStack, boolean simulate);

    ItemStack extractItem(ItemStack thisStack, int slot, int amount, boolean simulate);
}
