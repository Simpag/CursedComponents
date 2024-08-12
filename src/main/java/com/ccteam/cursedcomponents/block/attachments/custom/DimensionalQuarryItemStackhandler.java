package com.ccteam.cursedcomponents.block.attachments.custom;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DimensionalQuarryItemStackhandler extends ItemStackHandler {
    public DimensionalQuarryItemStackhandler(int itemStackHandlerSize) {
        super(itemStackHandlerSize);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        switch (slot) {
            case 0: // Pickaxe slot
                return stack.is(Items.NETHERITE_PICKAXE);
            case 1: // Dimension slot
                return false;
            case 2: // Blacklist slot
                return false;
            default:
                return true;
        }
    }
}
