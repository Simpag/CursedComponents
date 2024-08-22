package com.ccteam.cursedcomponents;

import com.ccteam.cursedcomponents.block.ModBlocks;
import com.ccteam.cursedcomponents.block.capabilities.ModBlockCapabilities;
import com.ccteam.cursedcomponents.block.entity.ModBlockEntities;
import com.ccteam.cursedcomponents.codecs.ModCodecs;
import com.ccteam.cursedcomponents.datacomponents.ModDataComponents;
import com.ccteam.cursedcomponents.gui.containers.ModContainers;
import com.ccteam.cursedcomponents.gui.screens.ModScreens;
import com.ccteam.cursedcomponents.item.ModCreativeModeTabs;
import com.ccteam.cursedcomponents.item.ModItems;
import com.ccteam.cursedcomponents.network.PacketHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CursedComponentsMod.MOD_ID)
public class CursedComponentsMod {
    public static final String MOD_ID = "cursedcomponents";

    public CursedComponentsMod(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        // Register creative mode tabs
        ModCreativeModeTabs.register(modEventBus);

        // Register mod items
        ModItems.register(modEventBus);

        // Register mod blocks
        ModBlocks.register(modEventBus);

        // Register mod block entities
        ModBlockEntities.register(modEventBus);

        // Register the block capabilities
        modEventBus.addListener(ModBlockCapabilities::registerCapabilities);

        // Register Container Menus
        ModContainers.register(modEventBus);

        // Register Screens
        modEventBus.addListener(ModScreens::registerScreens);

        // Register packet handler for networking
        modEventBus.addListener(PacketHandler::register);

        // Register custom CODECS
        ModCodecs.register(modEventBus);

        // Register custom data components
        ModDataComponents.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
    }
}
