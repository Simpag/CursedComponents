package com.ccteam.cursedcomponents.gui.containers.custom;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.gui.containers.ModContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class DimensionalQuarryContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final IItemHandler inventory;
    private final ContainerData energyData;

    // Client menu constructor
    public DimensionalQuarryContainer(int containerId, Inventory playerInventory) { // optional FriendlyByteBuf parameter if reading data from server
        this(containerId, playerInventory, ContainerLevelAccess.NULL, new ItemStackHandler(DimensionalQuarryEntity.INVENTORY_SIZE), new SimpleContainerData(DimensionalQuarryEntity.ENERGY_DATA_SIZE));
    }

    // Server menu constructor
    public DimensionalQuarryContainer(int containerId, Inventory playerInventory, ContainerLevelAccess access, IItemHandler dataInventory, ContainerData  energyData) { // add energy storage using containerdata
        super(ModContainers.DIMENSIONAL_QUARRY_CONTAINER.get(), containerId);
        this.access = access;
        this.inventory = dataInventory;
        this.energyData = energyData;

        checkContainerDataCount(this.energyData, DimensionalQuarryEntity.ENERGY_DATA_SIZE);

        if (dataInventory.getSlots() < DimensionalQuarryEntity.INVENTORY_SIZE) {
            throw new IllegalArgumentException("Container size " + dataInventory.getSlots() + " is smaller than expected " + DimensionalQuarryEntity.INVENTORY_SIZE);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(dataInventory, col + 3 * row, 8 + 18 * col, 21 + row * 18));
            }
        }

        for (int playerInventoryRow = 0; playerInventoryRow < 3; ++playerInventoryRow) {
            for (int playerInventoryCol = 0; playerInventoryCol < 9; playerInventoryCol++) {
                this.addSlot(new Slot(playerInventory, playerInventoryCol + playerInventoryRow * 9 + 9, 8 + 18 * playerInventoryCol, 99 + playerInventoryRow * 18));
            }
        }

        for (int hotHarSlot = 0; hotHarSlot < 9; hotHarSlot++) {
            this.addSlot(new Slot(playerInventory, hotHarSlot, 8 + 18 * hotHarSlot, 157));
        }

        this.addDataSlots(energyData);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Handle shift-clicking in the inventory
        ItemStack itemStack = ItemStack.EMPTY;
        Slot movedSlot = this.slots.get(index);

        if (movedSlot != null && movedSlot.hasItem()) {
            ItemStack rawStack = movedSlot.getItem();
            itemStack = rawStack.copy();

            /*
            Add logic for upgrades so that items cant be moved into that slot except for specials
            See: https://docs.neoforged.net/docs/gui/menus#:~:text=if%20(quickMovedSlotIndex%20%3D%3D%200)%20%7B
             */

            if (index < this.inventory.getSlots()) { // From block to player
                if (!this.moveItemStackTo(rawStack, this.inventory.getSlots(), this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= this.inventory.getSlots() && index < this.slots.size()) { // From player to block
                if (!this.moveItemStackTo(rawStack, 0, this.inventory.getSlots(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (rawStack.isEmpty()) {
                movedSlot.set(ItemStack.EMPTY);
            } else {
                movedSlot.setChanged();
            }

            if (rawStack.getCount() == itemStack.getCount()) {
                // If the raw stack was not able to be moved to another slot, no longer quick move
                return ItemStack.EMPTY;
            }
            // Execute logic on what to do post move with the remaining stack
            movedSlot.onTake(player, rawStack);
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.DIMENSIONAL_QUARRY.get());
    }

    public int getEnergyStored() {
        return this.energyData.get(0);
    }

    public int getCurrentYLevel() {
        return this.energyData.get(1);
    }

    public int getCooldown() {
        return this.energyData.get(2);
    }

    public int getRunning() {
        return this.energyData.get(3);
    }
}
