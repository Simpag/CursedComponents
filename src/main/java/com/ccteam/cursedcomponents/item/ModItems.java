package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.item.custom.DimensionalQuarryItemFilter;
import com.ccteam.cursedcomponents.item.custom.SpongeOnStick;
import com.ccteam.cursedcomponents.item.custom.WardenInfusion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CursedComponentsMod.MOD_ID);

    public static final DeferredItem<Item> WARDEN_INFUSION = ITEMS.registerItem(
            "warden_infusion",
            WardenInfusion::new,
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final DeferredItem<Item> DIMENSIONAL_CORE = ITEMS.registerSimpleItem(
            "dimensional_core",
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final DeferredItem<Item> WARDEN_INFUSED_DIAMOND = ITEMS.registerSimpleItem(
            "warden_infused_diamond",
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final DeferredItem<Item> DIMENSIONAL_QUARRY_ITEM_FILTER = ITEMS.registerItem(
            "dimensional_quarry_item_filter",
            DimensionalQuarryItemFilter::new,
            new Item.Properties()
    );
    public static final DeferredItem<Item> SPONGE_ON_STICK = ITEMS.registerItem(
            "sponge_on_stick",
            SpongeOnStick::new,
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredItem<Item> ICON = ITEMS.registerSimpleItem("icon");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
