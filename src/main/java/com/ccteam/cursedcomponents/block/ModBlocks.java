package com.ccteam.cursedcomponents.block;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.custom.DimensionTester;
import com.ccteam.cursedcomponents.block.custom.DimensionalQuarryBlock;
import com.ccteam.cursedcomponents.block.custom.MiniChunkBlock;
import com.ccteam.cursedcomponents.item.ModItems;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CursedComponentsMod.MOD_ID);

    public static final DeferredBlock<Block> DIMENSIONAL_QUARRY = registerBlock(
            "dimensional_quarry",
            () -> new DimensionalQuarryBlock(
                    BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.IGNORE)
            ),
            new Item.Properties().rarity(Rarity.RARE)
    );

    public static final DeferredBlock<Block> MINI_CHUNK_OVERWORLD = registerBlock(
            "mini_chunk_overworld",
            () -> new MiniChunkBlock(BlockBehaviour.Properties.of().strength(2f).noOcclusion(), MiniChunkBlock.MiniChunkType.overworld),
            new Item.Properties().rarity(Rarity.RARE)
    );

    public static final DeferredBlock<Block> DimTester = registerBlock("dim_tester", () -> new DimensionTester(BlockBehaviour.Properties.of().strength(2f)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, new Item.Properties());

        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, Item.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, props);

        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, Item.Properties props) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
