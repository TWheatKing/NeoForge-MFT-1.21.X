package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Basic Energy Storage Menu - follows the same pattern as CoalGeneratorMenu
 * Based on TWheatKing's MFT framework patterns
 * Minecraft 1.21 | NeoForge 21.0.167
 */
public class BasicEnergyStorageMenu extends AbstractContainerMenu {
    public final BasicEnergyStorageBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // Constructor for network creation (like your CoalGeneratorMenu)
    public BasicEnergyStorageMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    // Constructor for direct creation (like your CoalGeneratorMenu)
    public BasicEnergyStorageMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BASIC_ENERGY_STORAGE_MENU.get(), pContainerId);
        // Note: Energy storage doesn't need checkContainerSize since it has no item slots
        blockEntity = ((BasicEnergyStorageBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // No item slots needed for basic energy storage - it's just a battery

        addDataSlots(data);
    }

    // Energy status methods (similar to how CoalGeneratorMenu has isBurning(), getScaledBurnProgress())
    public boolean isActive() {
        return data.get(0) > 0; // Has energy stored
    }

    public int getCurrentEnergy() {
        return data.get(0);
    }

    public int getMaxEnergy() {
        return data.get(1);
    }

    public int getPreviousEnergy() {
        return data.get(2);
    }

    public float getEnergyPercentage() {
        int max = getMaxEnergy();
        if (max == 0) return 0.0f;
        return (float) getCurrentEnergy() / max;
    }

    // Scaled energy for horizontal bar (like getScaledEnergyProgress in CoalGeneratorMenu)
    public int getScaledEnergy(int scale) {
        int energy = getCurrentEnergy();
        int maxEnergy = getMaxEnergy();

        if (maxEnergy == 0) return 0;

        // Ensure at least 1 pixel shows when there's any energy (like your coal generator)
        long scaledEnergy = (long)energy * scale / maxEnergy;
        return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
    }

    // Energy status detection for status lights
    public EnergyStatus getEnergyStatus() {
        int current = getCurrentEnergy();
        int previous = getPreviousEnergy();
        int max = getMaxEnergy();

        if (current >= max) {
            return EnergyStatus.FULL;
        } else if (current > previous) {
            return EnergyStatus.CHARGING;
        } else if (current < previous) {
            return EnergyStatus.DISCHARGING;
        } else {
            return EnergyStatus.IDLE;
        }
    }

    // Energy bar color based on charge level
    public int getEnergyBarColor() {
        float percentage = getEnergyPercentage();
        if (percentage < 0.33f) {
            return 0xFFFF0000; // Red for low
        } else if (percentage < 0.66f) {
            return 0xFFFFFF00; // Yellow for medium
        } else {
            return 0xFF00FF00; // Green for high
        }
    }

    public enum EnergyStatus {
        CHARGING, DISCHARGING, FULL, IDLE
    }

    // Standard quickMoveStack (like your CoalGeneratorMenu pattern)
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            // Since energy storage has no item slots, just handle player inventory
            if (index < 36) { // Player inventory slots
                // Try to move to player hotbar/inventory
                if (!this.moveItemStackTo(stack, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From hotbar to inventory
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    // Standard stillValid (like your CoalGeneratorMenu)
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, blockEntity.getBlockState().getBlock());
    }

    // Standard player inventory methods (exactly like your CoalGeneratorMenu)
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}