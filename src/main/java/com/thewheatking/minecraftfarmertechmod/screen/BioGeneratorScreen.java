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

public class BioGeneratorScreen extends AbstractContainerScreen<BioGeneratorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MinecraftFarmerTechMod.MOD_ID, "textures/gui/bio_generator_gui.png");

    public BioGeneratorScreen(BioGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        renderBurnIcon(guiGraphics, x, y);
        renderEnergyBar(guiGraphics, x, y);
    }

    private void renderBurnIcon(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isBurning()) {
            int burnProgress = menu.getScaledBurnProgress();
            // Fire icon below the fuel slot
            guiGraphics.blit(TEXTURE, x + 81, y + 54 + 14 - burnProgress,
                    176, 14 - burnProgress, 14, burnProgress);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyHeight = menu.getScaledEnergyProgress();

        if (energyHeight > 0) {
            // Green energy bar for bio generator
            guiGraphics.blit(TEXTURE,
                    x + 152,
                    y + 18 + (52 - energyHeight),
                    176,
                    16 + (52 - energyHeight),
                    16,
                    energyHeight);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int pX, int pY) {
        super.renderTooltip(guiGraphics, pX, pY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Energy bar tooltip
        if (pX >= x + 152 && pX <= x + 168 && pY >= y + 18 && pY <= y + 70) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Energy: " + menu.getEnergyStored() + "/" + menu.getMaxEnergyStored() + " RF"));
            tooltip.add(Component.literal("Generation: 40 RF/t (Tier 3)"));
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