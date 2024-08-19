package com.ccteam.cursedcomponents.threads;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DimensionalQuarrySearcher extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DimensionalQuarryEntity quarryEntity;
    private State state = State.IDLE;
    private ServerLevel dimension;

    private Int2ObjectMap<BlockStateInfo> blockStatesToMine = new Int2ObjectOpenHashMap<>();
    private ChunkPos chunkPos;
    private int startY;

    public DimensionalQuarrySearcher(DimensionalQuarryEntity entity) {
        super("Dimensional Quarry Searcher: " + entity.getBlockPos());
        this.quarryEntity = entity;
        setDaemon(true);
    }

    public State getCurrentState() {
        return state;
    }

    public void setDimension(ServerLevel dimension) {
        this.dimension = dimension;
    }

    @Override
    public void run() {
        this.state = State.RUNNING;
        if (!this.updateSamplingChunk())
            return;

        this.blockStatesToMine.clear();
        Reference2BooleanMap<Block> acceptedBlocks = new Reference2BooleanOpenHashMap<>();

        for (int y=this.startY; y > this.dimension.getMinBuildHeight(); y--) {
            for (int x = this.chunkPos.getMinBlockX(); x <= this.chunkPos.getMaxBlockX(); x++) {
                for (int z = this.chunkPos.getMinBlockZ(); z <= this.chunkPos.getMaxBlockZ(); z++) {
                    if (quarryEntity.isRemoved()) {
                        // Stop if the quarry is destroyed
                        LOGGER.debug("Stopping, quarry got destroyed...");
                        return;
                    }

                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState blockToMineState = this.dimension.getBlockState(pos);
                    Block blockToMine = blockToMineState.getBlock();

                    // Filters here
                    if (!this.isMineable(blockToMineState, blockToMine, pos))
                        continue;

                    if (!acceptedBlocks.containsKey(blockToMine)) {
                        // Check blacklist filters here and such....
                        acceptedBlocks.put(blockToMine, true);
                    } else {
                        acceptedBlocks.put(blockToMine, false);
                    }

                    if (acceptedBlocks.getBoolean(blockToMine)) {
                        this.blockStatesToMine.computeIfAbsent(y, k -> new BlockStateInfo()).increment(blockToMineState);
                        LOGGER.debug("Added block; " + blockToMine);
                    }
                }
            }
        }

        this.state = State.FINISHED;
        this.chunkPos = null;
        LOGGER.debug("Sending data: " + this.blockStatesToMine.size());
        if (quarryEntity.getSearcher() == this) {
            //Only update search if we are still valid
            quarryEntity.updateBlockStatesToMine(this.blockStatesToMine);
        }
    }

    private boolean updateSamplingChunk() {
        if (this.dimension == null) {
            LOGGER.debug("Target dimension is null!");
            return false;
        }

        Random random = new Random();
        int randomX = random.nextInt(-1_000_000, 1_000_000);
        int randomZ = random.nextInt(-1_000_000, 1_000_000);
        ChunkPos chunkPos = new ChunkPos(randomX, randomZ);

        BlockPos startPos = this.getFirstPos(chunkPos);

        if (startPos == null) {
            LOGGER.debug("Starting Y position in dimension is null!");
            return false;
        }

        this.chunkPos = chunkPos;
        this.startY = startPos.getY();

        return true;
    }

    private boolean isMineable(BlockState blockToMineState, Block blockToMine, BlockPos pos) {
        if (blockToMineState.isEmpty() || blockToMineState.getDestroySpeed(this.dimension, pos) <= 0) {
            // Skip air and unbreakable blocks
            LOGGER.debug("Block is air, insta-break or unbreakable.." + blockToMineState.getBlock().getName());
            return false;
        }

        if (blockToMine instanceof LiquidBlock || blockToMine instanceof BubbleColumnBlock) {
            // Skip liquids
            LOGGER.debug("Block is liquid...");
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

            if (isMineable(state, state.getBlock(), pos))
                return pos;
        }

        return null;
    }

    public enum State {
        IDLE,
        RUNNING,
        ERROR,
        FINISHED,
    }

    public class BlockStateInfo {
        public Object2IntMap<BlockState> blockStates;
        private Random rng;

        public BlockStateInfo() {
            this.blockStates = new Object2IntOpenHashMap<>();
            this.rng = new Random();
        }

        public void increment(BlockState state) {
            this.blockStates.put(state, this.blockStates.getOrDefault(state, 0) + 1);
        }

        public boolean isEmpty() {
            return this.blockStates.isEmpty();
        }

        public BlockState getRandomState() {
            int r = this.rng.nextInt(this.blockStates.size()-1);
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
