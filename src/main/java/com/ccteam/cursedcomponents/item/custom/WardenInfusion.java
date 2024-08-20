package com.ccteam.cursedcomponents.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class WardenInfusion extends Item {
    public WardenInfusion(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.warden_infusion.1"));
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.warden_infusion.2"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.cursedcomponents.warden_infusion.shift"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
