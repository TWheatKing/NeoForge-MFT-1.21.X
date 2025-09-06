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

        renderWaterBar(guiGraphics, x, y);
        renderBioFuelBar(guiGraphics, x, y);
        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderWaterBar(GuiGraphics guiGraphics, int x, int y) {
        int waterLevel = menu.getScaledWaterLevel();
        if (waterLevel > 0) {
            // Water bar on left side (blue)
            guiGraphics.blit(TEXTURE, x + 8, y + 18 + (52 - waterLevel),
                    176, 52 - waterLevel, 16, waterLevel);
        }
    }

    private void renderBioFuelBar(GuiGraphics guiGraphics, int x, int y) {
        int bioFuelLevel = menu.getScaledBioFuelLevel();
        if (bioFuelLevel > 0) {
            // Bio fuel bar on right side (green)
            guiGraphics.blit(TEXTURE, x + 152, y + 18 + (52 - bioFuelLevel),
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

        // Water tooltip
        if (pX >= x + 8 && pX <= x + 24 && pY >= y + 18 && pY <= y + 70) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Water: " + menu.getWaterAmount() + " mB"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), pX, pY);
        }

        // Bio fuel tooltip
        if (pX >= x + 152 && pX <= x + 168 && pY >= y + 18 && pY <= y + 70) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Bio Fuel: " + menu.getBioFuelAmount() + " mB"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), pX, pY);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}