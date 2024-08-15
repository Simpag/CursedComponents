package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.ItemStackHandlers.DimensionalQuarryItemStackHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DimensionalQuarryEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int EJECTION_COOLDOWN = 20;
    public static final int TICKS_PER_BLOCK = 10;
    public static final int ENERGY_CAPACITY = 10000;
    public static final int ENERGY_CONSUMPTION_PER_TICK = 50;
    public static final int UPGRADE_SLOTS = 3;
    public static final int INVENTORY_SIZE = 9 + UPGRADE_SLOTS; // 9 storage slots + 3 upgrade
    public static final int QUARRY_DATA_SIZE = 8;

    private int currentYLevel;
    private int miningCooldown;
    private int ejectCooldown;
    private boolean running;
    private float miniChunkRotation;
    private static final float miniChunkRotationSpeed = 0.5f;

    private final DimensionalQuarryItemStackHandler inventory = new DimensionalQuarryItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            // Update slots to client when upgrades change
            if (slot < 3 && !level.isClientSide)
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);

            setChanged();
        }
    };
    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);

    private HashMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> itemHandlerCapCaches = new HashMap<>();

    public DimensionalQuarryEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), pos, blockState);
        this.running = false;
        this.currentYLevel = pos.getY() - 1;
        this.miningCooldown = 0;
        this.ejectCooldown = 0;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null && !level.isClientSide)
            createItemHandlerCapCaches((ServerLevel) level);
    }

    public ItemStack getPickaxeSlot() {
        return this.inventory.getStackInSlot(0);
    }

    public ItemStack getMiniChunkInSlot() {
        return this.inventory.getStackInSlot(1);
    }

    public ContainerData getQuarryData() {
        return this.quarryData;
    }

    public DimensionalQuarryItemStackHandler getInventory() {
        return this.inventory;
    }

    public int getInventorySlots() {
        return this.inventory.getSlots();
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
    }

    public void setRunning(boolean state) {
        this.running = state;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        if (level.isClientSide)
            return;

        if (entity.running && checkMiningRequirements(entity.getEnergyStored(), entity.inventory, isStorageFull(entity.getInventory(), 3, entity.getInventorySlots())).getFirst() == MiningRequirement.ok) {
            entity.doMining(level, pos);
        }

        entity.tryEject(level, pos);
    }

    public void doMining(Level level, BlockPos pos) {
        // Draw energy every tick
        energy.extractEnergy(ENERGY_CONSUMPTION_PER_TICK, false);

        this.miningCooldown++;
        if (this.miningCooldown <= TICKS_PER_BLOCK) {
            return;
        }
        this.miningCooldown = 0;

        if (this.getCurrentYLevel() > level.getMinBuildHeight()) {
            BlockPos blockToMinePos = new BlockPos(pos.getX(), this.getCurrentYLevel(), pos.getZ());
            BlockState blockToMineState = level.getBlockState(blockToMinePos);
            Block block = blockToMineState.getBlock();

            if (block != Blocks.AIR && blockToMineState.getDestroySpeed(level, blockToMinePos) >= 0) {
                // Try to add the mined block to the inventory
                LootParams.Builder params = new LootParams.Builder((ServerLevel) level);
                params = params.withOptionalParameter(LootContextParams.ORIGIN, blockToMinePos.getCenter());
                params = params.withOptionalParameter(LootContextParams.TOOL, getPickaxeSlot());

                List<ItemStack> itemStacks = blockToMineState.getDrops(params);

                // Check if we can insert all drops
                for (ItemStack itemStack : itemStacks) {
                    ItemStack simItemStack = ItemHandlerHelper.insertItemStacked(this.inventory, itemStack, true);

                    // If the inventory is full, stop the mining process
                    if (!simItemStack.isEmpty()) {
                        return;
                    }
                }

                // Insert the items
                for (ItemStack itemStack : itemStacks) {
                    ItemHandlerHelper.insertItemStacked(this.inventory, itemStack, false);
                }

                // Set the block to air (break the block)
                level.setBlock(blockToMinePos, Blocks.AIR.defaultBlockState(), 3);
            }

            this.decrementCurrentYLevel();
        }
    }

    public void tryEject(Level level, BlockPos pos) {
        this.ejectCooldown++;
        if (this.ejectCooldown <= EJECTION_COOLDOWN) {
            return;

        }
        this.ejectCooldown = 0;

        // Check for attached containers
        List<IItemHandler> attachedStorages = getAttachedStorages(level, pos);
        if (attachedStorages.isEmpty()) {
            return;
        }

        for (IItemHandler storage : attachedStorages) {
            if (isStorageFull(storage)) {
                // Go to next container if full
                continue;
            }

            for (int i = UPGRADE_SLOTS; i < this.getInventory().getSlots(); i++) {
                ItemStack itemStack = this.getInventory().getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    ItemStack itemStack1 = ItemHandlerHelper.insertItemStacked(storage, itemStack, false); //addItem(blockEntity, container, blockEntity.removeItem(i, 1), direction);

                    if (itemStack1.isEmpty()) {
                        inventory.setStackInSlot(i, ItemStack.EMPTY);
                        return;
                    }

                    // If we could not eject all items, set the new count and return
                    if (itemStack.getCount() != itemStack1.getCount()) {
                        this.getInventory().setStackInSlot(i, itemStack1);
                        return;
                    }
                }
            }
        }
    }

    private List<IItemHandler> getAttachedStorages(Level level, BlockPos center) {
        List<IItemHandler> attachedStorages = new ArrayList<>();

        this.itemHandlerCapCaches.forEach((ejectDir, cache) -> {
            IItemHandler handler = cache.getCapability();
            if (handler != null)
                attachedStorages.add(handler);
        });

        return attachedStorages;
    }

    public static boolean isStorageFull(IItemHandler storage, int from, int to) {
        if (from < 0 || to > storage.getSlots())
            new IllegalArgumentException("From must be less than 0 and to must be less than " + storage.getSlots() + ", got (from: " + from + ", to: " + to + ")");

        for (int i = from; i < to; i++) {
            ItemStack stack = storage.getStackInSlot(i);
            if (stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isStorageFull(IItemHandler storage) {
        return isStorageFull(storage, 0, storage.getSlots());
    }

    private void createItemHandlerCapCaches(ServerLevel serverLevel) {
        Direction.Plane.VERTICAL.forEach((ejectDir) -> {
            itemHandlerCapCaches.put(
                    ejectDir,
                    BlockCapabilityCache.create(
                            Capabilities.ItemHandler.BLOCK, // capability to cache
                            serverLevel, // level
                            worldPosition.relative(ejectDir), // target position
                            ejectDir.getOpposite(), // context
                            () -> !this.isRemoved(), // validity check (because the cache might outlive the object it belongs to)
                            () -> onItemHandlerCapInvalidate(ejectDir, serverLevel) // invalidation listener
                    )
            );
        });

        Direction.Plane.HORIZONTAL.forEach((ejectDir) -> {
            itemHandlerCapCaches.put(
                    ejectDir,
                    BlockCapabilityCache.create(
                            Capabilities.ItemHandler.BLOCK, // capability to cache
                            serverLevel, // level
                            worldPosition.relative(ejectDir), // target position
                            ejectDir.getOpposite(), // context
                            () -> !this.isRemoved(), // validity check (because the cache might outlive the object it belongs to)
                            () -> onItemHandlerCapInvalidate(ejectDir, serverLevel) // invalidation listener
                    )
            );
        });
    }

    private void onItemHandlerCapInvalidate(Direction ejectDir, ServerLevel serverLevel) {
        itemHandlerCapCaches.put(
                ejectDir,
                BlockCapabilityCache.create(
                        Capabilities.ItemHandler.BLOCK, // capability to cache
                        serverLevel, // level
                        worldPosition.relative(ejectDir), // target position
                        ejectDir.getOpposite(), // context
                        () -> !this.isRemoved(), // validity check (because the cache might outlive the object it belongs to)
                        () -> onItemHandlerCapInvalidate(ejectDir, serverLevel) // invalidation listener
                )
        );
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cursedcomponents:dimensional_quarry_current_y_level", this.currentYLevel);
        tag.putBoolean("cursedcomponents:dimensional_quarry_running", this.running);
        CompoundTag inventoryTag = inventory.serializeNBT(registries);
        tag.put("cursedcomponents:dimensional_quarry_inventory", inventoryTag);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.running = tag.getBoolean("cursedcomponents:dimensional_quarry_running");
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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
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
                    return miningCooldown;
                case 3:
                    return running ? 1 : 0;
                case 4:
                    return worldPosition.getX();
                case 5:
                    return worldPosition.getY();
                case 6:
                    return worldPosition.getZ();
                case 7:
                    return isStorageFull(inventory, 3, getInventorySlots()) ? 1 : 0;
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