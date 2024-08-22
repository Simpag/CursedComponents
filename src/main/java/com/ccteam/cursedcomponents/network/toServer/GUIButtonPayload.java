package com.ccteam.cursedcomponents.network.toServer;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public record GUIButtonPayload(ButtonType buttonType, BlockPos pos, boolean state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GUIButtonPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "gui_button_payload"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GUIButtonPayload> STREAM_CODEC = StreamCodec.composite(
            ButtonType.STREAM_CODEC,
            GUIButtonPayload::buttonType,
            BlockPos.STREAM_CODEC,
            GUIButtonPayload::pos,
            ByteBufCodecs.BOOL,
            GUIButtonPayload::state,
            GUIButtonPayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum ButtonType {
        dimensionalQuarryRunningState;

        public static final IntFunction<ButtonType> BY_ID = ByIdMap.continuous(ButtonType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, ButtonType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ButtonType::ordinal);
    }
}
