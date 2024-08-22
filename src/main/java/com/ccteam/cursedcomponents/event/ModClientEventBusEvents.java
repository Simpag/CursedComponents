package com.ccteam.cursedcomponents.event;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.additional.ModAdditionalModels;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.block.entity.renderer.LuckyBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventBusEvents {

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.LUCKY_BLOCK_BE.get(), LuckyBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        ModAdditionalModels.register(event);
    }

}
