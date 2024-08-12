package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.network.toServer.GUIButtonPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void handleGUIButtonPayload(final GUIButtonPayload data, final IPayloadContext context) {
        // Do something with the data, on the network thread

        switch (data.buttonType()) {
            case GUIButtonPayload.ButtonType.dimensionalQuarryStartToggle:
                handleDimensionalQuarryPayload(data, context);
                break;
            default:
                break;
        }


        // Do something with the data, on the main thread
        /*context.enqueueWork(() -> {
                    blah(data.age());
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("my_mod.networking.failed", e.getMessage()));
                    return null;
                });*/
    }

    private static void handleDimensionalQuarryPayload(final GUIButtonPayload data, final IPayloadContext context) {
        Level level = context.player().level();
        BlockPos pos = data.pos();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof DimensionalQuarryEntity quarryEntity) {
            quarryEntity.setRunning(data.state());
        }
    }
}
