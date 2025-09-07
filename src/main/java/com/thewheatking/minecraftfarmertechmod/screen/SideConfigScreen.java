package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.config.SideConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * GUI Screen for configuring machine side settings with improved layout and interactions
 */
public class SideConfigScreen extends AbstractContainerScreen<SideConfigMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MinecraftFarmerTechMod.MOD_ID, "textures/gui/side_config_gui.png");

    private static final int FACE_SIZE = 32;
    private static final int CENTER_X = 160; // Moved right to make room for legend
    private static final int CENTER_Y = 70;

    // Map directions to their buttons for easier access
    private final Map<Direction, FaceButton> faceButtons = new HashMap<>();
    private final Map<SideConfig.SideMode, ModeButton> modeButtons = new HashMap<>();
    private Button applyButton;
    private Button resetButton;

    // Selected mode for painting faces
    private SideConfig.SideMode selectedMode = null;

    public SideConfigScreen(SideConfigMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 320; // Much wider to accommodate legend and spread out layout
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Create mode selection buttons (legend area)
        createModeButtons(x, y);

        // Create face buttons in an isometric cube layout
        createFaceButtons(x, y);

        // Apply button
        applyButton = Button.builder(Component.literal("Apply"), button -> {
            menu.applyConfiguration();
            onClose();
        }).bounds(x + 20, y + 170, 50, 20).build();
        addRenderableWidget(applyButton);

        // Reset button
        resetButton = Button.builder(Component.literal("Reset"), button -> {
            menu.resetConfiguration();
            selectedMode = null;
            updateButtons();
        }).bounds(x + 250, y + 170, 50, 20).build();
        addRenderableWidget(resetButton);

        updateButtons();
    }

    private void createModeButtons(int x, int y) {
        SideConfig.SideMode[] modes = SideConfig.SideMode.values();

        for (int i = 0; i < modes.length; i++) {
            SideConfig.SideMode mode = modes[i];
            int buttonY = y + 35 + i * 20;

            ModeButton modeButton = new ModeButton(x + 10, buttonY, mode);
            modeButtons.put(mode, modeButton);
            addRenderableWidget(modeButton);
        }
    }

    private void createFaceButtons(int x, int y) {
        // Create face buttons in a 3D cube layout that makes sense

        // NORTH face (front center) - what you see when facing the block
        FaceButton northButton = new FaceButton(
                x + CENTER_X, y + CENTER_Y,
                Direction.NORTH
        );
        faceButtons.put(Direction.NORTH, northButton);
        addRenderableWidget(northButton);

        // SOUTH face (back, shown smaller behind north)
        FaceButton southButton = new FaceButton(
                x + CENTER_X + 8, y + CENTER_Y - 8,
                Direction.SOUTH
        );
        faceButtons.put(Direction.SOUTH, southButton);
        addRenderableWidget(southButton);

        // EAST face (right side)
        FaceButton eastButton = new FaceButton(
                x + CENTER_X + FACE_SIZE + 16, y + CENTER_Y,
                Direction.EAST
        );
        faceButtons.put(Direction.EAST, eastButton);
        addRenderableWidget(eastButton);

        // WEST face (left side)
        FaceButton westButton = new FaceButton(
                x + CENTER_X - FACE_SIZE - 16, y + CENTER_Y,
                Direction.WEST
        );
        faceButtons.put(Direction.WEST, westButton);
        addRenderableWidget(westButton);

        // UP face (top)
        FaceButton upButton = new FaceButton(
                x + CENTER_X, y + CENTER_Y - FACE_SIZE - 16,
                Direction.UP
        );
        faceButtons.put(Direction.UP, upButton);
        addRenderableWidget(upButton);

        // DOWN face (bottom)
        FaceButton downButton = new FaceButton(
                x + CENTER_X, y + CENTER_Y + FACE_SIZE + 16,
                Direction.DOWN
        );
        faceButtons.put(Direction.DOWN, downButton);
        addRenderableWidget(downButton);
    }

    private void updateButtons() {
        // Update face buttons
        for (Direction direction : Direction.values()) {
            FaceButton button = faceButtons.get(direction);
            if (button != null) {
                SideConfig.SideMode mode = menu.getSideMode(direction);
                button.setMode(mode);
            }
        }

        // Update mode button selection
        for (ModeButton modeButton : modeButtons.values()) {
            modeButton.setSelected(modeButton.getMode() == selectedMode);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Draw background
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF8B8B8B);
        guiGraphics.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFFC6C6C6);

        // Draw title
        guiGraphics.drawString(font, "Side Configuration", x + 8, y + 6, 0x404040, false);

        // Draw mode selection area title
        guiGraphics.drawString(font, "Select Mode:", x + 10, y + 20, 0x404040, false);

        // Draw face area title
        guiGraphics.drawString(font, "Machine Faces:", x + 120, y + 20, 0x404040, false);

        // Draw connecting lines to show cube structure
        drawCubeLines(guiGraphics, x, y);

        // Draw instruction text
        if (selectedMode != null) {
            guiGraphics.drawString(font, "Click a face to apply " + selectedMode.getDisplayName(),
                    x + 120, y + 155, 0x404040, false);
        } else {
            guiGraphics.drawString(font, "Left-click: cycle mode | Right-click: select mode first",
                    x + 120, y + 155, 0x404040, false);
        }
    }

    private void drawCubeLines(GuiGraphics guiGraphics, int x, int y) {
        int color = 0xFF404040;

        // Draw lines connecting the cube faces
        FaceButton north = faceButtons.get(Direction.NORTH);
        FaceButton east = faceButtons.get(Direction.EAST);
        FaceButton west = faceButtons.get(Direction.WEST);
        FaceButton up = faceButtons.get(Direction.UP);
        FaceButton down = faceButtons.get(Direction.DOWN);

        // Horizontal lines
        if (north != null && east != null) {
            drawLine(guiGraphics, north.getX() + FACE_SIZE, north.getY() + FACE_SIZE/2,
                    east.getX(), east.getY() + FACE_SIZE/2, color);
        }
        if (north != null && west != null) {
            drawLine(guiGraphics, north.getX(), north.getY() + FACE_SIZE/2,
                    west.getX() + FACE_SIZE, west.getY() + FACE_SIZE/2, color);
        }

        // Vertical lines
        if (north != null && up != null) {
            drawLine(guiGraphics, north.getX() + FACE_SIZE/2, north.getY(),
                    up.getX() + FACE_SIZE/2, up.getY() + FACE_SIZE, color);
        }
        if (north != null && down != null) {
            drawLine(guiGraphics, north.getX() + FACE_SIZE/2, north.getY() + FACE_SIZE,
                    down.getX() + FACE_SIZE/2, down.getY(), color);
        }
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
            int startX = Math.min(x1, x2);
            int endX = Math.max(x1, x2);
            int y = (y1 + y2) / 2;
            guiGraphics.fill(startX, y, endX, y + 1, color);
        } else {
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);
            int x = (x1 + x2) / 2;
            guiGraphics.fill(x, startY, x + 1, endY, color);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        // Show tooltips for face buttons (only when actually hovering)
        for (Map.Entry<Direction, FaceButton> entry : faceButtons.entrySet()) {
            FaceButton button = entry.getValue();
            if (mouseX >= button.getX() && mouseX < button.getX() + button.getWidth() &&
                    mouseY >= button.getY() && mouseY < button.getY() + button.getHeight()) {

                Direction direction = entry.getKey();
                SideConfig.SideMode mode = menu.getSideMode(direction);

                // Get friendly name based on direction
                String friendlyName = switch (direction) {
                    case NORTH -> "Front";
                    case SOUTH -> "Back";
                    case EAST -> "Right Side";
                    case WEST -> "Left Side";
                    case UP -> "Top";
                    case DOWN -> "Bottom";
                };

                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(friendlyName));
                tooltip.add(Component.literal("Current: " + mode.getDisplayName()));
                tooltip.add(Component.literal("Left-click: cycle modes"));
                if (selectedMode != null) {
                    tooltip.add(Component.literal("Right-click: set to " + selectedMode.getDisplayName()));
                }

                guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
                break;
            }
        }

        // Show tooltips for mode buttons
        for (ModeButton modeButton : modeButtons.values()) {
            if (mouseX >= modeButton.getX() && mouseX < modeButton.getX() + modeButton.getWidth() &&
                    mouseY >= modeButton.getY() && mouseY < modeButton.getY() + modeButton.getHeight()) {

                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(modeButton.getMode().getDisplayName()));
                tooltip.add(Component.literal("Click to select this mode"));

                guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Don't render default labels
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /**
     * Custom button class for mode selection in the legend
     */
    private class ModeButton extends Button {
        private final SideConfig.SideMode mode;
        private boolean selected = false;

        public ModeButton(int x, int y, SideConfig.SideMode mode) {
            super(x, y, 100, 16, Component.literal(mode.getDisplayName()),
                    button -> {
                        selectedMode = mode;
                        updateButtons();
                    }, DEFAULT_NARRATION);
            this.mode = mode;
        }

        public SideConfig.SideMode getMode() {
            return mode;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // Draw colored background
            int bgColor = selected ? 0xFFFFFFFF : mode.getColor();
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), bgColor);

            // Draw border
            int borderColor = selected ? 0xFF000000 : (isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF404040);
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + 1, borderColor);
            guiGraphics.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), borderColor);
            guiGraphics.fill(getX(), getY(), getX() + 1, getY() + getHeight(), borderColor);
            guiGraphics.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), borderColor);

            // Draw text
            int textColor = selected ? 0xFF000000 : 0xFFFFFFFF;
            guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, getMessage(),
                    getX() + 4, getY() + 4, textColor, false);
        }
    }

    /**
     * Custom button class for face configuration
     */
    private class FaceButton extends Button {
        private final Direction direction;
        private SideConfig.SideMode mode = SideConfig.SideMode.NONE;

        public FaceButton(int x, int y, Direction direction) {
            super(x, y, FACE_SIZE, FACE_SIZE, Component.literal(""),
                    button -> {}, DEFAULT_NARRATION);
            this.direction = direction;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.isValidClickButton(button) && this.clicked(mouseX, mouseY)) {
                if (button == 0) { // Left click - cycle
                    menu.cycleSideMode(direction);
                    updateButtons();
                    return true;
                } else if (button == 1 && selectedMode != null) { // Right click - apply selected mode
                    menu.getTempConfig().setSideMode(direction, selectedMode);
                    updateButtons();
                    return true;
                }
            }
            return false;
        }

        public void setMode(SideConfig.SideMode mode) {
            this.mode = mode;

            // Update button text based on mode
            String modeChar = switch (mode) {
                case NONE -> "X";
                case INPUT -> "I";
                case OUTPUT -> "O";
                case INPUT_OUTPUT -> "B";
                case ENERGY_INPUT -> "E";
                case ENERGY_OUTPUT -> "P";
            };

            this.setMessage(Component.literal(modeChar));
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // Draw colored background based on mode
            int color = mode.getColor();
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);

            // Draw border
            int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF000000;
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + 1, borderColor);
            guiGraphics.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), borderColor);
            guiGraphics.fill(getX(), getY(), getX() + 1, getY() + getHeight(), borderColor);
            guiGraphics.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), borderColor);

            // Draw text
            guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, getMessage(),
                    getX() + getWidth() / 2, getY() + getHeight() / 2 - 4, 0xFFFFFFFF);
        }
    }
}