package com.ccteam.cursedcomponents.network.to_client;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.custom.RollOutcome;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LuckyBlockInteractionPayload(RollOutcome rollOutcome, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LuckyBlockInteractionPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "lucky_block_interaction_payload"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, LuckyBlockInteractionPayload> STREAM_CODEC = StreamCodec.composite(
            RollOutcome.STREAM_CODEC, LuckyBlockInteractionPayload::rollOutcome,
            BlockPos.STREAM_CODEC, LuckyBlockInteractionPayload::pos,
            LuckyBlockInteractionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}