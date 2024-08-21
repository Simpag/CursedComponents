package com.ccteam.cursedcomponents.gui.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FilterSlot extends SlotItemHandler {
    public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if (!stack.isEmpty() && this.mayPlace(stack)) {
            ItemStack itemstack = this.getItem();
            int i = Math.min(Math.min(increment, stack.getCount()), this.getMaxStackSize(stack) - itemstack.getCount());
            if (itemstack.isEmpty()) {
                this.setByPlayer(stack.copyWithCount(i));
            } else if (ItemStack.isSameItemSameComponents(itemstack, stack)) {
                itemstack.grow(i);
                this.setByPlayer(itemstack);
            }

            return stack;
        } else {
            return stack;
        }
    }

    @Override
    public ItemStack remove(int amount) {
        this.getItemHandler().extractItem(index, amount, false);
        return ItemStack.EMPTY;
    }
}
