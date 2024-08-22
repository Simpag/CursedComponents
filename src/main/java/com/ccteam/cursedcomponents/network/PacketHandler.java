package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.network.toClient.DimensionalQuarryMinMaxYLevelPayload;
import com.ccteam.cursedcomponents.network.toClient.DimensionalQuarryYLevelPayload;
import com.ccteam.cursedcomponents.network.toClient.LuckyBlockInteractionPayload;
import com.ccteam.cursedcomponents.network.toServer.GUIButtonPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    public static final String VERSION = "1";

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(VERSION);
        // Client
        registrar.playToClient(
                LuckyBlockInteractionPayload.TYPE,
                LuckyBlockInteractionPayload.STREAM_CODEC,
                ClientPayloadHandler::handleLuckyBlockInteractionPayload
        );
        registrar.playToClient(
                DimensionalQuarryYLevelPayload.TYPE,
                DimensionalQuarryYLevelPayload.STREAM_CODEC,
                ClientPayloadHandler::handleDimensionalQuarryYLevelPayload
        );
        registrar.playToClient(
                DimensionalQuarryMinMaxYLevelPayload.TYPE,
                DimensionalQuarryMinMaxYLevelPayload.STREAM_CODEC,
                ClientPayloadHandler::handleDimensionalQuarryMaxYLevelPayload
        );

        // Server
        registrar.playToServer(
                GUIButtonPayload.TYPE,
                GUIButtonPayload.STREAM_CODEC,
                ServerPayloadHandler::handleGUIButtonPayload
        );
    }
}
