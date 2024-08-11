package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModCreativeModeTabs {
    // Add your items that should appear in the creative tab
    private static final List<DeferredItem<Item>> MOD_ITEMS = new ArrayList<>() {{
        // add(ModItems.ITEM1);
    }};

    // Add your blocks that should appear in the creative tab
    private static final List<DeferredBlock<Block>> MOD_BLOCKS = new ArrayList<>() {{
        add(ModBlocks.DIMENSIONAL_QUARRY);
    }};

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CursedComponentsMod.MOD_ID);

    public static final Supplier<CreativeModeTab> CURSED_COMPONENTS_ITEMS_TAB = CREATIVE_MODE_TABS.register("cursed_components_item_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cursedcomponents.cursed_components_item_tab"))
            .icon(() -> new ItemStack(Items.STONE))
            .displayItems((parameters, output) -> {
                for (var item : MOD_ITEMS) {
                    output.accept(item);
                }

                for (var block : MOD_BLOCKS) {
                    output.accept(block);
                }
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
