package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HybridCoalGeneratorScreen extends AbstractContainerScreen<HybridMenuTypes.HybridCoalGeneratorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MinecraftFarmerTechMod.MOD_ID, "textures/gui/hybrid_coal_generator_gui.png");

    public HybridCoalGeneratorScreen(HybridMenuTypes.HybridCoalGeneratorMenu menu,
                                     Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
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
        renderBurnIcon(guiGraphics, x, y);
        renderEnergyBar(guiGraphics, x, y);
    }

    private void renderBurnIcon(GuiGraphics guiGraphics, int x, int y) {
        // Fire icon similar to existing coal generator
        int burnProgress = 10; // Placeholder
        guiGraphics.blit(TEXTURE, x + 81, y + 54 + 14 - burnProgress,
                176, 14 - burnProgress, 14, burnProgress);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyHeight = 30; // Placeholder
        guiGraphics.blit(TEXTURE, x + 152, y + 18 + (52 - energyHeight),
                176, 16, 16, energyHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}