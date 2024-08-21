package com.ccteam.cursedcomponents.stackHandlers;

import com.ccteam.cursedcomponents.block.ModBlocks;

import com.ccteam.cursedcomponents.item.ModItems;
import com.ccteam.cursedcomponents.util.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DimensionalQuarryItemStackHandler extends ItemStackHandler {
    public DimensionalQuarryItemStackHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        switch (slot) {
            case 0: // Pickaxe slot
                return stack.is(Items.NETHERITE_PICKAXE);
            case 1: // Dimension slot
                return stack.is(ModTags.Items.MINI_CHUNK);
            case 2: // Blacklist slot
                return stack.is(ModItems.ITEM_FILTER.get());
            default:
                return true;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < 3)
            return 1;

        return super.getSlotLimit(slot);
    }

    public NonNullList<ItemStack> getStacks() {
        return NonNullList.copyOf(this.stacks);
    }
}
