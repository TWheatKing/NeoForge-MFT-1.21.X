package com.thewheatking.minecraftfarmertechmod.screen;
import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnergyAnalyzerScreen extends AbstractContainerScreen<EnergyAnalyzerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "textures/gui/energy_analyzer_gui.png");
    public EnergyAnalyzerScreen(EnergyAnalyzerMenu menu, Inventory playerInventory, Component title) { super(menu, playerInventory, title); }
    @Override protected void init() { super.init(); this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2; this.titleLabelY = 6; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) { int x = (width - imageWidth) / 2; int y = (height - imageHeight) / 2; guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight); }
    @Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) { renderBackground(guiGraphics, mouseX, mouseY, delta); super.render(guiGraphics, mouseX, mouseY, delta); renderTooltip(guiGraphics, mouseX, mouseY); }
}