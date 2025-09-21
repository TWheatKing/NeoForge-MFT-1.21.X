package com.thewheatking.minecraftfarmertechmod.menu.base;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyCapabilityProviders.IHybridEnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class BaseEnergyStorageMenu extends AbstractContainerMenu {

    protected final BlockEntity blockEntity;
    protected final IHybridEnergyBlockEntity energyBlockEntity;
    protected final Player player;

    // Constructor for client-side (from network data)
    protected BaseEnergyStorageMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(menuType, containerId);
        this.player = playerInventory.player;

        // Read block position from network data
        BlockPos pos = extraData.readBlockPos();
        this.blockEntity = player.level().getBlockEntity(pos);

        if (blockEntity instanceof IHybridEnergyBlockEntity hybridEnergyBE) {
            this.energyBlockEntity = hybridEnergyBE;
        } else {
            this.energyBlockEntity = null;
        }

        // Add player inventory slots
        addPlayerInventorySlots(playerInventory);
    }

    // Constructor for server-side
    protected BaseEnergyStorageMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(menuType, containerId);
        this.player = playerInventory.player;
        this.blockEntity = blockEntity;

        if (blockEntity instanceof IHybridEnergyBlockEntity hybridEnergyBE) {
            this.energyBlockEntity = hybridEnergyBE;
        } else {
            this.energyBlockEntity = null;
        }

        // Add player inventory slots
        addPlayerInventorySlots(playerInventory);
    }

    /**
     * Adds the standard 9x4 player inventory (including hotbar) to the menu
     */
    protected void addPlayerInventorySlots(Inventory playerInventory) {
        // Player inventory (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar (1x9)
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    /**
     * Adds the standard 9x4 player inventory with custom positioning
     */
    protected void addPlayerInventorySlots(Inventory playerInventory, int startX, int startY) {
        // Player inventory (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // Player hotbar (1x9) - 24 pixels below inventory
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInventory, col, startX + col * 18, startY + 58));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null &&
                !blockEntity.isRemoved() &&
                player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5D,
                        blockEntity.getBlockPos().getY() + 0.5D,
                        blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    // Energy-related getter methods for client-side UI
    public int getEnergyStored() {
        if (energyBlockEntity != null) {
            IEnergyStorage energyStorage = energyBlockEntity.getEnergyStorage(null);
            return energyStorage != null ? energyStorage.getEnergyStored() : 0;
        }
        return 0;
    }

    public int getMaxEnergyStored() {
        if (energyBlockEntity != null) {
            IEnergyStorage energyStorage = energyBlockEntity.getEnergyStorage(null);
            return energyStorage != null ? energyStorage.getMaxEnergyStored() : 0;
        }
        return 0;
    }

    public int getEnergyCapacity() {
        return getMaxEnergyStored();
    }

    public float getEnergyPercentage() {
        int max = getMaxEnergyStored();
        if (max <= 0) return 0.0f;
        return (float) getEnergyStored() / (float) max;
    }

    public boolean canExtractEnergy() {
        if (energyBlockEntity != null) {
            IEnergyStorage energyStorage = energyBlockEntity.getEnergyStorage(null);
            return energyStorage != null && energyStorage.canExtract();
        }
        return false;
    }

    public boolean canReceiveEnergy() {
        if (energyBlockEntity != null) {
            IEnergyStorage energyStorage = energyBlockEntity.getEnergyStorage(null);
            return energyStorage != null && energyStorage.canReceive();
        }
        return false;
    }

    // Utility method for energy transfer rate display
    public int getEnergyTransferRate() {
        // Override in subclasses if they track transfer rates
        return 0;
    }

    // Utility method to get the block entity (for subclasses)
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public IHybridEnergyBlockEntity getEnergyBlockEntity() {
        return energyBlockEntity;
    }

    // Helper method for moving items between machine and player inventory
    protected boolean moveItemStackToPlayerInventory(net.minecraft.world.item.ItemStack stack, int playerInventoryStartSlot) {
        return moveItemStackTo(stack, playerInventoryStartSlot, slots.size(), true);
    }

    // Helper method for moving items from player inventory to machine
    protected boolean moveItemStackToMachine(net.minecraft.world.item.ItemStack stack, int machineStartSlot, int machineEndSlot) {
        return moveItemStackTo(stack, machineStartSlot, machineEndSlot, false);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Override in subclasses if cleanup is needed
    }
}