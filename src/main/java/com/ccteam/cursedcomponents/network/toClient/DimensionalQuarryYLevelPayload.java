package com.ccteam.cursedcomponents.network.toClient;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.custom.RollOutcome;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;

public record DimensionalQuarryYLevelPayload(Integer currentYLevel, BlockPos pos) implements CustomPacketPayload {
    public static final Type<DimensionalQuarryYLevelPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "dimensional_quarry_y_level_payload"));

    private static final StreamCodec<ByteBuf, Integer> NULLABLE_INT = new StreamCodec<ByteBuf, Integer>() {
        public Integer decode(ByteBuf byteBuf) {
            int i = byteBuf.readInt();
            if (i == Integer.MAX_VALUE)
                return null;

            return i;
        }

        public void encode(ByteBuf byteBuf, @Nullable Integer i) {
            byteBuf.writeInt(Objects.requireNonNullElse(i, Integer.MAX_VALUE));
        }
    };

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, DimensionalQuarryYLevelPayload> STREAM_CODEC = StreamCodec.composite(
            NULLABLE_INT, DimensionalQuarryYLevelPayload::currentYLevel,
            BlockPos.STREAM_CODEC, DimensionalQuarryYLevelPayload::pos,
            DimensionalQuarryYLevelPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}