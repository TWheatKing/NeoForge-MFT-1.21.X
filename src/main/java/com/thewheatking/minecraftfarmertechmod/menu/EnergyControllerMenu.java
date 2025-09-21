package com.thewheatking.minecraftfarmertechmod.menu;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyControllerBlockEntity;
import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class EnergyControllerMenu extends BaseEnergyStorageMenu {

    private final EnergyControllerBlockEntity controllerBlockEntity;

    public EnergyControllerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(getMenuType(), containerId, playerInventory, extraData);
        this.controllerBlockEntity = (EnergyControllerBlockEntity) blockEntity;

        // No inventory slots needed - this is a control interface only
        // All interaction is through buttons on the client side
    }

    // Constructor for server-side creation
    public EnergyControllerMenu(int containerId, Inventory playerInventory, EnergyControllerBlockEntity blockEntity) {
        super(getMenuType(), containerId, playerInventory, blockEntity);
        this.controllerBlockEntity = blockEntity;
    }

    private static MenuType<?> getMenuType() {
        // This will be set when the menu type is registered
        // For now, return null - the registration system will handle this
        return null; // Will be replaced by the actual menu type during registration
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No inventory slots to move items between
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return controllerBlockEntity != null &&
                !controllerBlockEntity.isRemoved() &&
                player.distanceToSqr(controllerBlockEntity.getBlockPos().getX() + 0.5D,
                        controllerBlockEntity.getBlockPos().getY() + 0.5D,
                        controllerBlockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    // Getter methods for client-side UI to access controller state
    public EnergyControllerBlockEntity getControllerBlockEntity() {
        return controllerBlockEntity;
    }

    public boolean isPowerEnabled() {
        return controllerBlockEntity != null && controllerBlockEntity.isPowerEnabled();
    }

    public int getEnergyFlowLimit() {
        return controllerBlockEntity != null ? controllerBlockEntity.getEnergyFlowLimit() : 0;
    }

    public int getMaxEnergyFlowLimit() {
        return controllerBlockEntity != null ? controllerBlockEntity.getMaxEnergyFlowLimit() : 1000;
    }

    public int getConnectedDevicesCount() {
        return controllerBlockEntity != null ? controllerBlockEntity.getConnectedDevicesCount() : 0;
    }

    public int getNetworkEnergyUsage() {
        return controllerBlockEntity != null ? controllerBlockEntity.getNetworkEnergyUsage() : 0;
    }

    // Methods for handling button clicks (called from client-side screen)
    // These should send packets to the server to update the block entity
    public void togglePower() {
        if (controllerBlockEntity != null) {
            // Send packet to server to toggle power
            // Implementation depends on your networking system
        }
    }

    public void setEnergyFlowLimit(int limit) {
        if (controllerBlockEntity != null) {
            // Send packet to server to set energy flow limit
            // Implementation depends on your networking system
        }
    }

    public void emergencyShutdown() {
        if (controllerBlockEntity != null) {
            // Send packet to server to perform emergency shutdown
            // Implementation depends on your networking system
        }
    }

    // Override to ensure proper cleanup
    @Override
    public void removed(Player player) {
        super.removed(player);
        // Any cleanup needed when menu is closed
    }
}