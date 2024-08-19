package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LuckyBlockEntity extends BlockEntity {

    private static final float diceRotationSpeed = 0.5f;

    private float diceRotation;

    public LuckyBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LUCKY_BLOCK_BE.get(), pos, blockState);
    }

    public float getDiceRotation() {
        diceRotation = (diceRotation + diceRotationSpeed) % 360;
        return diceRotation;
    }
}
