package com.ccteam.cursedcomponents.item;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        add(ModItems.WARDEN_INFUSION);
        add(ModItems.DIMENSIONAL_CORE);
        add(ModItems.WARDEN_INFUSED_DIAMOND);
        add(ModItems.ITEM_FILTER);
    }};

    // Add your blocks that should appear in the creative tab
    private static final List<DeferredBlock<Block>> MOD_BLOCKS = new ArrayList<>() {{
        add(ModBlocks.LUCKY_BLOCK);

        add(ModBlocks.DIMENSIONAL_QUARRY);
        add(ModBlocks.MINI_CHUNK_OVERWORLD);
        add(ModBlocks.MINI_CHUNK_NETHER);
        add(ModBlocks.MINI_CHUNK_END);

        add(ModBlocks.CONVEYOR_BELT);
    }};

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CursedComponentsMod.MOD_ID);

    public static final Supplier<CreativeModeTab> CURSED_COMPONENTS_ITEMS_TAB = CREATIVE_MODE_TABS.register("cursed_components_item_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cursedcomponents.cursed_components_item_tab"))
            .icon(() -> new ItemStack(ModItems.ICON.get()))
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
