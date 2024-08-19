package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.CursedComponentsMod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CursedComponentsMod.MOD_ID);

    // public static final DeferredItem<Item> ITEM_1 = ITEMS.registerSimpleItem("item_1");

    public static final DeferredItem<Item> ICON = ITEMS.registerSimpleItem("icon");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
