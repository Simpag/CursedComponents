package com.ccteam.cursedcomponents.energy_storage;

import com.ccteam.cursedcomponents.data_component.ModDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

public class ItemStackEnergyStorage extends EnergyStorage {
    protected final ItemStack itemStack;

    public ItemStackEnergyStorage(int capacity, int usage, ItemStack itemStack) {
        super(capacity, usage, usage, 0);
        this.itemStack = itemStack;
        this.energy = this.getEnergyStored();
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) {
            return 0;
        }

        int energyReceived = Mth.clamp(this.capacity - this.energy, 0, Math.min(this.maxReceive, toReceive));
        if (!simulate) {
            this.energy += energyReceived;
            onEnergyChaged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!canExtract() || toExtract <= 0) {
            return 0;
        }

        int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, toExtract));
        if (!simulate) {
            this.energy -= energyExtracted;
            onEnergyChaged();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return this.itemStack.getOrDefault(ModDataComponents.ITEM_ENERGY, 0);
    }

    protected void onEnergyChaged() {
        this.itemStack.set(ModDataComponents.ITEM_ENERGY, this.energy);
    }
}