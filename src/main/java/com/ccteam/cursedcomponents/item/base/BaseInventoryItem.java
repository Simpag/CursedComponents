package com.ccteam.cursedcomponents.item.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class BaseInventoryItem extends Item implements InventoryItem {
    protected final int size;
    protected final int slotSizeLimit;

    public BaseInventoryItem(Properties properties, int size, int slotSizeLimit) {
        super(properties);
        this.size = size;
        this.slotSizeLimit = slotSizeLimit;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getSlotSizeLimit() {
        return this.slotSizeLimit;
    }

    @Override
    public IItemHandler getInventory(ItemStack thisStack) {
        return thisStack.getCapability(Capabilities.ItemHandler.ITEM);
    }

    @Override
    public ItemStack getStackInSlot(ItemStack thisStack, int slot) {
        IItemHandler inv = this.getInventory(thisStack);
        if (inv == null)
            return ItemStack.EMPTY;

        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(ItemStack thisStack, int slot, ItemStack toInsertStack, boolean simulate) {
        IItemHandler inv = this.getInventory(thisStack);
        if (inv == null)
            return ItemStack.EMPTY;

        return inv.insertItem(slot, toInsertStack, simulate);
    }

    @Override
    public ItemStack extractItem(ItemStack thisStack, int slot, int amount, boolean simulate) {
        IItemHandler inv = this.getInventory(thisStack);
        if (inv == null)
            return ItemStack.EMPTY;

        return inv.extractItem(slot, amount, simulate);
    }
}
