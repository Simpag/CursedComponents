package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.attachments.ModBlockAttachments;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DimensionalQuarryEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int TICKS_PER_BLOCK = 10;
    public static final int ENERGY_CAPACITY = 10000;
    public static final int ENERGY_RECEIVE = 100_000;
    public static final int ENERGY_CONSUMPTION_PER_TICK = 50;
    public static final int INVENTORY_SIZE = 9; // TODO CHANGE THIS REFERENCE TO ONE BELOW
    public static final int UPGRADES_SIZE = 3;
    public static final int ITEM_STACK_HANDLER_SIZE = INVENTORY_SIZE + UPGRADES_SIZE;
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
        return getEnergyStorage(true);
    }

    public EnergyStorage getEnergyStorage(boolean changed) {
        if (changed)
            setChanged();
        return this.getData(ModBlockAttachments.DIMENSIONAL_QUARRY_ENERGY);
    }

    public ItemStackHandler getInventory() {
        return getInventory(true);
    }

    public ItemStackHandler getInventory(boolean changed) {
        setChanged();
        return this.getData(ModBlockAttachments.DIMENSIONAL_QUARRY_INV);
    }

    public ContainerData getQuarryData() {
        return this.quarryData;
    }

    public NonNullList<ItemStack> getItemStacks() {
        ItemStackHandler inv = getInventory(true);
        int size = inv.getSlots();

        NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            stacks.set(i, inv.getStackInSlot(i));
        }

        return stacks;
    }

    public int getInventorySlotsUsed() {
        int cnt = 0;
        ItemStackHandler inv = this.getInventory(false);
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                cnt++;
            }
        }
        return cnt;
    }

    public String getInventoryString() {
        StringBuilder ret = new StringBuilder();
        ItemStackHandler inv = this.getInventory(false);
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

    public static List<MiningRequirement> checkMiningRequirements(IItemHandler inventory, int energyStored) {
        List<MiningRequirement> ret = new ArrayList<>();

        if (energyStored < ENERGY_CONSUMPTION_PER_TICK)
            ret.add(MiningRequirement.notEnoughEnergy);

        boolean hasPickaxe = inventory.getStackInSlot(0).is(Items.NETHERITE_PICKAXE);
        boolean hasDimension = false; // TODO CHECK FOR DIMENSIONAL CARD

        if (!hasPickaxe)
            ret.add(MiningRequirement.noPickaxe);

        if (ret.size() == 0)
            ret.add(MiningRequirement.ok);

        return ret;
    }

    public void decrementCurrentYLevel() {
        this.currentYLevel--;
        setChanged();
    }

    public void setRunning(boolean state) {
        this.running = state;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        if (!level.isClientSide && checkMiningRequirements(entity.getInventory(false), entity.getEnergyStorage(false).getEnergyStored()).get(0) == MiningRequirement.ok && entity.running) {
            doMining(level, pos, state, entity);
        }
    }

    public static void doMining(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        EnergyStorage energyStorage = entity.getEnergyStorage();

        //if (energyStorage.getEnergyStored() < ENERGY_CONSUMPTION_PER_TICK) {
        //    return;
        //}

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

                itemStack = ItemHandlerHelper.insertItemStacked(entity.getInventory(false), itemStack, false);


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

    public enum MiningRequirement {
        ok(Component.translatable("status.cursedcomponents.dimensional_quarry.ok")),
        notEnoughEnergy(Component.translatable("status.cursedcomponents.dimensional_quarry.not_enough_energy")),
        noPickaxe(Component.translatable("status.cursedcomponents.dimensional_quarry.no_pickaxe")),
        noDimension(Component.translatable("status.cursedcomponents.dimensional_quarry.no_dimension"));

        public final Component status;

        MiningRequirement(Component status) {
            this.status = status;
        }

        public static List<Component> getComponentList(List<MiningRequirement> reqs) {
            return reqs.stream().map((req) -> req.status).toList();
        }
    }
}