package com.ccteam.cursedcomponents.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class GuiUtil {
    public static void drawScaledString(Font font, GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean dropShadow, float scale) {
        drawScaledString(font, guiGraphics, text, x, y, color, dropShadow, scale, false);
    }

    public static void drawScaledString(Font font, GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean dropShadow, float scale, boolean centered) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, scale);

        if (centered)
            drawCenteredString(guiGraphics, font, text, 0, 0, color, dropShadow);
        else
            guiGraphics.drawString(font, text, 0, 0, color, dropShadow);

        pose.popPose();
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color, boolean dropShadow) {
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        guiGraphics.drawString(font, text, x - font.width(formattedcharsequence) / 2, y, color, dropShadow);
    }
}
