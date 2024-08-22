package com.ccteam.cursedcomponents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ModRegistries {
    public static class Dimension {
        // Mining dimension sampling dimensions
        public static final ResourceKey<Level> OVERWORLD_SAMPLE_DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "overworld_sampling_dimension"));
        public static final ResourceKey<Level> NETHER_SAMPLE_DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "nether_sampling_dimension"));
        public static final ResourceKey<Level> END_SAMPLE_DIMENSION_KEY = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "end_sampling_dimension"));
    }
}
