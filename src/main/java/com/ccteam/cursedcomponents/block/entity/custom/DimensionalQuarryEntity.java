package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.attachments.ModBlockAttachments;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class DimensionalQuarryEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int TICKS_PER_BLOCK = 10;
    public static final int ENERGY_CAPACITY = 10000;
    public static final int ENERGY_RECEIVE = 100_000;
    public static final int ENERGY_CONSUMPTION_PER_TICK = 50;
    public static final int INVENTORY_SIZE = 9;
    public static final int QUARRY_DATA_SIZE = 7;

    private int currentYLevel;
    private int cooldown;
    private boolean running;

    public DimensionalQuarryEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), pos, blockState);
        this.running = false;
        this.currentYLevel = pos.getY() - 1;
        this.cooldown = 0;
    }

    public EnergyStorage getEnergyStorage() {
        //return this.energyStorage;
        return getEnergyStorage(true);
    }

    public EnergyStorage getEnergyStorage(boolean changed) {
        //return this.energyStorage;
        if (changed)
            setChanged();
        return this.getData(ModBlockAttachments.DIMENSIONAL_QUARRY_ENERGY);
    }

    public ItemStackHandler getInventory() {
        //return this.inventory;
        setChanged();
        return this.getData(ModBlockAttachments.DIMENSIONAL_QUARRY_INV);
    }

    public ContainerData getQuarryData() {
        return this.quarryData;
    }

    public NonNullList<ItemStack> getItemStacks() {
        ItemStackHandler inv = getInventory();
        int size = inv.getSlots();

        NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            stacks.set(i, inv.getStackInSlot(i));
        }

        return stacks;
    }

    public int getInventorySlotsUsed() {
        int cnt = 0;
        ItemStackHandler inv = this.getInventory();
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                cnt++;
            }
        }
        return cnt;
    }

    public String getInventoryString() {
        StringBuilder ret = new StringBuilder();
        ItemStackHandler inv = this.getInventory();
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                ret.append(inv.getStackInSlot(i).toString()).append(", ");
            }
        }

        return ret.toString();
    }

    public int getCurrentYLevel() {
        return this.currentYLevel;
    }

    public void decrementCurrentYLevel() {
        this.currentYLevel--;
        setChanged();
    }

    public void toggleRunning() {
        this.running = !this.running;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        if (!level.isClientSide && entity.running) {
            doMining(level, pos, state, entity);
        }
    }

    public static void doMining(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        EnergyStorage energyStorage = entity.getEnergyStorage();

        if (energyStorage.getEnergyStored() < ENERGY_CONSUMPTION_PER_TICK) {
            LOGGER.debug("Not enough energy to mine!");
            return;
        }

        // Draw energy every tick
        energyStorage.extractEnergy(ENERGY_CONSUMPTION_PER_TICK, false);

        entity.cooldown++;
        if (entity.cooldown <= TICKS_PER_BLOCK) {
            return;
        }
        entity.cooldown = 0;

        if (entity.getCurrentYLevel() > level.getMinBuildHeight()) {
            BlockPos blockToMinePos = new BlockPos(pos.getX(), entity.getCurrentYLevel(), pos.getZ());
            BlockState blockToMineState = level.getBlockState(blockToMinePos);
            Block block = blockToMineState.getBlock();

            if (block != Blocks.AIR && blockToMineState.getDestroySpeed(level, blockToMinePos) >= 0) {
                // Try to add the mined block to the inventory
                ItemStack itemStack = new ItemStack(block);

                itemStack = ItemHandlerHelper.insertItemStacked(entity.getInventory(), itemStack, false);


                // If the inventory is full, stop the mining process
                if (!itemStack.isEmpty()) {
                    return;
                }

                // Set the block to air (break the block)
                level.setBlock(blockToMinePos, Blocks.AIR.defaultBlockState(), 3);
            }

            entity.decrementCurrentYLevel();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cursedcomponents:dimensional_quarry_current_y_level", this.currentYLevel);
        tag.putBoolean("cursedcomponents:dimensional_quarry_running", this.running);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.running = tag.getBoolean("cursedcomponents:dimensional_quarry_running");

        if (tag.contains("cursedcomponents:dimensional_quarry_current_y_level")) {
            this.currentYLevel = tag.getInt("cursedcomponents:dimensional_quarry_current_y_level");
        }
    }

    private final ContainerData quarryData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return getEnergyStorage(false).getEnergyStored();
                case 1:
                    return currentYLevel;
                case 2:
                    return cooldown;
                case 3:
                    return running ? 1 : 0;
                case 4:
                    return worldPosition.getX();
                case 5:
                    return worldPosition.getY();
                case 6:
                    return worldPosition.getZ();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            return;
        }


        @Override
        public int getCount() {
            return QUARRY_DATA_SIZE;
        }
    };
}