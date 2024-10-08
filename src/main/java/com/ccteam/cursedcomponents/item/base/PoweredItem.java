package com.ccteam.cursedcomponents.item.base;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public interface PoweredItem {
    int getCapacity();

    int getEnergyUsage();

    IEnergyStorage getEnergyStorage(ItemStack stack);

    int getEnergyStored(ItemStack stack);

    int getPowerBarWidth(ItemStack stack);

    Integer getPowerBarColor(ItemStack stack);

    int extractEnergy(ItemStack stack, int amount, boolean simulate);

    boolean isPowerBarVisible(ItemStack stack);

    boolean isOperable(ItemStack stack);
}
