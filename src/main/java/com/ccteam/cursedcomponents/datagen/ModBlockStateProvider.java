package com.ccteam.cursedcomponents.datagen;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CursedComponentsMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // blockWithItem(ModBlocks.ITEM_1);

        // Custom stairs
        // stairsBlock((StairBlock) ModBlocks.BLACK_OPAL_STAIRS.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));
        // blockItem(ModBlocks.BLACK_OPAL_STAIRS);

        // Custom slabs
        // slabBlock((SlabBlock) ModBlocks.BLACK_OPAL_SLAB.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));
        // blockItem(ModBlocks.BLACK_OPAL_SLAB);

        // Custom pressure plates
        // pressurePlateBlock((PressurePlateBlock) ModBlocks.BLACK_OPAL_PRESSURE_PLATE.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));
        // blockItem(ModBlocks.BLACK_OPAL_PRESSURE_PLATE);

        // Custom buttons (block item in ModItemModelProvider)
        // buttonBlock((ButtonBlock) ModBlocks.BLACK_OPAL_BUTTON.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));

        // Fences, walls and fence gates (fences and walls are in ModItemModelProvider)
        // fenceBlock((FenceBlock) ModBlocks.BLACK_OPAL_FENCE.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));
        // fenceGateBlock((FenceGateBlock) ModBlocks.BLACK_OPAL_FENCE_GATE.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));
        // blockItem(ModBlocks.BLACK_OPAL_FENCE_GATE);
        // wallBlock((WallBlock) ModBlocks.BLACK_OPAL_WALL.get(), blockTexture(ModBlocks.BLACK_OPAL_BLOCK.get()));
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        // Simple helper function to register simple blocks
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("cursedcomponents:block/" + deferredBlock.getId().getPath()));
    }
}
