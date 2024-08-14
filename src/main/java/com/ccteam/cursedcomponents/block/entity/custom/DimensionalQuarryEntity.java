package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.util.DimensionalQuarryItemStackHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DimensionalQuarryEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int TICKS_PER_BLOCK = 10;
    public static final int ENERGY_CAPACITY = 10000;
    public static final int ENERGY_CONSUMPTION_PER_TICK = 50;
    public static final int INVENTORY_SIZE = 9 + 3; // 9 storage slots + 3 upgrade
    public static final int QUARRY_DATA_SIZE = 8;

    private int currentYLevel;
    private int cooldown;
    private boolean running;
    private boolean inventoryFull;
    private float miniChunkRotation;
    private final float miniChunkRotationSpeed = 0.5f;

    private final DimensionalQuarryItemStackHandler inventory = new DimensionalQuarryItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            // When anything in the inventory changes
            if (slot < 3 && !level.isClientSide)
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);

            if (slot >= 3 && this.getStackInSlot(slot).isEmpty())
                inventoryFull = false;

            setChanged();
        }
    };
    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);

    public DimensionalQuarryEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), pos, blockState);
        this.running = false;
        this.inventoryFull = false;
        this.currentYLevel = pos.getY() - 1;
        this.cooldown = 0;
    }

    public ItemStack getPickaxeSlot() {
        return this.inventory.getStackInSlot(0);
    }

    public ItemStack getMiniChunkInSlot() {
        return this.inventory.getStackInSlot(1);
    }

    public boolean getRunning() {
        return this.running;
    }

    public ContainerData getQuarryData() {
        return this.quarryData;
    }

    public IItemHandler getInventory() {
        return this.inventory;
    }

    public NonNullList<ItemStack> getAllStacks() {
        NonNullList<ItemStack> stacks = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < getInventory().getSlots(); i++) {
            stacks.set(i, getInventory().getStackInSlot(i));
        }

        return stacks;
    }

    public int getInventorySlotsUsed() {
        int cnt = 0;
        for (int i = 0; i < getInventory().getSlots(); i++) {
            if (!getInventory().getStackInSlot(i).isEmpty()) {
                cnt++;
            }
        }
        return cnt;
    }

    public String getInventoryString() {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < getInventory().getSlots(); i++) {
            if (!getInventory().getStackInSlot(i).isEmpty()) {
                ret.append(getInventory().getStackInSlot(i).toString()).append(", ");
            }
        }

        return ret.toString();
    }

    public int getCurrentYLevel() {
        return this.currentYLevel;
    }

    public float getMiniChunkRotation() {
        // maybe change this to use server time
        miniChunkRotation = (miniChunkRotation + miniChunkRotationSpeed) % 360;
        return miniChunkRotation;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    public static List<MiningRequirement> checkMiningRequirements(int energyStore, IItemHandler inv, boolean inventoryFull) {
        List<MiningRequirement> ret = new ArrayList<>();

        if (energyStore < ENERGY_CONSUMPTION_PER_TICK)
            ret.add(MiningRequirement.notEnoughEnergy);

        boolean hasPickaxe = inv.getStackInSlot(0).is(Items.NETHERITE_PICKAXE);
        boolean hasDimension = inv.getStackInSlot(1).is(ModBlocks.MINI_CHUNK_OVERWORLD.asItem()); // TODO, maybe use tags

        if (!hasPickaxe)
            ret.add(MiningRequirement.noPickaxe);

        if (!hasDimension)
            ret.add(MiningRequirement.noDimension);

        if (inventoryFull)
            ret.add(MiningRequirement.inventoryFull);

        if (ret.isEmpty())
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
        if (!level.isClientSide &&
                checkMiningRequirements(entity.getEnergyStored(), entity.inventory, entity.inventoryFull).getFirst() == MiningRequirement.ok &&
                entity.running &&
                !entity.inventoryFull) {
            entity.doMining(level, pos);
        }
    }

    public void doMining(Level level, BlockPos pos) {
        // Draw energy every tick
        energy.extractEnergy(ENERGY_CONSUMPTION_PER_TICK, false);

        this.cooldown++;
        if (this.cooldown <= TICKS_PER_BLOCK) {
            return;
        }
        this.cooldown = 0;

        if (this.getCurrentYLevel() > level.getMinBuildHeight()) {
            BlockPos blockToMinePos = new BlockPos(pos.getX(), this.getCurrentYLevel(), pos.getZ());
            BlockState blockToMineState = level.getBlockState(blockToMinePos);
            Block block = blockToMineState.getBlock();

            if (block != Blocks.AIR && blockToMineState.getDestroySpeed(level, blockToMinePos) >= 0) {
                // Try to add the mined block to the inventory
                ItemStack itemStack = new ItemStack(block);

                itemStack = ItemHandlerHelper.insertItemStacked(this.inventory, itemStack, false);

                // If the inventory is full, stop the mining process
                if (!itemStack.isEmpty()) {
                    inventoryFull = true;
                    return;
                }

                // Set the block to air (break the block)
                level.setBlock(blockToMinePos, Blocks.AIR.defaultBlockState(), 3);
            }

            this.decrementCurrentYLevel();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cursedcomponents:dimensional_quarry_current_y_level", this.currentYLevel);
        tag.putBoolean("cursedcomponents:dimensional_quarry_running", this.running);
        tag.putBoolean("cursedcomponents:dimensional_quarry_inventory_full", inventoryFull);
        CompoundTag upgradesTag = inventory.serializeNBT(registries);
        tag.put("cursedcomponents:dimensional_quarry_inventory", upgradesTag);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.running = tag.getBoolean("cursedcomponents:dimensional_quarry_running");
        this.inventoryFull = tag.getBoolean("cursedcomponents:dimensional_quarry_inventory_full");
        this.inventory.deserializeNBT(registries, (CompoundTag) tag.get("cursedcomponents:dimensional_quarry_inventory"));
        if (tag.contains("cursedcomponents:dimensional_quarry_current_y_level")) {
            this.currentYLevel = tag.getInt("cursedcomponents:dimensional_quarry_current_y_level");
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    private final ContainerData quarryData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy.getEnergyStored();
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
                case 7:
                    return inventoryFull ? 1 : 0;
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
        noDimension(Component.translatable("status.cursedcomponents.dimensional_quarry.no_dimension")),
        inventoryFull(Component.translatable(("status.cursedcomponents.dimensional_quarry.inventory_full")));

        public final Component status;

        MiningRequirement(Component status) {
            this.status = status;
        }

        public static List<Component> getComponentList(List<MiningRequirement> reqs) {
            return reqs.stream().map((req) -> req.status).toList();
        }
    }
}