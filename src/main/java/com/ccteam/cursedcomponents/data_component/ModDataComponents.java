package com.ccteam.cursedcomponents.data_component;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.codec.ModCodecs;
import com.ccteam.cursedcomponents.data_component.custom.ItemFilterData;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(CursedComponentsMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemFilterData>> ITEM_FILTER_DATA = REGISTRAR.registerComponentType(
            "item_filter_data",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(ModCodecs.ITEM_FILTER_DATA_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(ModCodecs.ITEM_FILTER_DATA_STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}