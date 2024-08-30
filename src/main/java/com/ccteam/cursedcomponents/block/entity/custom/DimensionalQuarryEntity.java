package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.Config;
import com.ccteam.cursedcomponents.ModRegistries;
import com.ccteam.cursedcomponents.block.custom.MiniChunkBlock;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.block.stack_handler.DimensionalQuarryItemStackHandler;
import com.ccteam.cursedcomponents.block.threading.DimensionalQuarrySearcher;
import com.ccteam.cursedcomponents.item.base.InventoryItem;
import com.ccteam.cursedcomponents.network.to_client.DimensionalQuarryMinMaxYLevelPayload;
import com.ccteam.cursedcomponents.network.to_client.DimensionalQuarryYLevelPayload;
import com.ccteam.cursedcomponents.util.ModTags;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class DimensionalQuarryEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final float miniChunkRotationSpeed = 0.5f; // For rotating the rendered minichunk inside the quarry
    public static final int EJECTION_COOLDOWN = 20;
    public static final int EJECTION_ATTEMPTS_PER_CYCLE = 4;
    public static final int ENERGY_CAPACITY = 1_000_000;
    public static final int UPGRADE_SLOTS = 3;
    public static final int INVENTORY_SIZE = 9 + UPGRADE_SLOTS; // 9 storage slots + 3 upgrade
    public static final int QUARRY_DATA_SIZE = 10;

    // Entity
    private int miningCooldown;
    private int ejectCooldown;
    private int currentEnergyConsumption;
    private int currentTicksPerBlock;
    private boolean running;
    private float miniChunkRotation;
    private Set<Item> blacklistedItems;
    private ItemStack overflowingItemStack;

    // Searcher
    private DimensionalQuarrySearcher searcher = new DimensionalQuarrySearcher(this);
    private Int2ObjectMap<DimensionalQuarrySearcher.BlockStateInfo> blockStatesToMine;
    private Integer currentYLevel;
    private Integer maxCurrentYLevel;
    private Integer minCurrentYLevel;
    private ChunkPos currentChunkPos;
    private ServerLevel currentDimension;

    private final DimensionalQuarryItemStackHandler inventory = new DimensionalQuarryItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            // Update slots to client when upgrades change
            if (level != null && !level.isClientSide) {
                if (slot == 0) {
                    // Pickaxe slot, update the upgrades
                    updateUpgrades();
                } else if (slot == 1) {
                    // Dimension slot, update the mining dimension
                    updateDimension((ServerLevel) level);
                    resetSearcher((ServerLevel) level, false);
                } else if (slot == 2) {
                    // Blacklist filter slot
                    updateBlacklistedItems();
                }

                if (slot < UPGRADE_SLOTS) {
                    // A bit hacky but send data to client when changing upgrades
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
                } else if (overflowingItemStack != null) {
                    isOverflowIsFixed();
                }
            }

            setChanged();
        }
    };
    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);
    private final HashMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> itemHandlerCapCaches = new HashMap<>();

    public DimensionalQuarryEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DIMENSIONAL_QUARRY_BE.get(), pos, blockState);
        this.running = false;
        this.miningCooldown = 0;
        this.ejectCooldown = 0;
        this.overflowingItemStack = null;
        this.currentYLevel = null;
        this.currentChunkPos = null;
        this.maxCurrentYLevel = null;
        this.minCurrentYLevel = null;
        this.blockStatesToMine = null;
        this.blacklistedItems = new HashSet<>();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.updateUpgrades();
        this.updateBlacklistedItems();

        if (level != null && !level.isClientSide) {
            createItemHandlerCapCaches((ServerLevel) level);
        }
    }

    // Getters

    public ItemStack getPickaxeSlot() {
        return this.inventory.getStackInSlot(0).copy();
    }

    public ItemStack getMiniChunkInSlot() {
        return this.inventory.getStackInSlot(1).copy();
    }

    public ItemStack getItemFilterSlot() {
        return this.inventory.getStackInSlot(2).copy();
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
        return this.inventory.getStacks();
    }

    public float getMiniChunkRotation() {
        // maybe change this to use server time
        miniChunkRotation = (miniChunkRotation + miniChunkRotationSpeed) % 360;
        return miniChunkRotation;
    }

    public Integer getCurrentYLevel() {
        return currentYLevel;
    }

    public Integer getMaxCurrentYLevel() {
        return maxCurrentYLevel;
    }

    public Integer getMinCurrentYLevel() {
        return minCurrentYLevel;
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

    public int getTicksPerBlock() {
        return this.currentTicksPerBlock;
    }

    private List<IItemHandler> getAttachedStorages() {
        List<IItemHandler> attachedStorages = new ArrayList<>();

        this.itemHandlerCapCaches.forEach((ejectDir, cache) -> {
            IItemHandler handler = cache.getCapability();
            if (handler != null && !(handler instanceof DimensionalQuarryItemStackHandler))
                attachedStorages.add(handler);
        });

        return attachedStorages;
    }

    // Setters

    public void setRunning(boolean state) {
        this.running = state;
        setChanged();
    }

    public void setCurrentYLevel(Integer currentYLevel) {
        this.currentYLevel = currentYLevel;

        if (!this.level.isClientSide)
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) this.level, this.level.getChunk(this.worldPosition).getPos(), new DimensionalQuarryYLevelPayload(this.currentYLevel, this.worldPosition));

        setChanged();
    }

    public void setMinMaxCurrentYLevel(Integer minCurrentYLevel, Integer maxCurrentYLevel) {
        this.minCurrentYLevel = minCurrentYLevel;
        this.maxCurrentYLevel = maxCurrentYLevel;

        if (!this.level.isClientSide)
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) this.level, this.level.getChunk(this.worldPosition).getPos(), new DimensionalQuarryMinMaxYLevelPayload(this.minCurrentYLevel, this.maxCurrentYLevel, this.worldPosition));

        setChanged();
    }

    // Updaters

    private boolean updateDimension(ServerLevel level) {
        if (level.isClientSide)
            return false;

        this.currentDimension = null;
        ResourceKey<Level> dim = this.getMiniChunkDimension();
        if (dim == null) {
            return false;
        }

        this.currentDimension = level.getServer().getLevel(dim);
        setChanged();
        return this.currentDimension != null;
    }

    public void updateBlockStatesToMine(Int2ObjectMap<DimensionalQuarrySearcher.BlockStateInfo> info) {
        this.blockStatesToMine = info;
        this.setCurrentYLevel(this.blockStatesToMine.keySet().intStream().max().orElseThrow());
        if (this.minCurrentYLevel == null)
            this.setMinMaxCurrentYLevel(this.blockStatesToMine.keySet().intStream().min().orElseThrow(), this.getCurrentYLevel());

        LOGGER.debug("Got blocks, max y: " + this.currentYLevel);
        setChanged();
    }

    private void updateUpgrades() {
        updateEnergyConsumption();
        updateTicksPerBlock();
        setChanged();
    }

    private void updateTicksPerBlock() {
        ItemStack pick = this.getPickaxeSlot();

        if (pick.isEmpty()) {
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.getFirst();
            return;
        }

        int efficiencyLevel = 0;
        if (this.level != null && this.level.holder(Enchantments.EFFICIENCY).isPresent())
            efficiencyLevel = EnchantmentHelper.getTagEnchantmentLevel(this.level.holder(Enchantments.EFFICIENCY).get(), pick);

        if (efficiencyLevel < 0) {
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.getFirst();
        } else if (efficiencyLevel < 6) {
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.get(efficiencyLevel);
        } else {
            // If any mods add higher efficiency levels
            this.currentTicksPerBlock = Config.dimensionalQuarrySpeed.getLast();
        }
    }

    private void updateEnergyConsumption() {
        ItemStack pick = this.getPickaxeSlot();

        this.currentEnergyConsumption = Config.dimensionalQuarryBaseConsumption;

        if (pick.isEmpty())
            return;

        int unbreakingLevel = 0;
        if (this.level != null && this.level.holder(Enchantments.UNBREAKING).isPresent())
            unbreakingLevel = EnchantmentHelper.getTagEnchantmentLevel(this.level.holder(Enchantments.UNBREAKING).get(), pick);

        int efficiencyLevel = 0;
        if (this.level != null && this.level.holder(Enchantments.EFFICIENCY).isPresent())
            efficiencyLevel = EnchantmentHelper.getTagEnchantmentLevel(this.level.holder(Enchantments.EFFICIENCY).get(), pick);

        boolean hasSilkTouch = this.level != null &&
                this.level.holder(Enchantments.SILK_TOUCH).isPresent() &&
                EnchantmentHelper.getTagEnchantmentLevel(this.level.holder(Enchantments.SILK_TOUCH).get(), pick) > 0;

        int fortuneLevel = 0;
        if (this.level != null && this.level.holder(Enchantments.FORTUNE).isPresent())
            fortuneLevel = EnchantmentHelper.getTagEnchantmentLevel(this.level.holder(Enchantments.FORTUNE).get(), pick);

        if (unbreakingLevel > 0 && unbreakingLevel < 4) {
            this.currentEnergyConsumption *= Config.dimensionalQuarryUnbreakingConsumptionDecrease.get(unbreakingLevel - 1);
        } else if (unbreakingLevel > 4) {
            // If any mods add higher unbreaking levels
            this.currentEnergyConsumption *= Config.dimensionalQuarryUnbreakingConsumptionDecrease.getLast();
        }

        if (efficiencyLevel > 0 && efficiencyLevel < 6) {
            this.currentEnergyConsumption *= Config.dimensionalQuarryEfficiencyConsumptionIncrease.get(efficiencyLevel - 1);
        } else if (efficiencyLevel > 6) {
            // If any mods add higher efficiency levels
            this.currentEnergyConsumption *= Config.dimensionalQuarryEfficiencyConsumptionIncrease.getLast();
        }

        if (hasSilkTouch)
            this.currentEnergyConsumption *= Config.dimensionalQuarrySilkTouchConsumptionIncrease;

        if (fortuneLevel > 0 && fortuneLevel < 4) {
            this.currentEnergyConsumption *= Config.dimensionalQuarryFortuneConsumptionIncrease.get(fortuneLevel - 1);
        } else if (fortuneLevel > 4) {
            this.currentEnergyConsumption *= Config.dimensionalQuarryFortuneConsumptionIncrease.getLast();
        }
    }

    public void updateBlacklistedItems() {
        if (this.level == null)
            return;

        setChanged();
        ItemStack filter = this.getItemFilterSlot();
        if (filter.getItem() instanceof InventoryItem ii) {
            IItemHandler inv = ii.getInventory(filter);
            Set<Item> items = new HashSet<>();
            for (int i = 0; i < inv.getSlots(); i++) {
                items.add(inv.getStackInSlot(i).getItem());
            }

            this.blacklistedItems = items;
            return;
        }

        this.blacklistedItems.clear();
    }

    // Checkers

    public static List<MiningRequirement> checkMiningRequirements(int energyStored, int energyConsumption, IItemHandler inv, boolean inventoryFull) {
        ItemStack pickaxe = inv.getStackInSlot(0).copy();
        ItemStack miniChunk = inv.getStackInSlot(1).copy();

        return checkMiningRequirements(energyStored, energyConsumption, miniChunk, pickaxe, inventoryFull);
    }

    public static List<MiningRequirement> checkMiningRequirements(int energyStored, int energyConsumption, List<Slot> slots, boolean inventoryFull) {
        ItemStack pickaxe = slots.get(0).getItem().copy();
        ItemStack miniChunk = slots.get(1).getItem().copy();

        return checkMiningRequirements(energyStored, energyConsumption, miniChunk, pickaxe, inventoryFull);
    }

    private static List<MiningRequirement> checkMiningRequirements(int energyStored, int energyConsumption, ItemStack miniChunk, ItemStack pickaxe, boolean inventoryFull) {
        List<MiningRequirement> ret = new ArrayList<>();

        if (energyStored < energyConsumption)
            ret.add(MiningRequirement.notEnoughEnergy);

        boolean hasPickaxe = pickaxe.is(Items.NETHERITE_PICKAXE);
        boolean hasDimension = miniChunk.is(ModTags.Items.MINI_CHUNK);

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

    public void isOverflowIsFixed() {
        ItemStack simItemStack = ItemHandlerHelper.insertItemStacked(this.inventory, this.overflowingItemStack, true);

        // If the inventory is not full anymore, continue the mining process
        if (simItemStack.isEmpty()) {
            this.overflowingItemStack = null;
            setChanged();
        }
    }

    public static boolean isStorageFull(IItemHandler storage, int from, int to, ItemStack overflowingItem) {
        if (from < 0 || to > storage.getSlots())
            throw new IllegalArgumentException("From must be less than 0 and to must be less than " + storage.getSlots() + ", got (from: " + from + ", to: " + to + ")");

        if (overflowingItem != null)
            return true;

        for (int i = from; i < to; i++) {
            ItemStack stack = storage.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isStorageFull(IItemHandler storage) {
        return isStorageFull(storage, 0, storage.getSlots(), null);
    }

    // Creators

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

    public static void tick(Level level, BlockPos pos, BlockState state, DimensionalQuarryEntity entity) {
        if (level.isClientSide)
            return;

        if (entity.running
                && entity.searcher.getCurrentState() != DimensionalQuarrySearcher.State.RUNNING
                && checkMiningRequirements(entity.getEnergyStored(), entity.getEnergyConsumptionPerTick(), entity.inventory, isStorageFull(entity.getInventory(), 3, entity.getInventorySlots(), entity.overflowingItemStack)).getFirst() == MiningRequirement.ok) {
            entity.mineNextBlock((ServerLevel) level);
        }

        entity.tryEject();
    }

    public void mineNextBlock(ServerLevel level) {
        // Draw energy every tick
        energy.extractEnergy(this.getEnergyConsumptionPerTick(), false);
        setChanged();

        this.miningCooldown++;
        if (this.miningCooldown < this.getTicksPerBlock()) {
            return;
        }
        this.miningCooldown = 0;

        var entry = this.getNextBlockEntry(level);
        if (entry == null)
            return;

        BlockState blockToMineState = entry.getRandomState();

        // Try to add the mined block to the inventory
        LootParams.Builder params = new LootParams.Builder(this.currentDimension);
        params = params.withOptionalParameter(LootContextParams.ORIGIN, worldPosition.getCenter()); // might need to be the pos of the actual block...
        params = params.withOptionalParameter(LootContextParams.TOOL, getPickaxeSlot());

        List<ItemStack> itemStacks = blockToMineState.getDrops(params);

        // Check if we can insert all drops
        for (ItemStack itemStack : itemStacks) {
            if (this.blacklistedItems.contains(itemStack.getItem()))
                continue;

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
            if (this.blacklistedItems.contains(itemStack.getItem()))
                continue;

            ItemHandlerHelper.insertItemStacked(this.inventory, itemStack, false);
        }

        this.consumeBlockState(blockToMineState);
    }

    private DimensionalQuarrySearcher.BlockStateInfo getNextBlockEntry(ServerLevel level) {
        if (this.blockStatesToMine == null) {
            this.generateBlockStatesToMine(level);
            return null;
        }

        if (this.blockStatesToMine.isEmpty()) {
            resetSearcher(level, true);
            return null;
        }

        if (this.blockStatesToMine.get(this.currentYLevel.intValue()).isEmpty()) {
            this.blockStatesToMine.remove(this.currentYLevel.intValue());
            setChanged();

            if (this.blockStatesToMine.isEmpty()) {
                resetSearcher(level, true);
                return null;
            }

            this.setCurrentYLevel(this.blockStatesToMine.keySet().intStream().max().orElseThrow());
        }

        return this.blockStatesToMine.get(this.currentYLevel.intValue());
    }

    private void consumeBlockState(BlockState state) {
        this.blockStatesToMine.get(this.currentYLevel.intValue()).decrementState(state);
        setChanged();
    }

    public void tryEject() {
        this.ejectCooldown++;
        if (this.ejectCooldown <= EJECTION_COOLDOWN) {
            return;

        }
        this.ejectCooldown = 0;

        // Check for attached containers
        List<IItemHandler> attachedStorages = getAttachedStorages();
        if (attachedStorages.isEmpty()) {
            return;
        }

        int cnt = 0;
        for (IItemHandler storage : attachedStorages) {
            if (isStorageFull(storage)) {
                // Go to next container if full
                continue;
            }

            for (int i = UPGRADE_SLOTS; i < this.getInventory().getSlots(); i++) {
                ItemStack itemStack = this.getInventory().getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    ItemStack itemStack1 = ItemHandlerHelper.insertItemStacked(storage, itemStack, false);

                    if (itemStack1.isEmpty()) {
                        inventory.setStackInSlot(i, ItemStack.EMPTY);
                        cnt++;
                    } else if (itemStack.getCount() != itemStack1.getCount()) {
                        // If we could not eject all items, set the new count and try the next storage
                        this.getInventory().setStackInSlot(i, itemStack1);
                        break;
                    }

                    if (cnt >= EJECTION_ATTEMPTS_PER_CYCLE)
                        return;
                }
            }
        }
    }

    private void resetSearcher(ServerLevel level, boolean startSearch) {
        // If we're already searching, terminate the search and create a new one
        this.setCurrentYLevel(null);
        this.setMinMaxCurrentYLevel(null, null);
        this.currentChunkPos = null;
        this.blockStatesToMine = null;

        if (startSearch) {
            this.generateBlockStatesToMine(level);
        }
    }

    private void generateBlockStatesToMine(ServerLevel level) {
        if (searcher == null || searcher.getCurrentState() != DimensionalQuarrySearcher.State.RUNNING) {
            this.searcher = new DimensionalQuarrySearcher(this);

            if (this.currentDimension == null) {
                if (!this.updateDimension(level)) {
                    LOGGER.debug("Mini chunk dimension was not found!");
                    return;
                }
            }

            searcher.updateSettings(this.currentDimension, this.currentChunkPos, this.currentYLevel);
            searcher.start();
        }
    }

    private ResourceKey<Level> getMiniChunkDimension() {
        ItemStack miniChunk = this.getMiniChunkInSlot();

        if (miniChunk.isEmpty())
            return null;

        if (Block.byItem(miniChunk.getItem()) instanceof MiniChunkBlock block) {
            return switch (block.chunkType) {
                case MiniChunkBlock.MiniChunkType.overworld -> ModRegistries.Dimension.OVERWORLD_SAMPLE_DIMENSION_KEY;
                case MiniChunkBlock.MiniChunkType.nether -> ModRegistries.Dimension.NETHER_SAMPLE_DIMENSION_KEY;
                case MiniChunkBlock.MiniChunkType.end -> ModRegistries.Dimension.END_SAMPLE_DIMENSION_KEY;
            };
        }
        return null;
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
        CompoundTag inventoryTag = this.inventory.serializeNBT(registries);
        tag.put("cursedcomponents:dimensional_quarry_inventory", inventoryTag);
        Tag energyTag = this.energy.serializeNBT(registries);
        tag.put("cursedcomponents:dimensional_quarry_energy", energyTag);

        if (this.currentYLevel != null)
            tag.putInt("cursedcomponents:dimensional_quarry_current_y_level", this.currentYLevel);

        if (this.minCurrentYLevel != null)
            tag.putInt("cursedcomponents:dimensional_quarry_min_current_y_level", this.minCurrentYLevel);

        if (this.maxCurrentYLevel != null)
            tag.putInt("cursedcomponents:dimensional_quarry_max_current_y_level", this.maxCurrentYLevel);

        if (this.searcher != null && this.searcher.getChunkPosition() != null)
            tag.putLong("cursedcomponents:dimensional_quarry_chunk", this.searcher.getChunkPosition().toLong());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.running = tag.getBoolean("cursedcomponents:dimensional_quarry_running");

        if (tag.contains("cursedcomponents:dimensional_quarry_inventory")) {
            Tag invTag = tag.get("cursedcomponents:dimensional_quarry_inventory");
            if (invTag instanceof CompoundTag invCompoundTag)
                this.inventory.deserializeNBT(registries, invCompoundTag);
        }

        if (tag.contains("cursedcomponents:dimensional_quarry_energy")) {
            Tag energyTag = tag.get("cursedcomponents:dimensional_quarry_energy");
            if (energyTag != null)
                this.energy.deserializeNBT(registries, energyTag);
        }

        if (tag.contains("cursedcomponents:dimensional_quarry_current_y_level"))
            this.currentYLevel = tag.getInt("cursedcomponents:dimensional_quarry_current_y_level");

        if (tag.contains("cursedcomponents:dimensional_quarry_min_current_y_level"))
            this.minCurrentYLevel = tag.getInt("cursedcomponents:dimensional_quarry_min_current_y_level");

        if (tag.contains("cursedcomponents:dimensional_quarry_max_current_y_level"))
            this.maxCurrentYLevel = tag.getInt("cursedcomponents:dimensional_quarry_max_current_y_level");

        if (tag.contains("cursedcomponents:dimensional_quarry_chunk"))
            this.currentChunkPos = new ChunkPos(tag.getLong("cursedcomponents:dimensional_quarry_chunk"));
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
                case 1 -> currentYLevel != null ? currentYLevel : Integer.MAX_VALUE;
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