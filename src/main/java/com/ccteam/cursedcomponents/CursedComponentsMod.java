package com.ccteam.cursedcomponents;

import com.ccteam.cursedcomponents.block.ModBlockCapabilities;
import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.glm.ModGlobalLootModifierCodecs;
import com.ccteam.cursedcomponents.gui.container.ModContainers;
import com.ccteam.cursedcomponents.gui.screen.ModScreens;
import com.ccteam.cursedcomponents.item.ModCreativeModeTabs;
import com.ccteam.cursedcomponents.item.ModItemCapabilities;
import com.ccteam.cursedcomponents.item.ModItems;
import com.ccteam.cursedcomponents.item.data_component.ModDataComponents;
import com.ccteam.cursedcomponents.network.PacketHandler;
import com.ccteam.cursedcomponents.structure.ModStructures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CursedComponentsMod.MOD_ID)
public class CursedComponentsMod {
    public static final String MOD_ID = "cursedcomponents";

    public CursedComponentsMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register creative mode tabs
        ModCreativeModeTabs.register(modEventBus);

        // Register mod items
        ModItems.register(modEventBus);

        // Register mod blocks
        ModBlocks.register(modEventBus);

        // Register mod structures
        ModStructures.register(modEventBus);

        // Register mod block entities
        ModBlockEntities.register(modEventBus);

        // Register block capabilities
        modEventBus.addListener(ModBlockCapabilities::registerBlockCapabilities);

        // Register Container Menus
        ModContainers.register(modEventBus);

        // Register Screens
        modEventBus.addListener(ModScreens::registerScreens);

        // Register packet handler for networking
        modEventBus.addListener(PacketHandler::register);

        // Register custom CODECS
        ModGlobalLootModifierCodecs.register(modEventBus);

        // Register custom data components
        ModDataComponents.register(modEventBus);

        // Register item capabilities
        modEventBus.addListener(ModItemCapabilities::registerItemCapabilities);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC, MOD_ID + ".toml");
    }
}
