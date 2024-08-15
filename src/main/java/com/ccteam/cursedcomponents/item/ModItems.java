package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.CursedComponentsMod;

import com.ccteam.cursedcomponents.item.custom.Warden_Glue;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CursedComponentsMod.MOD_ID);

    public static final DeferredItem<Item> WARDEN_GLUE = ITEMS.registerItem(
            "warden_glue",
            Warden_Glue::new,
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final DeferredItem<Item> DIMENSIONAL_CORE = ITEMS.registerSimpleItem(
            "dimensional_core",
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static final DeferredItem<SwordItem> DEBUG_STICK = ITEMS.register("debug_stick",
            () -> new SwordItem(Tiers.NETHERITE, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 123456789, -1f))));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
