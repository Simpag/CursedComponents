package com.ccteam.cursedcomponents.datagen;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CursedComponentsMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        /*this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.BLACK_OPAL_BLOCK.get());
                
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.RAW_BLACK_OPAL_BLOCK.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.BLACK_OPAL_BLOCK.get());

        this.tag(BlockTags.FENCES).add(ModBlocks.BLACK_OPAL_FENCE.get());
        this.tag(BlockTags.FENCE_GATES).add(ModBlocks.BLACK_OPAL_FENCE_GATE.get());
        this.tag(BlockTags.WALLS).add(ModBlocks.BLACK_OPAL_WALL.get());*/

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DIMENSIONAL_QUARRY.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.DIMENSIONAL_QUARRY.get());
    }
}
