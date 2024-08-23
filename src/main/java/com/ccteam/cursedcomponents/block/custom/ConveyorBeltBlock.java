package com.ccteam.cursedcomponents.block.custom;

import com.ccteam.cursedcomponents.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConveyorBeltBlock extends Block {
    protected static final VoxelShape FLAT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;
    private final double speedMultiplier;

    private final boolean isStraight;

    public ConveyorBeltBlock(Properties properties, double speedMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
        this.isStraight = true;
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH));
    }

    public static boolean isConveyorBelt(BlockState state) {
        return state.is(ModTags.Blocks.CONVEYOR_BELT);
    }

    public static boolean isConveyorBelt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModTags.Blocks.CONVEYOR_BELT);
    }


    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.isShiftKeyDown())
            return;

        if (entity instanceof ItemEntity)
            ((ItemEntity) entity).setExtendedLifetime();

        RailShape shape = state.getValue(SHAPE);
        Vec3 dir = switch (shape) {
            case NORTH_SOUTH -> new Vec3(0, 0, 1);
            case EAST_WEST -> new Vec3(1, 0, 0);
            case ASCENDING_NORTH -> new Vec3(0, 1, -1);
            case ASCENDING_SOUTH -> new Vec3(0, 1, 1);
            case ASCENDING_EAST -> new Vec3(1, 1, 0);
            case ASCENDING_WEST -> new Vec3(-1, 1, 0);
            case NORTH_EAST -> new Vec3(1, 0, -1);
            case NORTH_WEST -> new Vec3(-1, 0, -1);
            case SOUTH_EAST -> new Vec3(1, 0, 1);
            case SOUTH_WEST -> new Vec3(-1, 0, 1);
        };
        Vec3 vel = dir.multiply(speedMultiplier, speedMultiplier, speedMultiplier);
        entity.push(vel);
    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    public boolean isValidConveyorBeltShape(RailShape shape) {
        return true;
    }

    public RailShape getConveyorBeltShape(BlockState state) {
        return state.getValue(SHAPE);
    }

    public boolean isStraight() {
        return isStraight;
    }

}
