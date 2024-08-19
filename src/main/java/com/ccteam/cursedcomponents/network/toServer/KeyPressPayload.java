package com.ccteam.cursedcomponents.network.toServer;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record KeyPressPayload(int keyCode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KeyPressPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "key_press_payload"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, KeyPressPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KeyPressPayload::keyCode,
            KeyPressPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
