package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.entity.custom.LuckyParrot;
import com.ccteam.cursedcomponents.network.toServer.KeyPressPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    public static void handleDismountKeyPressPayload(final KeyPressPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            LuckyParrot.dismountFromShoulder(context.player());
        });
    }
}
