package com.ccteam.cursedcomponents.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ChargeItemCommand {
    public ChargeItemCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chargeItem").requires((s) -> s.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(this::executeCharge));
        dispatcher.register(Commands.literal("dischargeItem").requires((s) -> s.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(this::executeDischarge));
    }

    private IEnergyStorage getStorage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        assert player != null;
        IEnergyStorage storage = player.getItemInHand(InteractionHand.MAIN_HAND).getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null) {
            source.sendFailure(Component.literal("Could not charge item in hand!"));
            return null;
        }

        return storage;
    }

    private int executeCharge(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        IEnergyStorage storage = getStorage(context);

        if (storage == null)
            return -1;

        int totalCharged = 0;
        int charged = -1;
        while (charged != 0) {
            charged = storage.receiveEnergy(Integer.MAX_VALUE, false);
            totalCharged += charged;
        }
        int finalTotalCharged = totalCharged; // Stupid formatting
        source.sendSuccess(() -> Component.literal("Charged item with %d FE!".formatted(finalTotalCharged)), true);

        return Command.SINGLE_SUCCESS;
    }

    private int executeDischarge(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        IEnergyStorage storage = getStorage(context);

        if (storage == null)
            return -1;

        int totalDischarged = 0;
        int discharged = -1;
        while (discharged != 0) {
            discharged = storage.extractEnergy(Integer.MAX_VALUE, false);
            totalDischarged += discharged;
        }
        int finalTotalDischarged = totalDischarged; //  Again I really dislike formatting
        source.sendSuccess(() -> Component.literal("Discharged item with %d FE!".formatted(finalTotalDischarged)), true);

        return Command.SINGLE_SUCCESS;
    }
}
