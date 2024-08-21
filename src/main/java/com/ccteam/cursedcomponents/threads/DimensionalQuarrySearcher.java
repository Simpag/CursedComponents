package com.ccteam.cursedcomponents.threads;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.Random;
import java.util.Set;


public class DimensionalQuarrySearcher extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DimensionalQuarryEntity quarryEntity;
    private State state;
    private ServerLevel dimension;

    private Int2ObjectMap<BlockStateInfo> blockStatesToMine = new Int2ObjectOpenHashMap<>();
    private ChunkAccess chunkAccess;
    private Integer startY;
    private Random rng;

    public DimensionalQuarrySearcher(DimensionalQuarryEntity entity) {
        this(entity, State.FRESH);
    }

    public DimensionalQuarrySearcher(DimensionalQuarryEntity entity, State state) {
        super("Dimensional Quarry Searcher: " + entity.getBlockPos());
        this.state = state;
        this.quarryEntity = entity;
        this.rng = new Random();
        this.rng.setSeed(System.nanoTime());

        setDaemon(true);
    }

    public State getCurrentState() {
        return state;
    }

    public ChunkPos getChunkPosition() {
        if (this.chunkAccess == null)
            return null;

        return this.chunkAccess.getPos();
    }

    public void updateSettings(ServerLevel dimension, ChunkPos chunk, Integer startY) {
        this.dimension = dimension;

        if (chunk == null || startY == null)
            return;

        this.chunkAccess = this.dimension.getChunk(chunk.x, chunk.z);
        this.startY = startY;
    }

    @Override
    public void run() {
        this.state = State.RUNNING;
        if (this.chunkAccess == null) {
            if (!this.updateSamplingChunk()) {
                this.state = State.ERROR;
                return;
            }
        }

        this.blockStatesToMine.clear();
        Reference2BooleanMap<Block> acceptedBlocks = new Reference2BooleanOpenHashMap<>();

        AABB area = AABB.encapsulatingFullBlocks(
                this.chunkAccess.getPos().getBlockAt(0, this.chunkAccess.getMinBuildHeight(), 0),
                this.chunkAccess.getPos().getBlockAt(15, this.startY - 1, 15)
        );

        BlockPos.betweenClosedStream(area).forEach(pos -> {
            if (quarryEntity.isRemoved() || quarryEntity.getSearcher() != this) {
                // Stop if the quarry is destroyed
                this.state = State.ERROR;
                this.interrupt();
                return;
            }

            BlockState blockState = this.chunkAccess.getBlockState(pos);
            Block blockToMine = blockState.getBlock();

            // Filters here
            if (!acceptedBlocks.containsKey(blockToMine)) {
                // Check blacklist filters here and such....
                acceptedBlocks.put(blockToMine, this.isMineable(blockState, blockToMine));
            }

            if (acceptedBlocks.getBoolean(blockToMine)) {
                this.blockStatesToMine.computeIfAbsent(pos.getY(), k -> new BlockStateInfo()).increment(blockState);
            }

        });

        this.state = State.FINISHED;

        if (!quarryEntity.isRemoved() && quarryEntity.getSearcher() == this) {
            //Only update search if we are still valid
            quarryEntity.updateBlockStatesToMine(this.blockStatesToMine);
        }
    }

    private boolean updateSamplingChunk() {
        if (this.dimension == null) {
            LOGGER.debug("Target dimension is null!");
            throw new IllegalArgumentException("Target dimension is null!");
            //return false;
        }

        int randomX = this.rng.nextInt(-1_000_000, 1_000_000);
        int randomZ = this.rng.nextInt(-1_000_000, 1_000_000);
        ChunkPos chunkPos = new ChunkPos(randomX, randomZ);

        BlockPos startPos = this.getFirstPos(chunkPos);

        if (startPos == null) {
            LOGGER.debug("Starting Y position in dimension is null!");
            return false;
        }

        this.chunkAccess = this.dimension.getChunk(chunkPos.x, chunkPos.z);
        this.startY = startPos.getY();

        return true;
    }

    private boolean isMineable(BlockState blockToMineState, Block blockToMine) {
        if (blockToMineState.isEmpty() || blockToMine.defaultDestroyTime() <= 0) {
            // Skip air and unbreakable blocks
            return false;
        }

        if (blockToMine instanceof LiquidBlock || blockToMine instanceof BubbleColumnBlock) {
            // Skip liquids
            return false;
        }

        return true;
    }

    private BlockPos getFirstPos(ChunkPos chunkPos) {
        // Gets a rough estimate of the first breakable block
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();
        for (int y = this.dimension.getMaxBuildHeight(); y > this.dimension.getMinBuildHeight(); y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = this.dimension.getBlockState(pos);

            if (isMineable(state, state.getBlock()))
                return pos;
        }

        return null;
    }

    public enum State {
        FRESH,
        FINISHED,
        RUNNING,
        ERROR,
    }

    public class BlockStateInfo {
        public Object2IntMap<BlockState> blockStates;

        public BlockStateInfo() {
            this.blockStates = new Object2IntOpenHashMap<>();
        }

        public void increment(BlockState state) {
            this.blockStates.put(state, this.blockStates.getOrDefault(state, 0) + 1);
        }

        public boolean isEmpty() {
            return this.blockStates.isEmpty();
        }

        public BlockState getRandomState() {
            int r = rng.nextInt(this.blockStates.size());
            var it = this.blockStates.keySet().iterator();
            it.skip(r);

            return it.next();
        }

        public void decrementState(BlockState bs) {
            int currNumBlocks = this.blockStates.getInt(bs);

            if (currNumBlocks > 1)
                this.blockStates.put(bs, currNumBlocks - 1);
            else
                this.blockStates.removeInt(bs);
        }
    }
}
