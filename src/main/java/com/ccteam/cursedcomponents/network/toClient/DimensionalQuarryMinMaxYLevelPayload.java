package com.ccteam.cursedcomponents.network.toClient;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;

public record DimensionalQuarryMinMaxYLevelPayload(Integer currentMinYLevel, Integer currentMaxYLevel,
                                                   BlockPos pos) implements CustomPacketPayload {
    public static final Type<DimensionalQuarryMinMaxYLevelPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "dimensional_quarry_min_max_y_level_payload"));

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
    public static final StreamCodec<ByteBuf, DimensionalQuarryMinMaxYLevelPayload> STREAM_CODEC = StreamCodec.composite(
            NULLABLE_INT, DimensionalQuarryMinMaxYLevelPayload::currentMinYLevel,
            NULLABLE_INT, DimensionalQuarryMinMaxYLevelPayload::currentMaxYLevel,
            BlockPos.STREAM_CODEC, DimensionalQuarryMinMaxYLevelPayload::pos,
            DimensionalQuarryMinMaxYLevelPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}