package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AutoShearerEntity extends BlockEntity {
    public AutoShearerEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AUTO_SHEARER_BE.get(), pos, blockState);
    }
}
