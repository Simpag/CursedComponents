package com.ccteam.cursedcomponents.threads;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.Random;

public class DimensionalQuarrySearcher extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DimensionalQuarryEntity quarryEntity;
    private State state = State.IDLE;
    private ServerLevel dimension;

    private Long2ObjectMap<BlockStateInfo> blockStatesToMine = new Long2ObjectOpenHashMap<>();
    private ChunkPos chunkPos;
    private BlockPos currentPos;

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

        while (true) {
            if (quarryEntity.isRemoved()) {
                // Stop if the quarry is destroyed
                LOGGER.debug("Stopping, quarry got destroyed...");
                return;
            }

            BlockState blockToMineState = this.dimension.getBlockState(this.currentPos);

            // Filters here
            if (blockToMineState.isEmpty() || blockToMineState.getDestroySpeed(this.dimension, this.currentPos) < 0) {
                // Skip air and unbreakable blocks
                LOGGER.debug("Block is air or unbreakable..");
                continue;
            }

            Block blockToMine = blockToMineState.getBlock();
            if (blockToMine instanceof LiquidBlock || blockToMine instanceof BubbleColumnBlock) {
                // Skip liquids
                LOGGER.debug("Block is liquid...");
                continue;
            }

            if (!acceptedBlocks.containsKey(blockToMine)) {
                // Check blacklist filters here and such....
                acceptedBlocks.put(blockToMine, true);
            }

            if (acceptedBlocks.getBoolean(blockToMine)) {
                long pos = this.currentPos.asLong();
                this.blockStatesToMine.computeIfAbsent(pos, k -> new BlockStateInfo(blockToMineState)).increment();
                LOGGER.debug("Added block; " + blockToMine);
            }

            // Move +x once
            var nextPos = this.currentPos.east();
            if (nextPos.getX() >= this.chunkPos.getMaxBlockX()) {
                // If we reach the end of x, move in +z once
                nextPos = new BlockPos(this.chunkPos.getMinBlockX(), this.currentPos.getY(), this.currentPos.getZ() + 1);
            }

            // If we reached the end of z, move -y once
            if (nextPos.getZ() >= this.chunkPos.getMaxBlockZ()) {
                nextPos = new BlockPos(this.chunkPos.getMinBlockX(), this.currentPos.getY() - 1, this.chunkPos.getMinBlockZ());
                LOGGER.debug("MOVED Z: " + nextPos);
            }

            // If we reached the end of y, we're done
            if (nextPos.getY() <= this.dimension.getMinBuildHeight() + 1) {
                LOGGER.debug("FINISHED " + this);
                break;
            }

            this.currentPos = nextPos;
        }

        this.state = State.FINISHED;
        this.chunkPos = null;
        this.currentPos = null;
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
        this.currentPos = startPos;

        return true;
    }

    private BlockPos getFirstPos(ChunkPos chunkPos) {
        // Gets a rough estimate of the first breakable block
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();
        for (int y = this.dimension.getMaxBuildHeight(); y > this.dimension.getMinBuildHeight(); y -= 5) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = this.dimension.getBlockState(pos);

            if (!state.isEmpty() && state.getDestroySpeed(this.dimension, pos) > 0)
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
        public BlockState state;
        public int count;

        public BlockStateInfo(BlockState state) {
            this.state = state;
            this.count = 0;
        }

        public void increment() {
            this.count++;
        }
    }
}
