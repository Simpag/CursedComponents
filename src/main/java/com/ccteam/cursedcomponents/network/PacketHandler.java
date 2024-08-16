package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.network.toServer.KeyPressPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    public static final String VERSION = "1";

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(
                KeyPressPayload.TYPE,
                KeyPressPayload.STREAM_CODEC,
                ServerPayloadHandler::handleDismountKeyPressPayload
        );
    }
}
