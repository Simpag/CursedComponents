package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.network.toServer.GUIButtonPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void handleGUIButtonPayload(final GUIButtonPayload data, final IPayloadContext context) {
        switch (data.buttonType()) {
            case GUIButtonPayload.ButtonType.dimensionalQuarryRunningState:
                handleDimensionalQuarryPayload(data, context);
                break;
            // Can add another button for another block here
            default:
                break;
        }
    }

    private static void handleDimensionalQuarryPayload(final GUIButtonPayload data, final IPayloadContext context) {
        try {
            Level level = context.player().level();
            BlockPos pos = data.pos();
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof DimensionalQuarryEntity quarryEntity) {
                quarryEntity.setRunning(data.state());
            }
        } catch (Exception e) {
            LogUtils.getLogger().debug(e.toString());
        }
    }
}
