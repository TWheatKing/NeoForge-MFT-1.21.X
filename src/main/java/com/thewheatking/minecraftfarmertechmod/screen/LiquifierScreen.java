package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiquifierScreen extends AbstractContainerScreen<LiquifierMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MinecraftFarmerTechMod.MOD_ID, "textures/gui/liquifier_gui.png");

    public LiquifierScreen(LiquifierMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 4;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderEnergyBar(guiGraphics, x, y);
        renderWaterBar(guiGraphics, x, y);
        renderBioFuelBar(guiGraphics, x, y);
        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyLevel = menu.getScaledEnergyLevel();
        if (energyLevel > 0) {
            // Energy bar on far left side (red/orange)
            // You'll need to add this texture to your GUI PNG file
            // For now, using a different color/position to distinguish from water
            guiGraphics.fill(x + 26, y + 18 + (52 - energyLevel), x + 34, y + 70,
                    0xFFFF6600); // Orange color for energy
        }

        // Energy bar border/outline
        guiGraphics.fill(x + 25, y + 17, x + 35, y + 18, 0xFF000000); // Top
        guiGraphics.fill(x + 25, y + 70, x + 35, y + 71, 0xFF000000); // Bottom
        guiGraphics.fill(x + 25, y + 17, x + 26, y + 71, 0xFF000000); // Left
        guiGraphics.fill(x + 34, y + 17, x + 35, y + 71, 0xFF000000); // Right
    }

    private void renderWaterBar(GuiGraphics guiGraphics, int x, int y) {
        int waterLevel = menu.getScaledWaterLevel();
        if (waterLevel > 0) {
            // Water bar (blue) - moved slightly right to make room for energy
            guiGraphics.blit(TEXTURE, x + 44, y + 18 + (52 - waterLevel),
                    176, 52 - waterLevel, 16, waterLevel);
        }
    }

    private void renderBioFuelBar(GuiGraphics guiGraphics, int x, int y) {
        int bioFuelLevel = menu.getScaledBioFuelLevel();
        if (bioFuelLevel > 0) {
            // Bio fuel bar on right side (green)
            guiGraphics.blit(TEXTURE, x + 116, y + 18 + (52 - bioFuelLevel),
                    192, 52 - bioFuelLevel, 16, bioFuelLevel);
        }
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            // Progress arrow in center
            guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 70, menu.getScaledProgress(), 16);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int pX, int pY) {
        super.renderTooltip(guiGraphics, pX, pY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy tooltip
        if (pX >= x + 25 && pX <= x + 35 && pY >= y + 17 && pY <= y + 71) {
            List<Component> tooltip = new ArrayList<>();
            int energy = menu.getEnergyAmount();
            int maxEnergy = menu.getMaxEnergyAmount();
            float percentage = menu.getEnergyPercentage();

            tooltip.add(Component.literal("Energy: " + energy + " / " + maxEnergy + " RF"));
            tooltip.add(Component.literal(String.format("%.1f%%", percentage * 100)));

            // Show if machine needs energy
            if (energy < 20) {
                tooltip.add(Component.literal("§cInsufficient Energy!"));
            }

            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), pX, pY);
        }

        // Water tooltip - back to original position
        if (pX >= x + 8 && pX <= x + 24 && pY >= y + 18 && pY <= y + 70) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Water: " + menu.getWaterAmount() + " / 10000 mB"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), pX, pY);
        }

        // Bio fuel tooltip - updated position
        if (pX >= x + 116 && pX <= x + 132 && pY >= y + 18 && pY <= y + 70) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Bio Fuel: " + menu.getBioFuelAmount() + " / 10000 mB"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), pX, pY);
        }

        // Progress tooltip
        if (pX >= x + 79 && pX <= x + 101 && pY >= y + 35 && pY <= y + 51) {
            if (menu.isCrafting()) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal("Processing..."));
                tooltip.add(Component.literal("Consuming 20 RF/tick"));
                guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), pX, pY);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}