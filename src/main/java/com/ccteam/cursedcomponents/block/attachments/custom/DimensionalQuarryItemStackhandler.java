package com.ccteam.cursedcomponents.block.attachments.custom;

import com.ccteam.cursedcomponents.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.Consumer;

public class DimensionalQuarryItemStackhandler extends ItemStackHandler {
    public Consumer<Item> onChangedCallback;
    private Item lastDimensionInSlot = null;

    public DimensionalQuarryItemStackhandler(int itemStackHandlerSize) {
        super(itemStackHandlerSize);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (onChangedCallback != null && slot == 1) {
            Item newItem = getStackInSlot(1).getItem();

            if (newItem == lastDimensionInSlot)
                return;

            lastDimensionInSlot = newItem;
            onChangedCallback.accept(newItem);
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        switch (slot) {
            case 0: // Pickaxe slot
                return stack.is(Items.NETHERITE_PICKAXE);
            case 1: // Dimension slot
                return stack.is(ModBlocks.MINI_CHUNK_OVERWORLD.get().asItem());
            case 2: // Blacklist slot
                return false;
            default:
                return true;
        }
    }
}
