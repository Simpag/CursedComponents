package com.ccteam.cursedcomponents.datagen;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CursedComponentsMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DIMENSIONAL_QUARRY.get())
                .add(ModBlocks.SPIKE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.DIMENSIONAL_QUARRY.get())
                .add(ModBlocks.SPIKE.get());

        this.tag(ModTags.Blocks.CONVEYOR_BELT)
                .add(ModBlocks.CONVEYOR_BELT_TIER1.get())
                .add(ModBlocks.CONVEYOR_BELT_TIER2.get());
    }
}
