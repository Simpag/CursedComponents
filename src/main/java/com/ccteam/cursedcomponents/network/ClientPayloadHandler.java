package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.network.toClient.DimensionalQuarryDimensionPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class ClientPayloadHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleDimensionalQuarryDimensionPayload(final DimensionalQuarryDimensionPayload data, final IPayloadContext context) {
        Level level = context.player().level();

        if (!level.isClientSide)
            return;

        BlockPos pos = data.pos();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof DimensionalQuarryEntity quarryEntity) {
            quarryEntity.setMiniChunkSlot(new ItemStack(data.item()));
        }
    }
}
