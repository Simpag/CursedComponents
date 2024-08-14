package com.ccteam.cursedcomponents.entity.client;


import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;


@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public final class UpgradeRenderManager {

    private static final Map<PlayerRenderer, Object> injected = new WeakHashMap<PlayerRenderer, Object>();

    private UpgradeRenderManager() {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        final PlayerRenderer renderer = event.getRenderer();
        if (renderer != null && !injected.containsKey(renderer)) {
            LogUtils.getLogger().info("Add lucky parrot layer");
            UpgradeRenderManager.addLuckyParrotShoulderLayer(renderer);
            injected.put(renderer, null);
        }
    }

    private static void addLuckyParrotShoulderLayer(@Nonnull PlayerRenderer playerRenderer) {
        try {
            EntityRenderDispatcher entityRenderDispatcher = ObfuscationReflectionHelper
                    .getPrivateValue(EntityRenderer.class, playerRenderer, "entityRenderDispatcher");
            EntityModelSet entityModelSet = ObfuscationReflectionHelper
                    .getPrivateValue(EntityRenderDispatcher.class, entityRenderDispatcher, "entityModels");

            playerRenderer.addLayer(new LuckyParrotOnShoulderLayer<>(playerRenderer, entityModelSet));
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LogUtils.getLogger().warn("Unable to access PlayerRenderer.layers, reason: {}", String.valueOf(e));
        }
    }

}
