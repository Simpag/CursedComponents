package com.ccteam.cursedcomponents.event;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.command.ChargeItemCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@EventBusSubscriber(modid = CursedComponentsMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEventBusEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new ChargeItemCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }
}
