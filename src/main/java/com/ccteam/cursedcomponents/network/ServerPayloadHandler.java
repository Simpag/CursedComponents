package com.ccteam.cursedcomponents.network;

import com.ccteam.cursedcomponents.network.toServer.KeyPressPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.InvocationTargetException;

public class ServerPayloadHandler {

    public static void handleDismountKeyPressPayload(final KeyPressPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                ObfuscationReflectionHelper.findMethod(Player.class, "removeEntitiesOnShoulder").invoke(context.player());
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
