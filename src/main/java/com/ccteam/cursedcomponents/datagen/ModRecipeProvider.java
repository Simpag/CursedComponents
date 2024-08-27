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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MINI_CHUNK_NETHER.get())
                .pattern("NGN")
                .pattern("NWN")
                .pattern("MMM")
                .define('G', Items.GOLD_NUGGET)
                .define('N', Items.NETHERRACK)
                .define('W', ModItems.WARDEN_INFUSION.get())
                .define('M', Items.MAGMA_BLOCK)
                .unlockedBy("has_warden_infusion", has(ModItems.WARDEN_INFUSION.get())
                ).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MINI_CHUNK_END.get())
                .pattern("EPE")
                .pattern("EWE")
                .pattern("EEE")
                .define('P', Items.ENDER_PEARL)
                .define('E', Items.END_STONE)
                .define('W', ModItems.WARDEN_INFUSION.get())
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WARDEN_INFUSED_DIAMOND.get(), 1)
                .requires(ModItems.WARDEN_INFUSION.get(), 1)
                .requires(Items.DIAMOND, 1)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.AUTO_SHEARER.get(), 1)
                .pattern("III")
                .pattern("SDS")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('D', Items.DIAMOND)
                .define('S', Items.SHEARS)
                .unlockedBy("has_slime_ball", has(Items.SHEARS)
                ).save(recipeOutput, "auto_shearer");

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.CONVEYOR_BELT_TIER1.get(), 8)
                .pattern("SIS")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.SLIME_BALL)
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL)
                ).save(recipeOutput, "conveyor_belt_tier1_variant1");

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.CONVEYOR_BELT_TIER1.get(), 8)
                .pattern("SCS")
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .define('S', Items.SLIME_BALL)
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL)
                ).save(recipeOutput, "conveyor_belt_tier1_variant2");

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.CONVEYOR_BELT_TIER2.get(), 8)
                .pattern("GGG")
                .pattern("GBG")
                .pattern("GGG")
                .define('B', ModBlocks.CONVEYOR_BELT_TIER1.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_conveyor_belt_tier1", has(ModBlocks.CONVEYOR_BELT_TIER1.get())
                ).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.SPIKE.get())
                .pattern(" S ")
                .pattern(" I ")
                .pattern(" R ")
                .define('S', Items.IRON_SWORD)
                .define('I', Blocks.IRON_BARS)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_redstone", has(Items.REDSTONE)
                ).save(recipeOutput);
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
