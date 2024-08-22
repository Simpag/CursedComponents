package com.ccteam.cursedcomponents.block.entity;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.block.entity.custom.LuckyBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CursedComponentsMod.MOD_ID);

    public static final Supplier<BlockEntityType<LuckyBlockEntity>> LUCKY_BLOCK_BE =
            BLOCK_ENTITIES.register("lucky_block_be", () -> BlockEntityType.Builder.of(
                    LuckyBlockEntity::new, ModBlocks.LUCKY_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<DimensionalQuarryEntity>> DIMENSIONAL_QUARRY_BE =
            BLOCK_ENTITIES.register("dimensional_quarry_be", () -> BlockEntityType.Builder.of(
                    DimensionalQuarryEntity::new, ModBlocks.DIMENSIONAL_QUARRY.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
