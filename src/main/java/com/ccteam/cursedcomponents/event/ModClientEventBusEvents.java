package com.ccteam.cursedcomponents.event;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.entity.ModEntities;
import com.ccteam.cursedcomponents.entity.client.LuckyParrotRenderer;
import com.ccteam.cursedcomponents.keybinds.ModKeyBinds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventBusEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.LUCKY_PARROT.get(), LuckyParrotRenderer::new);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        ModKeyBinds.register(event);
    }
}
