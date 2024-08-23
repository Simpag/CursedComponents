package com.ccteam.cursedcomponents.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WardenInfusion extends Item {
    public WardenInfusion(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.warden_infusion.1"));
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.warden_infusion.2"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.warden_infusion.shift"));
        }
    }
}
