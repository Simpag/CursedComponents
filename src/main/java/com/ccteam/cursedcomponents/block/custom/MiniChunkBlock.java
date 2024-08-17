package com.ccteam.cursedcomponents.block.custom;

import com.ccteam.cursedcomponents.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MiniChunkBlock extends Block {
    public static final VoxelShape OVERWORLD_SHAPE = VoxelShapeUtils.combineShapes(
            box(5, 0, 5, 11, 6, 11),
            box(6, 6, 7, 9, 10, 10)
    );
    public final MiniChunkType chunkType;

    public MiniChunkBlock(Properties properties, MiniChunkType chunkType) {
        super(properties);

        this.chunkType = chunkType;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (chunkType) {
            case overworld:
                return OVERWORLD_SHAPE;
            default:
                return super.getShape(state, level, pos, context);
        }
    }

    public enum MiniChunkType {
        overworld,
        nether,
        end
    }
}
