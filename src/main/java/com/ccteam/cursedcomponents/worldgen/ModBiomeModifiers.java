package com.ccteam.cursedcomponents.worldgen;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
//    public static final ResourceKey<BiomeModifier> ADD_TREE_EBONY = registerKey("add_tree_ebony");
//
//    public static final ResourceKey<BiomeModifier> ADD_BLACK_OPAL_ORE = registerKey("add_black_opal_ore");

    public static final ResourceKey<BiomeModifier> ADD_LUCKY_BLOCK = registerKey("add_lucky_block");


    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

//        context.register(ADD_TREE_EBONY, new BiomeModifiers.AddFeaturesBiomeModifier(
//            HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
//            HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.EBONY_PLACED_KEY)),
//            GenerationStep.Decoration.VEGETAL_DECORATION));
//
//
//        context.register(ADD_BLACK_OPAL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
//            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
//            HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BLACK_OPAL_ORE_PLACED_KEY)),
//            GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_LUCKY_BLOCK, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LUCKY_BLOCK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }


    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, name));
    }
}
