package com.ccteam.cursedcomponents.datagen;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CursedComponentsMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.WARDEN_INFUSION.get());
        basicItem(ModItems.DIMENSIONAL_CORE.get());
        basicItem(ModItems.WARDEN_INFUSED_DIAMOND.get());

        // buttonItem(ModBlocks.BLACK_OPAL_BUTTON, ModBlocks.BLACK_OPAL_BLOCK);
        // fenceItem(ModBlocks.BLACK_OPAL_FENCE, ModBlocks.BLACK_OPAL_BLOCK);
        // wallItem(ModBlocks.BLACK_OPAL_WALL, ModBlocks.BLACK_OPAL_BLOCK);
    }

    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }
}
