package com.ccteam.cursedcomponents.item.data_component;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(CursedComponentsMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEM_INVENTORY = REGISTRAR.registerComponentType(
            "item_inventory",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(ItemContainerContents.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ITEM_ENERGY = REGISTRAR.registerComponentType(
            "item_energy",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}
