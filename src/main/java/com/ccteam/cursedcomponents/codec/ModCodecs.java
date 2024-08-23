package com.ccteam.cursedcomponents.codec;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.glm.WardenGlobalLootModifier;
import com.ccteam.cursedcomponents.item.data_component.custom.ItemFilterData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModCodecs {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CursedComponentsMod.MOD_ID);

    public static final Supplier<MapCodec<WardenGlobalLootModifier>> WARDEN_GLOBAL_LOOT_MODIFIER =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("warden_global_loot_modifier", () -> WardenGlobalLootModifier.CODEC);

    // Data Components
    public static final Codec<ItemFilterData> ITEM_FILTER_DATA_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("inventoryTag").forGetter(ItemFilterData::inventoryTag)
            ).apply(instance, ItemFilterData::new)
    );
    public static final StreamCodec<ByteBuf, ItemFilterData> ITEM_FILTER_DATA_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, ItemFilterData::inventoryTag,
            ItemFilterData::new
    );

    public static void register(IEventBus eventBus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
