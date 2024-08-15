package com.ccteam.cursedcomponents.datagen;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.item.ModItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Build recipies

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MINI_CHUNK_OVERWORLD.get())
                .pattern("GGG")
                .pattern("DWD")
                .pattern("SSS")
                .define('G', Items.GRASS_BLOCK)
                .define('D', Items.DIRT)
                .define('W', ModItems.WARDEN_INFUSION.get())
                .define('S', Items.STONE)
                .unlockedBy("has_warden_infusion", has(ModItems.WARDEN_INFUSION.get())
                ).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIMENSIONAL_CORE.get())
                .pattern("WEW")
                .pattern("GNG")
                .pattern("WTW")
                .define('W', ModItems.WARDEN_INFUSION.get())
                .define('E', Items.ENDER_EYE)
                .define('G', Items.GOLD_INGOT)
                .define('T', Items.GHAST_TEAR)
                .define('N', Items.NETHER_STAR)
                .unlockedBy("has_warden_infusion", has(ModItems.WARDEN_INFUSION.get())
                ).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WARDEN_INFUSED_DIAMOND.get())
                .pattern(" W ")
                .pattern("WDW")
                .pattern(" W ")
                .define('W', ModItems.WARDEN_INFUSION.get())
                .define('D', Items.DIAMOND)
                .unlockedBy("has_warden_infusion", has(ModItems.WARDEN_INFUSION.get())
                ).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DIMENSIONAL_QUARRY.get())
                .pattern("NEN")
                .pattern("DCD")
                .pattern("NON")
                .define('E', Items.ENDER_CHEST)
                .define('C', ModItems.DIMENSIONAL_CORE.get())
                .define('N', Items.NETHERITE_INGOT)
                .define('O', Items.OBSIDIAN)
                .define('D', ModItems.WARDEN_INFUSED_DIAMOND.get())
                .unlockedBy("has_dimensional_core", has(ModItems.DIMENSIONAL_CORE.get())
                ).save(recipeOutput);

        /* ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLACK_OPAL_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BLACK_OPAL.get())
                .unlockedBy("has_black_opal", has(ModItems.BLACK_OPAL.get())
                ).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLACK_OPAL.get(), 9)
                .requires(ModBlocks.BLACK_OPAL_BLOCK.get())
                .unlockedBy("has_black_opal_block", has(ModBlocks.BLACK_OPAL_BLOCK.get())
                ).save(recipeOutput);

        List<ItemLike> BLACK_OPAL_SMELTABLES = List.of(ModItems.RAW_BLACK_OPAL,
                ModBlocks.BLACK_OPAL_ORE); // Can add multiple variants of the ore here too...
        oreSmelting(recipeOutput, BLACK_OPAL_SMELTABLES, RecipeCategory.MISC, ModItems.BLACK_OPAL.get(), 0.25f, 200, "black_opal");
        oreBlasting(recipeOutput, BLACK_OPAL_SMELTABLES, RecipeCategory.MISC, ModItems.BLACK_OPAL.get(), 0.25f, 100, "black_opal");

        stairBuilder(ModBlocks.BLACK_OPAL_STAIRS.get(), Ingredient.of(ModItems.BLACK_OPAL.get()))
                .group("black_opal")
                .unlockedBy("has_black_opal", has(ModItems.BLACK_OPAL.get())
                ).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_OPAL_SLAB.get(), ModItems.BLACK_OPAL.get());

        pressurePlate(recipeOutput, ModBlocks.BLACK_OPAL_PRESSURE_PLATE.get(), ModItems.BLACK_OPAL.get());
        buttonBuilder(ModBlocks.BLACK_OPAL_BUTTON.get(), Ingredient.of(ModItems.BLACK_OPAL.get()))
                .group("black_opal")
                .unlockedBy("has_black_opal", has(ModItems.BLACK_OPAL.get())
                ).save(recipeOutput);

        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_OPAL_WALL.get(), ModItems.BLACK_OPAL.get());
        fenceBuilder(ModBlocks.BLACK_OPAL_FENCE.get(), Ingredient.of(ModItems.BLACK_OPAL.get()))
                .group("black_opal")
                .unlockedBy("has_black_opal", has(ModItems.BLACK_OPAL.get())
                ).save(recipeOutput);
        fenceGateBuilder(ModBlocks.BLACK_OPAL_FENCE_GATE.get(), Ingredient.of(ModItems.BLACK_OPAL.get()))
                .group("black_opal")
                .unlockedBy("has_black_opal", has(ModItems.BLACK_OPAL.get())
                ).save(recipeOutput); */
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                                      float experience, int cookingTIme, String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result,
                experience, cookingTIme, group, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                                      float pExperience, int pCookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result,
                pExperience, pCookingTime, group, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeName) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, cookingSerializer, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, CursedComponentsMod.MOD_ID + ":" + getItemName(result) + recipeName + "_" + getItemName(itemlike));
        }
    }
}
