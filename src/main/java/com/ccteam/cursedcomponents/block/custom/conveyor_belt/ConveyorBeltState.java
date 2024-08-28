package com.ccteam.cursedcomponents.block.custom.conveyor_belt;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import javax.annotation.Nullable;
import java.util.List;

public class ConveyorBeltState {
    private final Level level;
    private final BlockPos pos;
    private final ConveyorBeltBlock block;
    private BlockState state;
    private final List<BlockPos> connections = Lists.newArrayList();
    private final boolean canMakeSlopes;

    public ConveyorBeltState(Level level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.block = (ConveyorBeltBlock)state.getBlock();
        RailShape railshape = this.block.getConveyorBeltShape(state);
        this.canMakeSlopes = true;
        this.updateConnections(railshape);
    }

    private void updateConnections(RailShape shape) {
        this.connections.clear();
        switch (shape) {
            case NORTH_SOUTH:
                this.connections.add(this.pos.north());
                this.connections.add(this.pos.south());
                break;
            case EAST_WEST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.east());
                break;
            case ASCENDING_EAST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.east().above());
                break;
            case ASCENDING_WEST:
                this.connections.add(this.pos.west().above());
                this.connections.add(this.pos.east());
                break;
            case ASCENDING_NORTH:
                this.connections.add(this.pos.north().above());
                this.connections.add(this.pos.south());
                break;
            case ASCENDING_SOUTH:
                this.connections.add(this.pos.north());
                this.connections.add(this.pos.south().above());
                break;
        }
    }

    private void removeSoftConnections() {
        for (int i = 0; i < this.connections.size(); i++) {
            ConveyorBeltState beltState = this.getConveyorBelt(this.connections.get(i));
            if (beltState != null && beltState.connectsTo(this)) {
                this.connections.set(i, beltState.pos);
            } else {
                this.connections.remove(i--);
            }
        }
    }

    private boolean hasConveyorBelt(BlockPos pos) {
        return ConveyorBeltBlock.isConveyorBelt(this.level, pos)
                || ConveyorBeltBlock.isConveyorBelt(this.level, pos.above())
                || ConveyorBeltBlock.isConveyorBelt(this.level, pos.below());
    }

    @Nullable
    private ConveyorBeltState getConveyorBelt(BlockPos pos) {
        BlockState blockstate = this.level.getBlockState(pos);
        if (ConveyorBeltBlock.isConveyorBelt(blockstate)) {
            return new ConveyorBeltState(this.level, pos, blockstate);
        } else {
            BlockPos $$1 = pos.above();
            blockstate = this.level.getBlockState($$1);
            if (ConveyorBeltBlock.isConveyorBelt(blockstate)) {
                return new ConveyorBeltState(this.level, $$1, blockstate);
            } else {
                $$1 = pos.below();
                blockstate = this.level.getBlockState($$1);
                return ConveyorBeltBlock.isConveyorBelt(blockstate) ? new ConveyorBeltState(this.level, $$1, blockstate) : null;
            }
        }
    }

    private boolean connectsTo(ConveyorBeltState state) {
        return this.hasConnection(state.pos);
    }

    private boolean hasConnection(BlockPos pos) {
        for (int i = 0; i < this.connections.size(); i++) {
            BlockPos blockpos = this.connections.get(i);
            if (blockpos.getX() == pos.getX() && blockpos.getZ() == pos.getZ()) {
                return true;
            }
        }
        return false;
    }

    protected int countPotentialConnections() {
        int i = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.hasConveyorBelt(this.pos.relative(direction))) {
                i++;
            }
        }
        return i;
    }

    private boolean canConnectTo(ConveyorBeltState state) {
        return this.connectsTo(state) || this.connections.size() != 2;
    }

    private void connectTo(ConveyorBeltState state) {
        this.connections.add(state.pos);
        BlockPos north = this.pos.north();
        BlockPos south = this.pos.south();
        BlockPos west = this.pos.west();
        BlockPos east = this.pos.east();

        RailShape railshape = null;
        if (this.hasConnection(north) || this.hasConnection(south)) {
            railshape = RailShape.NORTH_SOUTH;
        }

        if (this.hasConnection(west) || this.hasConnection(east)) {
            railshape = RailShape.EAST_WEST;
        }

        railshape = getRailShape(north, south, west, east, railshape);

        if (railshape == null) {
            railshape = RailShape.NORTH_SOUTH;
        }

        if (!this.block.isValidConveyorBeltShape()) { // Forge: allow rail block to decide if the new shape is valid
            this.connections.remove(state.pos);
            return;
        }
        this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
        this.level.setBlock(this.pos, this.state, 3);
    }

    public ConveyorBeltState place(boolean alwaysPlace, RailShape shape) {
        RailShape railshape = shape;

        BlockPos north = this.pos.north();
        BlockPos south = this.pos.south();
        BlockPos west = this.pos.west();
        BlockPos east = this.pos.east();

        railshape = getRailShape(north, south, west, east, railshape);

        this.updateConnections(railshape);
        this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
        if (alwaysPlace || this.level.getBlockState(this.pos) != this.state) {
            this.level.setBlock(this.pos, this.state, 3);

            for (int i = 0; i < this.connections.size(); i++) {
                ConveyorBeltState conveyorBeltState = this.getConveyorBelt(this.connections.get(i));
                if (conveyorBeltState != null) {
                    conveyorBeltState.removeSoftConnections();
                    if (conveyorBeltState.canConnectTo(this)) {
                        conveyorBeltState.connectTo(this);
                    }
                }
            }
        }

        return this;
    }

    private RailShape getRailShape(BlockPos north, BlockPos south, BlockPos west, BlockPos east, RailShape railshape) {
        if (railshape == RailShape.NORTH_SOUTH && canMakeSlopes) {
            if (ConveyorBeltBlock.isConveyorBelt(this.level, north.above())) {
                railshape = RailShape.ASCENDING_NORTH;
            }

            if (ConveyorBeltBlock.isConveyorBelt(this.level, south.above())) {
                railshape = RailShape.ASCENDING_SOUTH;
            }
        }

        if (railshape == RailShape.EAST_WEST && canMakeSlopes) {
            if (ConveyorBeltBlock.isConveyorBelt(this.level, east.above())) {
                railshape = RailShape.ASCENDING_EAST;
            }

            if (ConveyorBeltBlock.isConveyorBelt(this.level, west.above())) {
                railshape = RailShape.ASCENDING_WEST;
            }
        }
        return railshape;
    }

    public BlockState getState() {
        return this.state;
    }
}
