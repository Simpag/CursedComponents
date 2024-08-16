package com.ccteam.cursedcomponents.entity.attachments;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModEntityAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES
            = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CursedComponentsMod.MOD_ID);

    public static final Supplier<AttachmentType<String>> entityPlayerAttachment
            = ATTACHMENT_TYPES.register(
                    "entity_player_attachment",
                    () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
