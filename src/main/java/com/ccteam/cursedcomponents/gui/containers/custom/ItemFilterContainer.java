package com.ccteam.cursedcomponents.gui.containers.custom;

import com.ccteam.cursedcomponents.datacomponents.ModDataComponents;
import com.ccteam.cursedcomponents.datacomponents.custom.ItemFilterData;
import com.ccteam.cursedcomponents.gui.containers.ModContainers;
import com.ccteam.cursedcomponents.gui.slots.FilterSlot;
import com.ccteam.cursedcomponents.item.custom.ItemFilter;
import com.ccteam.cursedcomponents.stackHandlers.ItemFilterItemStackHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemFilterContainer extends AbstractContainerMenu {
    private final ItemStack myStack;
    private final ItemFilterItemStackHandler myItemHandler;

    // Client menu constructor
    public ItemFilterContainer(int containerId, Inventory playerInventory) { // optional FriendlyByteBuf parameter if reading data from server
        this(containerId, playerInventory, ItemStack.EMPTY);
    }

    // Server menu constructor
    public ItemFilterContainer(int containerId, Inventory playerInventory, ItemStack fromStack) {
        super(ModContainers.ITEM_FILTER_CONTAINER.get(), containerId);
        this.myStack = fromStack;
        this.myItemHandler = myStack.getOrDefault(ModDataComponents.ITEM_FILTER_DATA, new ItemFilterData(null)).getInventory(playerInventory.player.registryAccess());

        if (this.myItemHandler.getSlots() < ItemFilter.FILTER_SIZE) {
            throw new IllegalArgumentException("Container size " + this.myItemHandler.getSlots() + " is smaller than expected " + ItemFilter.FILTER_SIZE);
        }

        // Add quarry inventory item buffer
        for (int col = 0; col < ItemFilter.FILTER_SIZE; col++) {
            this.addSlot(new FilterSlot(this.myItemHandler, col, 43 + 18 * col, 21));
        }

        // Add player inventory
        for (int playerInventoryRow = 0; playerInventoryRow < 3; ++playerInventoryRow) {
            for (int playerInventoryCol = 0; playerInventoryCol < 9; playerInventoryCol++) {
                this.addSlot(new Slot(playerInventory, playerInventoryCol + playerInventoryRow * 9 + 9, 8 + 18 * playerInventoryCol, 52 + playerInventoryRow * 18));
            }
        }

        // Add player hot bar
        for (int hotHarSlot = 0; hotHarSlot < 9; hotHarSlot++) {
            this.addSlot(new Slot(playerInventory, hotHarSlot, 8 + 18 * hotHarSlot, 110));
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

            int total_inventory_slots = ItemFilter.FILTER_SIZE;
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
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.myStack.set(ModDataComponents.ITEM_FILTER_DATA, new ItemFilterData(this.myItemHandler.serializeNBT(player.registryAccess())));
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot.index >= ItemFilter.FILTER_SIZE;
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.index >= ItemFilter.FILTER_SIZE;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
