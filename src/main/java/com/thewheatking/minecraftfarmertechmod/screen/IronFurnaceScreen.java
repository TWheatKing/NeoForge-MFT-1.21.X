package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IronFurnaceScreen extends AbstractContainerScreen<IronFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "textures/gui/iron_furnace_gui.png");

    public IronFurnaceScreen(IronFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        //this.inventoryLabelY = 10000;
        //this.titleLabelY = 10000;
        // Set proper label positions
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2; // Center the title
        this.titleLabelY = 6; // Standard title position
        this.inventoryLabelY = this.imageHeight - 94; // Standard inventory label position
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
        renderFuelIcon(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 79, y + 34, 176, 0, menu.getScaledProgress(), 16);
        }
    }

    private void renderFuelIcon(GuiGraphics guiGraphics, int x, int y) {
        if (menu.hasFuel()) {
            // Fixed fire rendering - render from bottom up as fuel burns
            int fuelHeight = menu.getScaledFuelProgress();
            guiGraphics.blit(TEXTURE, x + 56, y + 36 + (14 - fuelHeight),
                    176, 16 + (14 - fuelHeight), 14, fuelHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}