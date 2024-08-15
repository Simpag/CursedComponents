package com.ccteam.cursedcomponents.gui.screens.custom;

import com.ccteam.cursedcomponents.CursedComponentsMod;
import com.ccteam.cursedcomponents.block.entity.custom.DimensionalQuarryEntity;
import com.ccteam.cursedcomponents.gui.GuiUtil;
import com.ccteam.cursedcomponents.gui.containers.custom.DimensionalQuarryContainer;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DimensionalQuarryScreen extends AbstractContainerScreen<DimensionalQuarryContainer> {
    private static final boolean DEBUG = true;
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "textures/gui/container/dimensional_quarry_container.png");
    private static final ResourceLocation WARNING_SPRITE = ResourceLocation.fromNamespaceAndPath(CursedComponentsMod.MOD_ID, "container/dimensional_quarry/warning_triangle");
    private final int green;
    private final int dark_green;
    private final int red;

    private ExtendedButton startRunningButton;
    private ExtendedButton stopRunningButton;

    public DimensionalQuarryScreen(DimensionalQuarryContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 181;
        this.inventoryLabelY = this.imageHeight - 94;
        this.green = FastColor.ARGB32.color(255, 31, 125, 0);
        this.dark_green = FastColor.ARGB32.color(255, 18, 74, 0);
        this.red = FastColor.ARGB32.color(255, 135, 0, 14);
    }

    @Override
    protected void init() {
        super.init();

        this.startRunningButton = new ExtendedButton(
                this.leftPos + 124 - 25,
                this.topPos + 56 - 16,
                50,
                16,
                Component.translatable("menu.cursedcomponents.dimensional_quarry.start_button"),
                (button) -> menu.setRunning(true)
        );

        this.stopRunningButton = new ExtendedButton(
                this.leftPos + 124 - 25,
                this.topPos + 74 - 16,
                50,
                16,
                Component.translatable("menu.cursedcomponents.dimensional_quarry.stop_button"),
                (button) -> menu.setRunning(false)
        );

        this.addRenderableWidget(this.startRunningButton);
        this.addRenderableWidget(this.stopRunningButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Draw warning sprite if needed
        List<DimensionalQuarryEntity.MiningRequirement> reqs = this.menu.getMiningRequirements();
        if (reqs.get(0) != DimensionalQuarryEntity.MiningRequirement.ok) {
            guiGraphics.blitSprite(WARNING_SPRITE, this.leftPos - 26, this.topPos, 26, 26);

            // Render requirement
            if (mouseX > this.leftPos - 26 && mouseX < this.leftPos
                    && mouseY > this.topPos && mouseY < this.topPos + 26) {

                var reqs_components = DimensionalQuarryEntity.MiningRequirement.getComponentList(reqs);
                guiGraphics.renderComponentTooltip(
                        this.font,
                        reqs_components,
                        mouseX,
                        mouseY);
            }
        }

        updateRunningButtons();

        // Progress bar
        float bar_progress = (float) this.menu.getCooldown() / DimensionalQuarryEntity.TICKS_PER_BLOCK;
        //int progress_bar_y = (int) ((73f - 21f) * bar_progress + 21f); // downwards
        int progress_bar_y = (int) (73f - (73f - 21f) * bar_progress); // upwards
        guiGraphics.fillGradient(this.leftPos + 166, this.topPos + progress_bar_y, this.leftPos + 168, this.topPos + 73, this.green, this.dark_green);

        // Energy bar
        float energy_bar_progress = (float) this.menu.getEnergyStored() / DimensionalQuarryEntity.ENERGY_CAPACITY;
        int energy_bar_x = (int) ((168f - 8f) * energy_bar_progress + 8f);
        int energy_bar_color = this.menu.getEnergyStored() < DimensionalQuarryEntity.ENERGY_CONSUMPTION_PER_TICK * DimensionalQuarryEntity.TICKS_PER_BLOCK ? this.red : this.green;
        guiGraphics.fill(this.leftPos + 8, this.topPos + 80, this.leftPos + energy_bar_x, this.topPos + 82, energy_bar_color);

        // Render energy consumption per tick tooltip
        if (mouseX > this.leftPos + 8 && mouseX < this.leftPos + 168f
                && mouseY > this.topPos + 79 && mouseY < this.topPos + 83) {
            List<Component> energy_tooltip = new ArrayList<>();
            energy_tooltip.add(Component.translatable("tooltip.cursedcomponents.dimensional_quarry.tooltip.energy_consumption.1", DimensionalQuarryEntity.ENERGY_CAPACITY));
            energy_tooltip.add(Component.translatable("tooltip.cursedcomponents.dimensional_quarry.tooltip.energy_consumption.2", DimensionalQuarryEntity.ENERGY_CONSUMPTION_PER_TICK));

            guiGraphics.renderComponentTooltip(
                    this.font,
                    energy_tooltip,
                    mouseX,
                    mouseY);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw super class
        GuiUtil.drawCenteredString(guiGraphics, this.font, this.title, this.imageWidth / 2, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);

        // Draw current y level
        GuiUtil.drawScaledString(
                this.font,
                guiGraphics,
                Component.translatable("menu.cursedcomponents.dimensional_quarry.current_y_level", this.menu.getCurrentYLevel()),
                124 - 30,
                20,
                4210752,
                false,
                0.9f
        );

        if (this.DEBUG) {
            guiGraphics.drawString(this.font, "Stored: " + String.valueOf(this.menu.getEnergyStored()), -100, 23, ChatFormatting.RED.getColor(), false);
            guiGraphics.drawString(this.font, "Capacity: " + String.valueOf(DimensionalQuarryEntity.ENERGY_CAPACITY), -100, 33, ChatFormatting.RED.getColor(), false);
            guiGraphics.drawString(this.font, "Consumption Rate (FE/t): " + String.valueOf(DimensionalQuarryEntity.ENERGY_CONSUMPTION_PER_TICK), -100, 43, ChatFormatting.RED.getColor(), false);
            guiGraphics.drawString(this.font, "Y: " + String.valueOf(this.menu.getCurrentYLevel()), -100, 53, ChatFormatting.RED.getColor(), false);
            guiGraphics.drawString(this.font, "Cooldown: " + String.valueOf(this.menu.getCooldown()), -100, 63, ChatFormatting.RED.getColor(), false);
            guiGraphics.drawString(this.font, "progress: " + String.valueOf((float) this.menu.getCooldown() / DimensionalQuarryEntity.TICKS_PER_BLOCK), -100, 73, ChatFormatting.RED.getColor(), false);
            guiGraphics.drawString(this.font, "Running: " + String.valueOf(this.menu.getRunning()), -100, 83, ChatFormatting.RED.getColor(), false);
        }
    }

    private void updateRunningButtons() {
        if (this.menu.getRunning() == 1) {
            this.startRunningButton.active = false;
            this.stopRunningButton.active = true;
        } else {
            this.startRunningButton.active = true;
            this.stopRunningButton.active = false;
        }
    }
}