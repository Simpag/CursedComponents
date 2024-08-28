package com.ccteam.cursedcomponents.block.stack_handler;

import com.ccteam.cursedcomponents.item.ModItems;
import com.ccteam.cursedcomponents.util.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DimensionalQuarryItemStackHandler extends ItemStackHandler {
    public DimensionalQuarryItemStackHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return switch (slot) {
            case 0 -> // Pickaxe slot
                    stack.is(Items.NETHERITE_PICKAXE);
            case 1 -> // Dimension slot
                    stack.is(ModTags.Items.MINI_CHUNK);
            case 2 -> // Blacklist slot
                    stack.is(ModItems.DIMENSIONAL_QUARRY_ITEM_FILTER.get());
            default -> true;
        };
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
