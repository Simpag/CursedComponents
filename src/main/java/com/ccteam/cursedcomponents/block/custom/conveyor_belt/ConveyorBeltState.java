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
        BlockPos blockpos = this.pos.north();
        BlockPos blockpos1 = this.pos.south();
        BlockPos blockpos2 = this.pos.west();
        BlockPos blockpos3 = this.pos.east();
        boolean flag = this.hasConnection(blockpos);
        boolean flag1 = this.hasConnection(blockpos1);
        boolean flag2 = this.hasConnection(blockpos2);
        boolean flag3 = this.hasConnection(blockpos3);
        RailShape railshape = null;
        if (flag || flag1) {
            railshape = RailShape.NORTH_SOUTH;
        }

        if (flag2 || flag3) {
            railshape = RailShape.EAST_WEST;
        }

        railshape = getRailShape(blockpos, blockpos1, blockpos2, blockpos3, railshape);

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

        BlockPos blockpos = this.pos.north();
        BlockPos blockpos1 = this.pos.south();
        BlockPos blockpos2 = this.pos.west();
        BlockPos blockpos3 = this.pos.east();

        railshape = getRailShape(blockpos, blockpos1, blockpos2, blockpos3, railshape);

        this.updateConnections(railshape);
        this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
        if (alwaysPlace || this.level.getBlockState(this.pos) != this.state) {
            this.level.setBlock(this.pos, this.state, 3);

            for (int i = 0; i < this.connections.size(); i++) {
                ConveyorBeltState ConveyorBeltState = this.getConveyorBelt(this.connections.get(i));
                if (ConveyorBeltState != null) {
                    ConveyorBeltState.removeSoftConnections();
                    if (ConveyorBeltState.canConnectTo(this)) {
                        ConveyorBeltState.connectTo(this);
                    }
                }
            }
        }

        return this;
    }

    private RailShape getRailShape(BlockPos blockpos, BlockPos blockpos1, BlockPos blockpos2, BlockPos blockpos3, RailShape railshape) {
        if (railshape == RailShape.NORTH_SOUTH && canMakeSlopes) {
            if (ConveyorBeltBlock.isConveyorBelt(this.level, blockpos.above())) {
                railshape = RailShape.ASCENDING_NORTH;
            }

            if (ConveyorBeltBlock.isConveyorBelt(this.level, blockpos1.above())) {
                railshape = RailShape.ASCENDING_SOUTH;
            }
        }

        if (railshape == RailShape.EAST_WEST && canMakeSlopes) {
            if (ConveyorBeltBlock.isConveyorBelt(this.level, blockpos3.above())) {
                railshape = RailShape.ASCENDING_EAST;
            }

            if (ConveyorBeltBlock.isConveyorBelt(this.level, blockpos2.above())) {
                railshape = RailShape.ASCENDING_WEST;
            }
        }
        return railshape;
    }

    public BlockState getState() {
        return this.state;
    }
}
