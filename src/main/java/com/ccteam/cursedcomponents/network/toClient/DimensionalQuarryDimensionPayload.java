package com.ccteam.cursedcomponents.network.toClient;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record DimensionalQuarryDimensionPayload(Item item, BlockPos pos) implements CustomPacketPayload {

    public static final Type<DimensionalQuarryDimensionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "dimensional_quarry_dimension_payload"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionalQuarryDimensionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM),
            DimensionalQuarryDimensionPayload::item,
            BlockPos.STREAM_CODEC,
            DimensionalQuarryDimensionPayload::pos,
            DimensionalQuarryDimensionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
