package com.ccteam.cursedcomponents.threading;

import com.ccteam.cursedcomponents.ModRegistries;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;

import java.util.Random;


public class DimensionalQuarrySearcher extends Thread {
    private static final int MAX_CHUNK_ATTEMPTS = 50;

    private final DimensionalQuarryEntity quarryEntity;
    private State state;
    private ServerLevel dimension;

    private final Int2ObjectMap<BlockStateInfo> blockStatesToMine = new Int2ObjectOpenHashMap<>();
    private final Random rng = new Random();
    private ChunkAccess chunkAccess;
    private Integer startY;

    public DimensionalQuarrySearcher(DimensionalQuarryEntity entity) {
        this(entity, State.FRESH);
    }

    public DimensionalQuarrySearcher(DimensionalQuarryEntity entity, State state) {
        super("Dimensional Quarry Searcher: " + entity.getBlockPos());
        this.state = state;
        this.quarryEntity = entity;
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
                this.blockStatesToMine.computeIfAbsent(pos.getY(), k -> new BlockStateInfo()).incrementState(blockState);
            }

        });

        this.state = State.FINISHED;

        if (!quarryEntity.isRemoved() && quarryEntity.getSearcher() == this) {
            //Only update search if we are still valid
            quarryEntity.updateBlockStatesToMine(this.blockStatesToMine);
        }
    }

    private boolean updateSamplingChunk(int attempt) {
        if (this.dimension == null) {
            throw new IllegalArgumentException("Target dimension is null!");
        }

        int randomX = this.rng.nextInt(-1_000_000, 1_000_000);
        int randomZ = this.rng.nextInt(-1_000_000, 1_000_000);

        if (this.dimension.dimension() == ModRegistries.Dimension.END_SAMPLE_DIMENSION_KEY) {
            // If the selected chunk is within a 1000 block radius (62.5 chunks) of the center island
            // then it's just void so sample a chunk from outside this area
            if (Math.abs(randomX) < 63)
                randomX = this.rng.nextFloat() < 0.5 ? 63 : -63;

            if (Math.abs(randomZ) < 63)
                randomZ = this.rng.nextFloat() < 0.5 ? 63 : -63;
        }

        ChunkPos chunkPos = new ChunkPos(randomX, randomZ);
        BlockPos startPos = this.getFirstPos(chunkPos);

        if (startPos == null) {
            if (attempt < MAX_CHUNK_ATTEMPTS)
                return this.updateSamplingChunk(attempt + 1);
            else
                throw new NullPointerException("Starting Y position in dimension is null!");
        }

        this.chunkAccess = this.dimension.getChunk(chunkPos.x, chunkPos.z);
        this.startY = startPos.getY();

        return true;
    }

    private boolean updateSamplingChunk() {
        return this.updateSamplingChunk(0);
    }

    private boolean isMineable(BlockState blockToMineState, Block blockToMine) {
        if (blockToMineState.isEmpty() || blockToMine.defaultDestroyTime() <= 0) {
            // Skip air and unbreakable blocks
            return false;
        }

        // Skip liquids
        return !(blockToMine instanceof LiquidBlock) && !(blockToMine instanceof BubbleColumnBlock);
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

        public void incrementState(BlockState state) {
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
