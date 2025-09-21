package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;

public class BasicEnergyStorageScreen extends AbstractContainerScreen<HybridMenuTypes.BasicEnergyStorageMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MinecraftFarmerTechMod.MOD_ID, "textures/gui/basic_energy_storage_gui.png");

    // Energy bar dimensions (horizontal at top middle)
    private static final int ENERGY_BAR_X = 60;
    private static final int ENERGY_BAR_Y = 20;
    private static final int ENERGY_BAR_WIDTH = 56;
    private static final int ENERGY_BAR_HEIGHT = 8;

    // Status light positions
    private static final int STATUS_LIGHT_X = 130;
    private static final int STATUS_LIGHT_Y = 18;
    private static final int STATUS_LIGHT_SIZE = 12;

    public BasicEnergyStorageScreen(HybridMenuTypes.BasicEnergyStorageMenu menu,
                                    Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderHorizontalEnergyBar(guiGraphics, x, y);
        renderStatusLights(guiGraphics, x, y);
    }

    private void renderHorizontalEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int barX = x + ENERGY_BAR_X;
        int barY = y + ENERGY_BAR_Y;

        float energyPercentage = menu.getEnergyPercentage();
        int energyColor = menu.getEnergyBarColor();
        int filledWidth = (int) (ENERGY_BAR_WIDTH * energyPercentage);

        // Background
        guiGraphics.fill(barX, barY, barX + ENERGY_BAR_WIDTH, barY + ENERGY_BAR_HEIGHT, 0xFF333333);

        // Energy fill (red/yellow/green based on level)
        if (filledWidth > 0) {
            guiGraphics.fill(barX, barY, barX + filledWidth, barY + ENERGY_BAR_HEIGHT, energyColor);
        }

        // Border
        guiGraphics.fill(barX - 1, barY - 1, barX + ENERGY_BAR_WIDTH + 1, barY, 0xFF000000);
        guiGraphics.fill(barX - 1, barY + ENERGY_BAR_HEIGHT, barX + ENERGY_BAR_WIDTH + 1,
                barY + ENERGY_BAR_HEIGHT + 1, 0xFF000000);
        guiGraphics.fill(barX - 1, barY, barX, barY + ENERGY_BAR_HEIGHT, 0xFF000000);
        guiGraphics.fill(barX + ENERGY_BAR_WIDTH, barY, barX + ENERGY_BAR_WIDTH + 1,
                barY + ENERGY_BAR_HEIGHT, 0xFF000000);

        // Percentage text
        String percentText = String.format("%.0f%%", energyPercentage * 100);
        int textX = barX + (ENERGY_BAR_WIDTH / 2) - (font.width(percentText) / 2);
        guiGraphics.drawString(font, percentText, textX, barY + 1, 0xFFFFFFFF);
    }

    private void renderStatusLights(GuiGraphics guiGraphics, int x, int y) {
        int lightX = x + STATUS_LIGHT_X;
        int lightY = y + STATUS_LIGHT_Y;

        HybridMenuTypes.BasicEnergyStorageMenu.EnergyStatus status = menu.getEnergyStatus();

        int statusColor = switch (status) {
            case CHARGING -> 0xFF00FF00;    // Green
            case DISCHARGING -> 0xFFFFAA00; // Orange
            case FULL -> 0xFF0099FF;        // Blue
            case IDLE -> 0xFF666666;        // Gray
        };

        // Light background and fill
        guiGraphics.fill(lightX, lightY, lightX + STATUS_LIGHT_SIZE, lightY + STATUS_LIGHT_SIZE, 0xFF222222);
        guiGraphics.fill(lightX + 2, lightY + 2, lightX + STATUS_LIGHT_SIZE - 2,
                lightY + STATUS_LIGHT_SIZE - 2, statusColor);

        // Light border
        guiGraphics.fill(lightX - 1, lightY - 1, lightX + STATUS_LIGHT_SIZE + 1, lightY, 0xFF000000);
        guiGraphics.fill(lightX - 1, lightY + STATUS_LIGHT_SIZE, lightX + STATUS_LIGHT_SIZE + 1,
                lightY + STATUS_LIGHT_SIZE + 1, 0xFF000000);
        guiGraphics.fill(lightX - 1, lightY, lightX, lightY + STATUS_LIGHT_SIZE, 0xFF000000);
        guiGraphics.fill(lightX + STATUS_LIGHT_SIZE, lightY, lightX + STATUS_LIGHT_SIZE + 1,
                lightY + STATUS_LIGHT_SIZE, 0xFF000000);

        // Status text
        String statusText = switch (status) {
            case CHARGING -> "Charging";
            case DISCHARGING -> "Discharging";
            case FULL -> "Full";
            case IDLE -> "Idle";
        };
        guiGraphics.drawString(font, statusText, lightX + STATUS_LIGHT_SIZE + 4, lightY + 2, 0xFFFFFFFF);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy bar tooltip
        int barX = x + ENERGY_BAR_X;
        int barY = y + ENERGY_BAR_Y;

        if (mouseX >= barX && mouseX <= barX + ENERGY_BAR_WIDTH &&
                mouseY >= barY && mouseY <= barY + ENERGY_BAR_HEIGHT) {

            List<Component> tooltip = new ArrayList<>();
            NumberFormat formatter = NumberFormat.getInstance();
            String currentEnergy = formatter.format(menu.getCurrentEnergy());
            String maxEnergy = formatter.format(menu.getMaxEnergy());

            tooltip.add(Component.literal("Basic Energy Storage"));
            tooltip.add(Component.literal("Energy: " + currentEnergy + " / " + maxEnergy + " FE"));
            tooltip.add(Component.literal("Charge: " + String.format("%.1f%%", menu.getEnergyPercentage() * 100)));
            tooltip.add(Component.literal("Capacity: 50,000 FE"));
            tooltip.add(Component.literal("Transfer: 1,000 FE/t"));

            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}