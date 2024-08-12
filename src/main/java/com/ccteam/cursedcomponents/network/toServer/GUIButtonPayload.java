package com.ccteam.cursedcomponents.network.toServer;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public record GUIButtonPayload(ButtonType buttonType, BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GUIButtonPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "gui_button_payload"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GUIButtonPayload> STREAM_CODEC = StreamCodec.composite(
            ButtonType.STREAM_CODEC,
            GUIButtonPayload::buttonType,
            BlockPos.STREAM_CODEC,
            GUIButtonPayload::pos,
            GUIButtonPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum ButtonType {
        dimensionalQuarryStartToggle;

        public static final IntFunction<ButtonType> BY_ID = ByIdMap.continuous(ButtonType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, ButtonType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ButtonType::ordinal);
    }
}
