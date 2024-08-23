package com.ccteam.cursedcomponents.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

public class ConveyorBeltBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final double speedMultiplier;

    public ConveyorBeltBlock(Properties properties, double speedMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.isShiftKeyDown())
            return;

        if (entity instanceof ItemEntity)
            ((ItemEntity) entity).setExtendedLifetime();

        Direction direction = state.getValue(FACING);
        Vec3 vel = new Vec3(direction.step());
        vel = vel.multiply(speedMultiplier, speedMultiplier, speedMultiplier);
        entity.push(vel);
    }
}
