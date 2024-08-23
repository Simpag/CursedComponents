package com.ccteam.cursedcomponents.item.base;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BasePoweredItem extends Item implements PoweredItem {
    protected final int energyCapacity;
    protected final int energyUsage;

    public BasePoweredItem(Properties properties, int energyCapacity, int energyUsage) {
        super(properties);
        this.energyCapacity = energyCapacity;
        this.energyUsage = energyUsage;
    }

    @Override
    public IEnergyStorage getEnergyStorage(ItemStack stack) {
        return stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }

    @Override
    public int getCapacity() {
        return this.energyCapacity;
    }

    @Override
    public int getEnergyUse() {
        return this.energyUsage;
    }

    @Override
    public int getEnergyStored(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return -1;

        return energy.getEnergyStored();
    }

    @Override
    public int getPowerBarWidth(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return 13;

        return Math.round(13f * (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored());
    }

    @Override
    public Integer getPowerBarColor(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return null;

        float f = Math.max(0.0f, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored());
        return Mth.hsvToRgb(f / 3.0f, 1.0F, 1.0F);
    }

    @Override
    public int extractEnergy(ItemStack stack, int amount, boolean simulate) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return 0;

        return energy.extractEnergy(amount, simulate);
    }

    @Override
    public boolean isPowerBarVisible(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy == null)
            return false;

        return (energy.getEnergyStored() < energy.getMaxEnergyStored());
    }

    @Override
    public boolean isOperable(ItemStack stack) {
        return getEnergyStored(stack) >= getEnergyUse();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isPowerBarVisible(stack);
    }

    // <Item> overrides
    @Override
    public int getBarWidth(ItemStack stack) {
        return this.getPowerBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        Integer color = this.getPowerBarColor(stack);
        if (color == null)
            return super.getBarColor(stack);
        return color;
    }
}
