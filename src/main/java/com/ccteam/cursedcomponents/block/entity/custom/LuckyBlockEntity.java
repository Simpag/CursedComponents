package com.ccteam.cursedcomponents.block.entity.custom;

import com.ccteam.cursedcomponents.block.custom.LuckyBlock;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LuckyBlockEntity extends BlockEntity {

    private static final float diceRotationSpeed = 40f;
    private static final int MAX_SPIN_TICKS = 20;

    private float diceRotation;
    private boolean isSpinning;
    private int spinTicks;
    private Player activatingPlayer;

    public LuckyBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LUCKY_BLOCK_BE.get(), pos, blockState);
        this.isSpinning = false;
        this.diceRotation = 0;
        this.spinTicks = 0;
    }

    public void startSpinning(Player activatingPlayer) {
        this.isSpinning = true;
        this.spinTicks = MAX_SPIN_TICKS;
        this.activatingPlayer = activatingPlayer;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LuckyBlockEntity entity) {
        if (level.isClientSide) {
            if (entity.isSpinning) {
                entity.diceRotation = (entity.diceRotation + diceRotationSpeed) % 360;
            }
        } else {
            entity.spinTicks--;
            if (entity.isSpinning && entity.spinTicks <= 0) {
                entity.isSpinning = false;
                BlockState luckyBlock = level.getBlockState(pos);
                if (luckyBlock.getBlock() instanceof LuckyBlock) {
                    luckyBlock.getBlock().playerDestroy(level, entity.activatingPlayer, pos, luckyBlock, null, entity.activatingPlayer.getMainHandItem());
                    level.destroyBlock(pos, false, entity.activatingPlayer);
                }
            }
        }
    }

    public float getDiceRotation() {
        return diceRotation;
    }
}

