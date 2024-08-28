package com.ccteam.cursedcomponents.gui.container.custom;

import com.ccteam.cursedcomponents.gui.container.ModContainers;
import com.ccteam.cursedcomponents.gui.slot.FilterSlot;
import com.ccteam.cursedcomponents.item.ModItems;
import com.ccteam.cursedcomponents.item.base.InventoryItem;
import com.ccteam.cursedcomponents.item.custom.DimensionalQuarryItemFilter;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class DimensionalQuarryItemFilterContainer extends AbstractContainerMenu {
    // Client menu constructor
    public DimensionalQuarryItemFilterContainer(int containerId, Inventory playerInventory) { // optional FriendlyByteBuf parameter if reading data from server
        this(containerId, playerInventory, new ItemStack(ModItems.DIMENSIONAL_QUARRY_ITEM_FILTER.get()));
    }

    // Server menu constructor
    public DimensionalQuarryItemFilterContainer(int containerId, Inventory playerInventory, ItemStack fromStack) {
        super(ModContainers.DIMENSIONAL_QUARRY_ITEM_FILTER_CONTAINER.get(), containerId);

        LogUtils.getLogger().debug("Side: " + fromStack);
        if (fromStack.getItem() instanceof InventoryItem ii) {
            IItemHandler myItemHandler = ii.getInventory(fromStack);

            if (myItemHandler.getSlots() < DimensionalQuarryItemFilter.FILTER_SIZE) {
                throw new IllegalArgumentException("Container size " + myItemHandler.getSlots() + " is smaller than expected " + DimensionalQuarryItemFilter.FILTER_SIZE);
            }

            for (int i = 0; i < myItemHandler.getSlots(); i++) {
                LogUtils.getLogger().debug("Has item: " + myItemHandler.getStackInSlot(i));
            }

            // Add quarry inventory item buffer
            for (int col = 0; col < DimensionalQuarryItemFilter.FILTER_SIZE; col++) {
                this.addSlot(new FilterSlot(myItemHandler, col, 44 + 18 * col, 20));
            }
        }

        // Add player inventory
        for (int playerInventoryRow = 0; playerInventoryRow < 3; ++playerInventoryRow) {
            for (int playerInventoryCol = 0; playerInventoryCol < 9; playerInventoryCol++) {
                this.addSlot(new Slot(playerInventory, playerInventoryCol + playerInventoryRow * 9 + 9, 8 + 18 * playerInventoryCol, 51 + playerInventoryRow * 18));
            }
        }

        // Add player hot bar
        for (int hotHarSlot = 0; hotHarSlot < 9; hotHarSlot++) {
            this.addSlot(new Slot(playerInventory, hotHarSlot, 8 + 18 * hotHarSlot, 109));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        // Handle shift-clicking in the inventory
        ItemStack itemStack = ItemStack.EMPTY;
        Slot movedSlot = this.slots.get(index);

        if (movedSlot.hasItem()) {
            ItemStack rawStack = movedSlot.getItem();
            itemStack = rawStack.copy();

            int total_inventory_slots = DimensionalQuarryItemFilter.FILTER_SIZE;
            if (index < total_inventory_slots) { // From block to player
                movedSlot.setByPlayer(ItemStack.EMPTY);
                movedSlot.setChanged();
                return ItemStack.EMPTY;
            } else if (index < this.slots.size()) { // From player to block
                for (int i = 0; i < total_inventory_slots; i++) {
                    Slot slot = this.slots.get(i);
                    if (slot.getItem().isEmpty()) {
                        slot.setByPlayer(rawStack.copyWithCount(1));
                        slot.setChanged();
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }
        }

        return itemStack;
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot.index >= DimensionalQuarryItemFilter.FILTER_SIZE;
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.index >= DimensionalQuarryItemFilter.FILTER_SIZE;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
