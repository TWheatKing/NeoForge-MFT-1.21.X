package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.config.SideConfig;
import com.thewheatking.minecraftfarmertechmod.config.SideConfigurable;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Menu for configuring machine side settings
 */
public class SideConfigMenu extends AbstractContainerMenu {

    private final SideConfigurable configurable;
    private final Level level;
    private SideConfig tempConfig; // Temporary config for editing

    // Constructor for client side
    public SideConfigMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Constructor for server side
    public SideConfigMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(ModMenuTypes.SIDE_CONFIG_MENU.get(), containerId);

        if (!(blockEntity instanceof SideConfigurable)) {
            throw new IllegalArgumentException("BlockEntity must implement SideConfigurable");
        }

        this.configurable = (SideConfigurable) blockEntity;
        this.level = playerInventory.player.level();
        this.tempConfig = configurable.getSideConfig().copy(); // Work with a copy

        // Add player inventory slots (disabled for this GUI)
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    /**
     * Called when a side button is clicked
     */
    public void cycleSideMode(Direction side) {
        tempConfig.cycleSideMode(side);
    }

    /**
     * Called when Apply button is clicked
     */
    public void applyConfiguration() {
        configurable.setSideConfig(tempConfig);
        // Notify all sides of changes
        for (Direction direction : Direction.values()) {
            configurable.onSideConfigChanged(direction, tempConfig.getSideMode(direction));
        }

        // Force the block entity to save immediately
        if (configurable instanceof BlockEntity blockEntity) {
            blockEntity.setChanged();
            // Also mark the chunk as dirty to ensure it gets saved
            if (blockEntity.getLevel() != null) {
                blockEntity.getLevel().blockEntityChanged(blockEntity.getBlockPos());
            }
        }
    }


    /**
     * Called when Reset button is clicked
     */
    public void resetConfiguration() {
        tempConfig = configurable.getSideConfig().copy();
    }

    public SideConfig getTempConfig() {
        return tempConfig;
    }

    public SideConfig.SideMode getSideMode(Direction side) {
        return tempConfig.getSideMode(side);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY; // No item movement in config GUI
    }

    @Override
    public boolean stillValid(Player player) {
        if (configurable instanceof BlockEntity blockEntity) {
            return player.distanceToSqr(
                    blockEntity.getBlockPos().getX() + 0.5,
                    blockEntity.getBlockPos().getY() + 0.5,
                    blockEntity.getBlockPos().getZ() + 0.5
            ) <= 64.0;
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Auto-apply changes when closing GUI
        applyConfiguration();

        // Force save to NBT
        if (configurable instanceof BlockEntity blockEntity) {
            blockEntity.setChanged();
            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
                blockEntity.getLevel().blockEntityChanged(blockEntity.getBlockPos());
            }
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // Player inventory (disabled/hidden in this GUI)
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new DisabledSlot(playerInventory, l + i * 9 + 9, -1000, -1000));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        // Player hotbar (disabled/hidden in this GUI)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new DisabledSlot(playerInventory, i, -1000, -1000));
        }
    }

    /**
     * Disabled slot that can't be interacted with
     */
    private static class DisabledSlot extends Slot {
        public DisabledSlot(net.minecraft.world.Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}