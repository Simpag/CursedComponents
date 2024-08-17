package com.ccteam.cursedcomponents.block.custom;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DimensionTester extends Block {
    private static final Logger LOGGER = LogUtils.getLogger();

    public DimensionTester(Properties properties) {
        super(properties);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            ResourceKey<Level> resourcekey = level.dimension() == CursedComponentsMod.OVERWORLD_SAMPLE_DIMENSION_KEY ? Level.OVERWORLD : CursedComponentsMod.OVERWORLD_SAMPLE_DIMENSION_KEY;
            ServerLevel serverLevel = ((ServerLevel) level).getServer().getLevel(resourcekey);
            if (serverLevel == null) {
                LOGGER.debug("Server level is null!");
                return InteractionResult.sidedSuccess(false);
            }

            if (!player.canChangeDimensions(level, serverLevel)) {
                LOGGER.debug("Player can't change dimensions!");
                return InteractionResult.sidedSuccess(false);
            }

            LOGGER.debug("Changing dims!");
            player.changeDimension(new DimensionTransition(
                    serverLevel,
                    player.position(),
                    Vec3.ZERO,
                    player.getXRot(),
                    player.getYRot(),
                    DimensionTransition.DO_NOTHING
            ));
        }


        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
