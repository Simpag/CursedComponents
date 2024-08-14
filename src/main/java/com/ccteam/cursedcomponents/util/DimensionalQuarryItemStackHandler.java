package com.ccteam.cursedcomponents.util;

import com.ccteam.cursedcomponents.block.ModBlocks;
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
                return stack.is(ModBlocks.MINI_CHUNK_OVERWORLD.get().asItem());
            case 2: // Blacklist slot
                return false;
            default:
                return true;
        }
    }
}
