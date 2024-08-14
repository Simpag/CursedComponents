package com.ccteam.cursedcomponents.gui.containers.custom;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.gui.containers.ModContainers;
import com.ccteam.cursedcomponents.network.toServer.GUIButtonPayload;
import com.ccteam.cursedcomponents.ItemStackHandlers.DimensionalQuarryItemStackHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.List;

public class DimensionalQuarryContainer extends AbstractContainerMenu {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ContainerLevelAccess access;
    private final IItemHandler inventory;
    private final ContainerData quarryData;

    // Client menu constructor
    public DimensionalQuarryContainer(int containerId, Inventory playerInventory) { // optional FriendlyByteBuf parameter if reading data from server
        this(containerId, playerInventory, ContainerLevelAccess.NULL, new DimensionalQuarryItemStackHandler(DimensionalQuarryEntity.INVENTORY_SIZE), new SimpleContainerData(DimensionalQuarryEntity.QUARRY_DATA_SIZE));
    }

    // Server menu constructor
    public DimensionalQuarryContainer(int containerId, Inventory playerInventory, ContainerLevelAccess access, IItemHandler inventory, ContainerData quarryData) {
        super(ModContainers.DIMENSIONAL_QUARRY_CONTAINER.get(), containerId);
        this.access = access;
        this.inventory = inventory;
        this.quarryData = quarryData;

        checkContainerDataCount(this.quarryData, DimensionalQuarryEntity.QUARRY_DATA_SIZE);

        if (inventory.getSlots() < DimensionalQuarryEntity.INVENTORY_SIZE) {
            throw new IllegalArgumentException("Container size " + inventory.getSlots() + " is smaller than expected " + DimensionalQuarryEntity.INVENTORY_SIZE);
        }

        // Add quarry upgrade slots
        for (int i = 0; i < 3; i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 66, 19 + i * 20));
        }

        // Add quarry inventory item buffer
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(inventory, 3 + col + 3 * row, 8 + 18 * col, 21 + row * 18));
            }
        }

        // Add player inventory
        for (int playerInventoryRow = 0; playerInventoryRow < 3; ++playerInventoryRow) {
            for (int playerInventoryCol = 0; playerInventoryCol < 9; playerInventoryCol++) {
                this.addSlot(new Slot(playerInventory, playerInventoryCol + playerInventoryRow * 9 + 9, 8 + 18 * playerInventoryCol, 99 + playerInventoryRow * 18));
            }
        }

        // Add player hot bar
        for (int hotHarSlot = 0; hotHarSlot < 9; hotHarSlot++) {
            this.addSlot(new Slot(playerInventory, hotHarSlot, 8 + 18 * hotHarSlot, 157));
        }

        this.addDataSlots(quarryData);
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
            int total_quarry_slots = this.inventory.getSlots();
            if (index < total_quarry_slots) { // From block to player
                if (!this.moveItemStackTo(rawStack, total_quarry_slots, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < this.slots.size()) { // From player to block
                if (!this.moveItemStackTo(rawStack, 0, total_quarry_slots, false)) {
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
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.DIMENSIONAL_QUARRY.get());
    }

    public void setRunning(boolean state) {
        BlockPos pos = new BlockPos(getPosX(), getPosY(), getPosZ());
        PacketDistributor.sendToServer(new GUIButtonPayload(GUIButtonPayload.ButtonType.dimensionalQuarryRunningState, pos, state));
    }

    public List<DimensionalQuarryEntity.MiningRequirement> getMiningRequirements() {
        return DimensionalQuarryEntity.checkMiningRequirements(this.getEnergyStored(), this.inventory, this.getInventoryFull() == 1);
    }

    public int getEnergyStored() {
        return this.quarryData.get(0);
    }

    public int getCurrentYLevel() {
        return this.quarryData.get(1);
    }

    public int getCooldown() {
        return this.quarryData.get(2);
    }

    public int getRunning() {
        return this.quarryData.get(3);
    }

    public int getPosX() {
        return this.quarryData.get(4);
    }

    public int getPosY() {
        return this.quarryData.get(5);
    }

    public int getPosZ() {
        return this.quarryData.get(6);
    }

    public int getInventoryFull() {
        return this.quarryData.get(7);
    }
}
