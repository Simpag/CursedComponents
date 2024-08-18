package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.Config;
import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.custom.MiniChunkBlock;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.itemStackHandlers.DimensionalQuarryItemStackHandler;

import com.ccteam.cursedcomponents.threads.DimensionalQuarrySearcher;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.property.Properties;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DimensionalQuarryEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final float miniChunkRotationSpeed = 0.5f; // For rotating the rendered minichunk inside the quarry

    public static final int EJECTION_COOLDOWN = 20;
    public static final int ENERGY_CAPACITY = 1_000_000;
    public static final int UPGRADE_SLOTS = 3;
    public static final int INVENTORY_SIZE = 9 + UPGRADE_SLOTS; // 9 storage slots + 3 upgrade
    public static final int QUARRY_DATA_SIZE = 10;

    private int miningCooldown;
    private int ejectCooldown;
    private int currentEnergyConsumption;
    private int currentTicksPerBlock;
    private boolean running;
    private float miniChunkRotation;
    private ItemStack overflowingItemStack;

    private Long2ObjectMap<DimensionalQuarrySearcher.BlockStateInfo> blockStatesToMine = new Long2ObjectOpenHashMap<>();
    private ObjectIterator<Long2ObjectMap.Entry<DimensionalQuarrySearcher.BlockStateInfo>> blockStatesToMineIt;
    private DimensionalQuarrySearcher searcher = new DimensionalQuarrySearcher(this);
    private boolean searching;
    private Integer currentYLevel;
    private ServerLevel currentDimension;

    private final DimensionalQuarryItemStackHandler inventory = new DimensionalQuarryItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            // Update slots to client when upgrades change
            if (!level.isClientSide) {
                if (slot == 0) {
                    // Pickaxe slot, update the upgrades
                    updateUpgrades();
                } else if (slot == 1) {
                    // Dimension slot, update the mining dimension
                    updateDimension((ServerLevel) level);
                }

                if (slot < 3) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
                } else if (overflowingItemStack != null) {
                    checkIfOverflowIsFixed();
                }
            }

            setChanged();
        }
    };
    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);

    private HashMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> itemHandlerCapCaches = new HashMap<>();

    public DimensionalQuarryEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), pos, blockState);
        this.running = false;
        this.searching = false;
        this.miningCooldown = 0;
        this.ejectCooldown = 0;
        this.overflowingItemStack = null;
        this.currentYLevel = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.updateEnergyConsumption();
        this.updateTicksPerBlock();

        if (level != null && !level.isClientSide) {
            createItemHandlerCapCaches((ServerLevel) level);
        }
    }

    public ItemStack getPickaxeSlot() {
        return this.inventory.getStackInSlot(0).copy();
    }

    public ItemStack getMiniChunkInSlot() {
        return this.inventory.getStackInSlot(1).copy();
    }

    public ContainerData getQuarryData() {
        return this.quarryData;
    }

    public DimensionalQuarrySearcher getSearcher() {
        return this.searcher;
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

    public int getEnergyConsumptionPerTick() {
        return this.currentEnergyConsumption;
    }

    private void updateEnergyConsumption() {
        ItemStack pick = this.getPickaxeSlot();

        if (pick.isEmpty()) {
            this.currentEnergyConsumption = Config.dimensionalQuarryConsumptions.getFirst();
            return;
        }

        int unbreakingLevel = EnchantmentHelper.getTagEnchantmentLevel(level.holder(Enchantments.UNBREAKING).get(), pick);

        if (unbreakingLevel < 0) {
            this.currentEnergyConsumption = Config.dimensionalQuarryConsumptions.getFirst();
        } else if (unbreakingLevel < 4) {
            this.currentEnergyConsumption = Config.dimensionalQuarryConsumptions.get(unbreakingLevel);
        } else {
            // If any mods add higher unbreaking levels
            this.currentEnergyConsumption = Config.dimensionalQuarryConsumptions.getLast();
        }
    }

    public int getTicksPerBlock() {
        return this.currentTicksPerBlock;
    }

    public void updateBlockStatesToMine(Long2ObjectMap<DimensionalQuarrySearcher.BlockStateInfo> info) {
        this.blockStatesToMine = info;
        this.blockStatesToMineIt = this.blockStatesToMine.long2ObjectEntrySet().iterator();
        this.searching = false;
        LOGGER.debug("Got blocks: " + this.blockStatesToMine.size());
    }

    private void updateTicksPerBlock() {
        ItemStack pick = this.getPickaxeSlot();

        if (pick.isEmpty()) {
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.getFirst();
            return;
        }

        int efficiencyLevel = EnchantmentHelper.getTagEnchantmentLevel(level.holder(Enchantments.EFFICIENCY).get(), pick);

        if (efficiencyLevel < 0) {
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.getFirst();
        } else if (efficiencyLevel < 6) {
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.get(efficiencyLevel);
        } else {
            // If any mods add higher unbreaking levels
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.getLast();
        }
    }

    private void updateUpgrades() {
        updateEnergyConsumption();
        updateTicksPerBlock();
    }

    public static List<MiningRequirement> checkMiningRequirements(int energyStored, int energyConsumption, IItemHandler inv, boolean inventoryFull) {
        List<MiningRequirement> ret = new ArrayList<>();

        if (energyStored < energyConsumption)
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

    public void checkIfOverflowIsFixed() {
        ItemStack simItemStack = ItemHandlerHelper.insertItemStacked(this.inventory, this.overflowingItemStack, true);

        // If the inventory is not full anymore, continue the mining process
        if (simItemStack.isEmpty()) {
            this.overflowingItemStack = null;
        }
    }

    public void setRunning(boolean state) {
        this.running = state;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        if (level.isClientSide)
            return;

        if (!entity.searching && entity.running && checkMiningRequirements(entity.getEnergyStored(), entity.getEnergyConsumptionPerTick(), entity.inventory, isStorageFull(entity.getInventory(), 3, entity.getInventorySlots(), entity.overflowingItemStack)).getFirst() == MiningRequirement.ok) {
            entity.mineNextBlock((ServerLevel) level);
        }

        entity.tryEject(level, pos);
    }

    public void mineNextBlock(ServerLevel level) {
        if (this.blockStatesToMineIt == null || !this.blockStatesToMineIt.hasNext()) {
            this.generateBlockStatesToMine(level);
            return;
        }

        // Don't draw energy while searching for blocks
        if (this.searcher.getCurrentState() == DimensionalQuarrySearcher.State.RUNNING)
            return;

        // Draw energy every tick
        energy.extractEnergy(this.getEnergyConsumptionPerTick(), false);

        this.miningCooldown++;
        if (this.miningCooldown <= this.getTicksPerBlock()) {
            return;
        }
        this.miningCooldown = 0;

        var it = this.blockStatesToMineIt.next();
        BlockState blockToMineState = it.getValue().state;
        BlockPos pos = BlockPos.of(it.getLongKey());
        this.currentYLevel = pos.getY();

        // Try to add the mined block to the inventory
        LootParams.Builder params = new LootParams.Builder(this.currentDimension);
        params = params.withOptionalParameter(LootContextParams.ORIGIN, pos.getCenter());
        params = params.withOptionalParameter(LootContextParams.TOOL, getPickaxeSlot());

        List<ItemStack> itemStacks = blockToMineState.getDrops(params);

        // Check if we can insert all drops
        for (ItemStack itemStack : itemStacks) {
            ItemStack simItemStack = ItemHandlerHelper.insertItemStacked(this.inventory, itemStack, true);

            // If the inventory is full, stop the mining process
            if (!simItemStack.isEmpty()) {
                this.overflowingItemStack = simItemStack;
                return;
            } else {
                this.overflowingItemStack = null;
            }
        }

        // Insert the items
        for (ItemStack itemStack : itemStacks) {
            ItemHandlerHelper.insertItemStacked(this.inventory, itemStack, false);
        }

    }

    public void tryEject(Level level, BlockPos pos) {
        // TODO update this, cache the eject inventory and only look for more if the inventory is full or gets destroyed
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

    private void generateBlockStatesToMine(ServerLevel level) {
        if (searcher.getCurrentState() == DimensionalQuarrySearcher.State.IDLE) {
            if (this.currentDimension == null)
                this.updateDimension(level);

            LOGGER.debug("Starting search for: " + worldPosition);
            this.searching = true;
            searcher.setDimension(this.currentDimension);
            searcher.start();
        }
    }

    private void updateDimension(ServerLevel level) {
        this.currentDimension = null;
        ResourceKey<Level> dim = this.getMiniChunkDimension();

        if (dim == null) {
            LOGGER.debug("Mini Chunk Dimension is null!");
            return;
        }

        this.currentDimension = level.getServer().getLevel(dim);
    }

    private ResourceKey<Level> getMiniChunkDimension() {
        ItemStack miniChunk = this.getMiniChunkInSlot();

        if (miniChunk.isEmpty())
            return null;

        if (Block.byItem(miniChunk.getItem()) instanceof MiniChunkBlock block) {
            switch (block.chunkType) {
                case MiniChunkBlock.MiniChunkType.overworld:
                    return CursedComponentsMod.OVERWORLD_SAMPLE_DIMENSION_KEY;
                case MiniChunkBlock.MiniChunkType.nether:
                    return Level.NETHER;
                case MiniChunkBlock.MiniChunkType.end:
                    return Level.END;
            }
        }
        return null;
    }

    public static boolean isStorageFull(IItemHandler storage, int from, int to, ItemStack overflowingItem) {
        if (from < 0 || to > storage.getSlots())
            new IllegalArgumentException("From must be less than 0 and to must be less than " + storage.getSlots() + ", got (from: " + from + ", to: " + to + ")");

        if (overflowingItem != null)
            return true;

        for (int i = from; i < to; i++) {
            ItemStack stack = storage.getStackInSlot(i);
            if (stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isStorageFull(IItemHandler storage) {
        return isStorageFull(storage, 0, storage.getSlots(), null);
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
        tag.putBoolean("cursedcomponents:dimensional_quarry_running", this.running);
        CompoundTag inventoryTag = inventory.serializeNBT(registries);
        tag.put("cursedcomponents:dimensional_quarry_inventory", inventoryTag);

        /*if (this.currentChunkPos != null)
            tag.putLong("cursedcomponents:dimensional_quarry_chunk", this.currentChunkPos.toLong());

        if (this.currentMiningPos != null)
            tag.putLong("cursedcomponents:dimensional_quarry_mine_pos", this.currentMiningPos.asLong());*/
        //tag.put("cursedcomponents:dimensional_quarry_overflowItem", overflowingItemStack.save(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.running = tag.getBoolean("cursedcomponents:dimensional_quarry_running");

        if (tag.contains("cursedcomponents:dimensional_quarry_inventory")) {
            Tag inv_tag = tag.get("cursedcomponents:dimensional_quarry_inventory");
            if (inv_tag instanceof CompoundTag inv_compound_tag)
                this.inventory.deserializeNBT(registries, inv_compound_tag);
        }

        /*if (tag.contains("cursedcomponents:dimensional_quarry_chunk"))
            this.currentChunkPos = new ChunkPos(tag.getLong("cursedcomponents:dimensional_quarry_chunk"));

        if (tag.contains("cursedcomponents:dimensional_quarry_mine_pos"))
            this.currentMiningPos = BlockPos.of(tag.getLong("cursedcomponents:dimensional_quarry_mine_pos"));*/

        /*if (tag.contains("cursedcomponents:dimensional_quarry_overflowItem")) {
            ItemStack.parse(registries, tag.get("cursedcomponents:dimensional_quarry_overflowItem")).ifPresent(stack -> this.overflowingItemStack = stack);
        }*/
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
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> currentYLevel != null ? currentYLevel : 0;
                case 2 -> miningCooldown;
                case 3 -> running ? 1 : 0;
                case 4 -> worldPosition.getX();
                case 5 -> worldPosition.getY();
                case 6 -> worldPosition.getZ();
                case 7 -> isStorageFull(inventory, 3, getInventorySlots(), overflowingItemStack) ? 1 : 0;
                case 8 -> getEnergyConsumptionPerTick();
                case 9 -> getTicksPerBlock();
                default -> 0;
            };
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