package com.ccteam.cursedcomponents.structures;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.structures.custom.LuckyStructure;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, CursedComponentsMod.MOD_ID);
    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resource location of cursedcomponents:lucky_structure.
     */
    public static final DeferredHolder<StructureType<?>, StructureType<LuckyStructure>> LUCKY_STRUCTURE = DEFERRED_REGISTRY_STRUCTURE.register("lucky_structure", () -> explicitStructureTypeTyping(LuckyStructure.CODEC));

    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(IEventBus eventBus) {
        DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
    }
}
