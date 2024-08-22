package com.ccteam.cursedcomponents.structure;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.structure.custom.LuckyStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, CursedComponentsMod.MOD_ID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resource location of cursedcomponents:lucky_structure
     */
    public static final DeferredHolder<StructureType<?>, StructureType<LuckyStructure>> LUCKY_STRUCTURE =
            DEFERRED_REGISTRY_STRUCTURE.register("lucky_structure", () -> () -> LuckyStructure.CODEC);

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
    }
}
