package com.ccteam.cursedcomponents.block;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.custom.*;
import com.ccteam.cursedcomponents.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CursedComponentsMod.MOD_ID);

    public static final DeferredBlock<Block> LUCKY_BLOCK = registerBlock("lucky_block",
            () -> new LuckyBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(8f)
                    .lightLevel((s) -> 15)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> CONVEYOR_BELT_TIER1 = registerBlock("conveyor_belt_tier1",
            () -> new ConveyorBeltBlock(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .noCollission(), 0.1));

    public static final DeferredBlock<Block> CONVEYOR_BELT_TIER2 = registerBlock("conveyor_belt_tier2",
            () -> new ConveyorBeltBlock(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .noCollission(), 0.3));


    public static final DeferredBlock<Block> DIMENSIONAL_QUARRY = registerBlock(
            "dimensional_quarry",
            () -> new DimensionalQuarryBlock(
                    BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.IGNORE)
            ),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static final DeferredBlock<Block> MINI_CHUNK_OVERWORLD = registerBlock(
            "mini_chunk_overworld",
            () -> new MiniChunkBlock(BlockBehaviour.Properties.of().strength(2f).noOcclusion(), MiniChunkBlock.MiniChunkType.overworld),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static final DeferredBlock<Block> MINI_CHUNK_NETHER = registerBlock(
            "mini_chunk_nether",
            () -> new MiniChunkBlock(BlockBehaviour.Properties.of().strength(2f).noOcclusion(), MiniChunkBlock.MiniChunkType.nether),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static final DeferredBlock<Block> MINI_CHUNK_END = registerBlock(
            "mini_chunk_end",
            () -> new MiniChunkBlock(BlockBehaviour.Properties.of().strength(2f).noOcclusion(), MiniChunkBlock.MiniChunkType.end),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static final DeferredBlock<Block> AUTO_SHEARER = registerBlock(
            "auto_shearer",
            () -> new AutoShearerBlock(BlockBehaviour.Properties.of().strength(4f).noOcclusion().noCollission())
    );

    public static final DeferredBlock<Block> SPIKE = registerBlock(
            "spike",
            () -> new SpikeBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().noOcclusion())
    );

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
