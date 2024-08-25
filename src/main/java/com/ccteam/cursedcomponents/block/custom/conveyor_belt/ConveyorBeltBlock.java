package com.ccteam.cursedcomponents.block.custom.conveyor_belt;

import com.ccteam.cursedcomponents.util.ModTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConveyorBeltBlock extends Block {

    protected static final VoxelShape FLAT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;
    public static final BooleanProperty REVERSED = BooleanProperty.create("reversed");

    private final double speedMultiplier;
    private final boolean isStraight;

    public ConveyorBeltBlock(Properties properties, double speedMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
        this.isStraight = true;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SHAPE, RailShape.NORTH_SOUTH));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.isShiftKeyDown())
            return;

        if (entity instanceof ItemEntity)
            ((ItemEntity) entity).setExtendedLifetime();

        RailShape shape = state.getValue(SHAPE);
        Vec3 dir = switch (shape) {
            case NORTH_SOUTH -> new Vec3(0, 0, -1);
            case EAST_WEST -> new Vec3(1, 0, 0);
            case ASCENDING_NORTH -> new Vec3(0, 1, -1);
            case ASCENDING_SOUTH -> new Vec3(0, -1, -1);
            case ASCENDING_EAST -> new Vec3(1, 1, 0);
            case ASCENDING_WEST -> new Vec3(1, -1, 0);
            case NORTH_EAST -> new Vec3(1, 0, -1);
            case NORTH_WEST -> new Vec3(-1, 0, -1);
            case SOUTH_EAST -> new Vec3(1, 0, 1);
            case SOUTH_WEST -> new Vec3(-1, 0, 1);
        };
        if (state.getValue(REVERSED)) {
            dir = dir.multiply(-1, -1, -1);
        }
        Vec3 vel = dir.multiply(speedMultiplier, speedMultiplier, speedMultiplier);
        entity.push(vel);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide && level.getBlockState(pos).is(this)) {
            RailShape shape = getConveyorBeltDirection(state);
            if (shouldBeRemoved(pos, level, shape)) {
                dropResources(state, level, pos);
                level.removeBlock(pos, isMoving);
            } else {
                this.updateState(state, level, pos, block);
            }
        }
    }

    protected void updateState(BlockState state, Level level, BlockPos pos, Block block) {
        if (block.defaultBlockState().isSignalSource() && new ConveyorBeltState(level, pos, state).countPotentialConnections() == 3) {
            this.updateDir(level, pos, state, false);
        }
    }

    protected BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean alwaysPlace) {
        if (level.isClientSide) {
            return state;
        } else {
            RailShape railshape = state.getValue(this.getShapeProperty());
            return new ConveyorBeltState(level, pos, state).place(level.hasNeighborSignal(pos), alwaysPlace, railshape).getState();
        }
    }

    private static boolean shouldBeRemoved(BlockPos pos, Level level, RailShape shape) {
        if (!canSupportRigidBlock(level, pos.below())) {
            return true;
        } else {
            switch (shape) {
                case ASCENDING_EAST:
                    return !canSupportRigidBlock(level, pos.east());
                case ASCENDING_WEST:
                    return !canSupportRigidBlock(level, pos.west());
                case ASCENDING_NORTH:
                    return !canSupportRigidBlock(level, pos.north());
                case ASCENDING_SOUTH:
                    return !canSupportRigidBlock(level, pos.south());
                default:
                    return false;
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, REVERSED);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, level, pos, movedByPiston);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving) {
            super.onRemove(state, level, pos, newState, isMoving);
            RailShape shape = getConveyorBeltDirection(state);
            if (shape.isAscending()) {
                level.updateNeighborsAt(pos.above(), this);
            }

            if (this.isStraight) {
                level.updateNeighborsAt(pos, this);
                level.updateNeighborsAt(pos.below(), this);
            }
        }
    }

    protected BlockState updateState(BlockState state, Level level, BlockPos pos, boolean movedByPiston) {
        state = this.updateDir(level, pos, state, true);
        if (this.isStraight) {
            level.neighborChanged(state, pos, this, pos, movedByPiston);
        }
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        RailShape railshape = state.getValue(SHAPE);

        return state.setValue(SHAPE, switch (rot) {
            case CLOCKWISE_180 -> {
                switch (railshape) {
                    case NORTH_SOUTH:
                        yield RailShape.NORTH_SOUTH;
                    case EAST_WEST:
                        yield RailShape.EAST_WEST;
                    case ASCENDING_EAST:
                        yield RailShape.ASCENDING_WEST;
                    case ASCENDING_WEST:
                        yield RailShape.ASCENDING_EAST;
                    case ASCENDING_NORTH:
                        yield RailShape.ASCENDING_SOUTH;
                    case ASCENDING_SOUTH:
                        yield RailShape.ASCENDING_NORTH;
                    case SOUTH_EAST:
                        yield RailShape.NORTH_WEST;
                    case SOUTH_WEST:
                        yield RailShape.NORTH_EAST;
                    case NORTH_WEST:
                        yield RailShape.SOUTH_EAST;
                    case NORTH_EAST:
                        yield RailShape.SOUTH_WEST;
                    default:
                        throw new MatchException(null, null);
                }
            }
            case COUNTERCLOCKWISE_90 -> {
                switch (railshape) {
                    case NORTH_SOUTH:
                        yield RailShape.EAST_WEST;
                    case EAST_WEST:
                        yield RailShape.NORTH_SOUTH;
                    case ASCENDING_EAST:
                        yield RailShape.ASCENDING_NORTH;
                    case ASCENDING_WEST:
                        yield RailShape.ASCENDING_SOUTH;
                    case ASCENDING_NORTH:
                        yield RailShape.ASCENDING_WEST;
                    case ASCENDING_SOUTH:
                        yield RailShape.ASCENDING_EAST;
                    case SOUTH_EAST:
                        yield RailShape.NORTH_EAST;
                    case SOUTH_WEST:
                        yield RailShape.SOUTH_EAST;
                    case NORTH_WEST:
                        yield RailShape.SOUTH_WEST;
                    case NORTH_EAST:
                        yield RailShape.NORTH_WEST;
                    default:
                        throw new MatchException(null, null);
                }
            }
            case CLOCKWISE_90 -> {
                switch (railshape) {
                    case NORTH_SOUTH:
                        yield RailShape.EAST_WEST;
                    case EAST_WEST:
                        yield RailShape.NORTH_SOUTH;
                    case ASCENDING_EAST:
                        yield RailShape.ASCENDING_SOUTH;
                    case ASCENDING_WEST:
                        yield RailShape.ASCENDING_NORTH;
                    case ASCENDING_NORTH:
                        yield RailShape.ASCENDING_EAST;
                    case ASCENDING_SOUTH:
                        yield RailShape.ASCENDING_WEST;
                    case SOUTH_EAST:
                        yield RailShape.SOUTH_WEST;
                    case SOUTH_WEST:
                        yield RailShape.NORTH_WEST;
                    case NORTH_WEST:
                        yield RailShape.NORTH_EAST;
                    case NORTH_EAST:
                        yield RailShape.SOUTH_EAST;
                    default:
                        throw new MatchException(null, null);
                }
            }
            default -> railshape;
        });
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        RailShape railshape = state.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                switch (railshape) {
                    case ASCENDING_NORTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirror);
                }
            case FRONT_BACK:
                switch (railshape) {
                    case ASCENDING_EAST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_SOUTH: //Forge fix: MC-196102
                    case EAST_WEST:
                        return state;
                }
        }

        return super.mirror(state, mirror);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        boolean isReversed = (direction == Direction.NORTH || direction == Direction.EAST);

        BlockState blockstate = super.defaultBlockState();
        boolean isEastWest = direction == Direction.EAST || direction == Direction.WEST;
        return blockstate.setValue(SHAPE, isEastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)
                .setValue(REVERSED, isReversed);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        RailShape railshape = state.is(this) ? state.getValue(this.getShapeProperty()) : null;
        return railshape != null && railshape.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
    }

    public RailShape getConveyorBeltDirection(BlockState state) {
        return state.getValue(getShapeProperty());
    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    public RailShape getConveyorBeltShape(BlockState state) {
        return state.getValue(SHAPE);
    }

    public static boolean isConveyorBelt(BlockState state) {
        return state.is(ModTags.Blocks.CONVEYOR_BELT);
    }

    public static boolean isConveyorBelt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModTags.Blocks.CONVEYOR_BELT);
    }

    public boolean isValidConveyorBeltShape() {
        return true;
    }

    public boolean isStraight() {
        return this.isStraight;
    }

}
