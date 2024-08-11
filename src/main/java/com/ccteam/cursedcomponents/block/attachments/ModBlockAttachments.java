package com.ccteam.cursedcomponents.block.attachments;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModBlockAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CursedComponentsMod.MOD_ID);

    // Serialization via INBTSerializable
    public static final Supplier<AttachmentType<ItemStackHandler>> DIMENSIONAL_QUARRY_INV = ATTACHMENT_TYPES.register(
            "dimensional_quarry_inv", () -> AttachmentType.serializable(
                    () -> new ItemStackHandler(DimensionalQuarryEntity.INVENTORY_SIZE)).build()
    );
    public static final Supplier<AttachmentType<EnergyStorage>> DIMENSIONAL_QUARRY_ENERGY = ATTACHMENT_TYPES.register(
            "dimensional_quarry_energy", () -> AttachmentType.serializable(
                    () -> new EnergyStorage(DimensionalQuarryEntity.ENERGY_CAPACITY, DimensionalQuarryEntity.ENERGY_RECEIVE, DimensionalQuarryEntity.ENERGY_RECEIVE, 0)
            ).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
