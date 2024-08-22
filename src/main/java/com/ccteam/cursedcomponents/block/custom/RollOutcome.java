package com.ccteam.cursedcomponents.block.custom;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FastColor;

import java.util.function.IntFunction;

public enum RollOutcome {
    VERY_UNLUCKY,
    UNLUCKY,
    NORMAL,
    LUCKY;

    public int getColor() {
        return switch (this) {
            case VERY_UNLUCKY -> FastColor.ARGB32.color(255, 0x000000); // Black color
            case UNLUCKY -> FastColor.ARGB32.color(255, 0xFF0000); // Red color
            case NORMAL -> FastColor.ARGB32.color(0, 0xFFFFFF); // transparent, so no tint
            case LUCKY -> FastColor.ARGB32.color(255, 0x00FF00); // Green color
        };
    }

    public static final IntFunction<RollOutcome> BY_ID = ByIdMap.continuous(RollOutcome::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, RollOutcome> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RollOutcome::ordinal);
}
