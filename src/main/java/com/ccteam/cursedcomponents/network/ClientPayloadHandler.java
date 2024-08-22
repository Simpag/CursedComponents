package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.block.entity.custom.LuckyBlockEntity;
import com.ccteam.cursedcomponents.network.toClient.DimensionalQuarryMinMaxYLevelPayload;
import com.ccteam.cursedcomponents.network.toClient.DimensionalQuarryYLevelPayload;
import com.ccteam.cursedcomponents.network.toClient.LuckyBlockInteractionPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleLuckyBlockInteractionPayload(final LuckyBlockInteractionPayload data, final IPayloadContext context) {
        BlockEntity entity = context.player().level().getBlockEntity(data.pos());
        if (entity instanceof LuckyBlockEntity luckyBlockEntity) {
            luckyBlockEntity.setRollOutcome(data.rollOutcome());
        }
    }

    public static void handleDimensionalQuarryYLevelPayload(final DimensionalQuarryYLevelPayload data, final IPayloadContext context) {
        BlockEntity entity = context.player().level().getBlockEntity(data.pos());
        if (entity instanceof DimensionalQuarryEntity quarryEntity) {
            quarryEntity.setCurrentYLevel(data.currentYLevel());
        }
    }

    public static void handleDimensionalQuarryMaxYLevelPayload(final DimensionalQuarryMinMaxYLevelPayload data, final IPayloadContext context) {
        BlockEntity entity = context.player().level().getBlockEntity(data.pos());
        if (entity instanceof DimensionalQuarryEntity quarryEntity) {
            quarryEntity.setMinMaxCurrentYLevel(data.currentMinYLevel(), data.currentMaxYLevel());
        }
    }
}
