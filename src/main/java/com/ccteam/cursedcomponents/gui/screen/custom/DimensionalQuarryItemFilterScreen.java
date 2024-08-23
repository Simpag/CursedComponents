package com.ccteam.cursedcomponents.gui.screen.custom;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.gui.container.custom.DimensionalQuarryItemFilterContainer;
import com.ccteam.cursedcomponents.util.GuiUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class DimensionalQuarryItemFilterScreen extends AbstractContainerScreen<DimensionalQuarryItemFilterContainer> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "textures/gui/container/dimensional_quarry_item_filter_container.png");


    public DimensionalQuarryItemFilterScreen(DimensionalQuarryItemFilterContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw super class
        GuiUtil.drawCenteredString(guiGraphics, this.font, this.title, this.imageWidth / 2, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
