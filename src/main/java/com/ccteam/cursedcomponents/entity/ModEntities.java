package com.ccteam.cursedcomponents.entity;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CursedComponentsMod.MOD_ID);

    public static final Supplier<EntityType<LuckyParrot>> LUCKY_PARROT =
            ENTITY_TYPES.register("lucky_parrot", () -> EntityType.Builder.of(LuckyParrot::new, MobCategory.CREATURE).sized(0.5f, 0.9f).build("lucky_parrot"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}