package com.ccteam.cursedcomponents.item.interfaces;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public interface PoweredItem {
    int getCapacity();

    int getEnergyUse();

    default IEnergyStorage getEnergyStorage(ItemStack stack) {
        return stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }

    default int getEnergyStored(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return -1;

        return energy.getEnergyStored();
    }

    default int getPowerBarWidth(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return 13;

        return Math.round(13f * (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored());
    }

    default Integer getPowerBarColor(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return null;

        float f = Math.max(0.0f, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored());
        return Mth.hsvToRgb(f / 3.0f, 1.0F, 1.0F);
    }

    default int extractEnergy(ItemStack stack, int amount, boolean simulate) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return 0;

        return energy.extractEnergy(amount, simulate);
    }

    default boolean isPowerBarVisible(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return false;

        return (energy.getEnergyStored() < energy.getMaxEnergyStored());
    }

    default boolean isOperable(ItemStack stack) {
        return getEnergyStored(stack) >= getEnergyUse();
    }
}
