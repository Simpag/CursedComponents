package com.ccteam.cursedcomponents.block.custom;

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
    private final boolean isStraight;
    private final List<BlockPos> connections = Lists.newArrayList();
    private final boolean canMakeSlopes;

    public ConveyorBeltState(Level level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.block = (ConveyorBeltBlock)state.getBlock();
        RailShape railshape = this.block.getConveyorBeltShape(state);
        this.isStraight = !this.block.isStraight();
        this.canMakeSlopes = true;
        this.updateConnections(railshape);
    }

    public List<BlockPos> getConnections() {
        return this.connections;
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
            case SOUTH_EAST:
                this.connections.add(this.pos.east());
                this.connections.add(this.pos.south());
                break;
            case SOUTH_WEST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.south());
                break;
            case NORTH_WEST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.north());
                break;
            case NORTH_EAST:
                this.connections.add(this.pos.east());
                this.connections.add(this.pos.north());
        }
    }

    private void removeSoftConnections() {
        for (int i = 0; i < this.connections.size(); i++) {
            ConveyorBeltState beltState = this.getRail(this.connections.get(i));
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
    private ConveyorBeltState getRail(BlockPos pos) {
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

        if (!this.isStraight) {
            if (flag1 && flag3 && !flag && !flag2) {
                railshape = RailShape.SOUTH_EAST;
            }

            if (flag1 && flag2 && !flag && !flag3) {
                railshape = RailShape.SOUTH_WEST;
            }

            if (flag && flag2 && !flag1 && !flag3) {
                railshape = RailShape.NORTH_WEST;
            }

            if (flag && flag3 && !flag1 && !flag2) {
                railshape = RailShape.NORTH_EAST;
            }
        }

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

        if (railshape == null) {
            railshape = RailShape.NORTH_SOUTH;
        }

        if (!this.block.isValidConveyorBeltShape(railshape)) { // Forge: allow rail block to decide if the new shape is valid
            this.connections.remove(state.pos);
            return;
        }
        this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
        this.level.setBlock(this.pos, this.state, 3);
    }

    private boolean hasNeighborRail(BlockPos pos) {
        ConveyorBeltState ConveyorBeltState = this.getRail(pos);
        if (ConveyorBeltState == null) {
            return false;
        } else {
            ConveyorBeltState.removeSoftConnections();
            return ConveyorBeltState.canConnectTo(this);
        }
    }

    public ConveyorBeltState place(boolean powered, boolean alwaysPlace, RailShape shape) {
        BlockPos blockpos = this.pos.north();
        BlockPos blockpos1 = this.pos.south();
        BlockPos blockpos2 = this.pos.west();
        BlockPos blockpos3 = this.pos.east();
        boolean flag = this.hasNeighborRail(blockpos);
        boolean flag1 = this.hasNeighborRail(blockpos1);
        boolean flag2 = this.hasNeighborRail(blockpos2);
        boolean flag3 = this.hasNeighborRail(blockpos3);
        RailShape railshape = null;
        boolean flag4 = flag || flag1;
        boolean flag5 = flag2 || flag3;
        if (flag4 && !flag5) {
            railshape = RailShape.NORTH_SOUTH;
        }

        if (flag5 && !flag4) {
            railshape = RailShape.EAST_WEST;
        }

        boolean flag6 = flag1 && flag3;
        boolean flag7 = flag1 && flag2;
        boolean flag8 = flag && flag3;
        boolean flag9 = flag && flag2;
        if (!this.isStraight) {
            if (flag6 && !flag && !flag2) {
                railshape = RailShape.SOUTH_EAST;
            }

            if (flag7 && !flag && !flag3) {
                railshape = RailShape.SOUTH_WEST;
            }

            if (flag9 && !flag1 && !flag3) {
                railshape = RailShape.NORTH_WEST;
            }

            if (flag8 && !flag1 && !flag2) {
                railshape = RailShape.NORTH_EAST;
            }
        }

        if (railshape == null) {
            if (flag4 && flag5) {
                railshape = shape;
            } else if (flag4) {
                railshape = RailShape.NORTH_SOUTH;
            } else if (flag5) {
                railshape = RailShape.EAST_WEST;
            }

            if (!this.isStraight) {
                if (powered) {
                    if (flag6) {
                        railshape = RailShape.SOUTH_EAST;
                    }

                    if (flag7) {
                        railshape = RailShape.SOUTH_WEST;
                    }

                    if (flag8) {
                        railshape = RailShape.NORTH_EAST;
                    }

                    if (flag9) {
                        railshape = RailShape.NORTH_WEST;
                    }
                } else {
                    if (flag9) {
                        railshape = RailShape.NORTH_WEST;
                    }

                    if (flag8) {
                        railshape = RailShape.NORTH_EAST;
                    }

                    if (flag7) {
                        railshape = RailShape.SOUTH_WEST;
                    }

                    if (flag6) {
                        railshape = RailShape.SOUTH_EAST;
                    }
                }
            }
        }

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

        if (railshape == null || !this.block.isValidConveyorBeltShape(railshape)) { // Forge: allow rail block to decide if the new shape is valid
            railshape = shape;
        }

        this.updateConnections(railshape);
        this.state = this.state.setValue(this.block.getShapeProperty(), railshape);
        if (alwaysPlace || this.level.getBlockState(this.pos) != this.state) {
            this.level.setBlock(this.pos, this.state, 3);

            for (int i = 0; i < this.connections.size(); i++) {
                ConveyorBeltState ConveyorBeltState = this.getRail(this.connections.get(i));
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

    public BlockState getState() {
        return this.state;
    }
}
