package com.ccteam.cursedcomponents.datagen;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    // Add new items here to generate their loot tables...
    @Override
    protected void generate() {
        // These are just nice shorthand functions
        // dropSelf(ModBlocks.ITEM_1.get());
        dropSelf(ModBlocks.LUCKY_BLOCK.get());

        // This is how you create a custom drop
        /* this.add(
                ModBlocks.BLACK_OPAL_ORE.get(),
                block -> createMultipleOreDrops(
                        ModBlocks.BLACK_OPAL_ORE.get(),
                        ModItems.RAW_BLACK_OPAL.get(),
                        2,
                        5
                )
        ); */

        // Slabs could drop 1 or 2 items
        // this.add(ModBlocks.BLACK_OPAL_SLAB.get(), block -> createSlabItemTable(ModBlocks.BLACK_OPAL_SLAB.get()));
    }

    protected LootTable.Builder createMultipleOreDrops(Block block, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                block,
                this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                                .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
