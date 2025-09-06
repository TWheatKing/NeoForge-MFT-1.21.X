package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CoalGeneratorScreen extends AbstractContainerScreen<CoalGeneratorMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "textures/gui/coal_generator_gui.png");

    public CoalGeneratorScreen(CoalGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderBurnIcon(guiGraphics, x, y);
        renderEnergyBar(guiGraphics, x, y);
    }

    private void renderBurnIcon(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isBurning()) {
            int burnProgress = menu.getScaledBurnProgress();
            // Render fire icon below the fuel slot
            guiGraphics.blit(TEXTURE, x + 81, y + 54 + 14 - burnProgress,
                    176, 14 - burnProgress, 14, burnProgress);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyHeight = menu.getScaledEnergyProgress();

        // Debug: Keep this line temporarily to verify height is still working
        //System.out.println("Energy Height: " + energyHeight);

        if (energyHeight > 0) {
            // Render the actual energy bar texture (removed the green rectangle)
            guiGraphics.blit(TEXTURE,
                    x + 152,                    // X position in GUI
                    y + 18 + (52 - energyHeight), // Y position (bottom up)
                    176,                        // Texture X
                    16 + (52 - energyHeight),   // Texture Y
                    16,                         // Width
                    energyHeight);              // Height
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int pX, int pY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy bar tooltip
        if (pX >= x + 152 && pX <= x + 168 && pY >= y + 18 && pY <= y + 70) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Energy: " + menu.getEnergyStored() + "/" + menu.getMaxEnergyStored() + " RF"));
            tooltip.add(Component.literal("Generation: 20 RF/tick"));
            guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(), pX, pY);
            return;
        }

        super.renderTooltip(guiGraphics, pX, pY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}