package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Enhanced Energy Storage Menu - 200k FE capacity, 5k FE/t transfer
 * Based on TWheatKing's MFT framework patterns (copied from BasicEnergyStorageMenu)
 * Minecraft 1.21 | NeoForge 21.0.167
 */
public class EnhancedEnergyStorageMenu extends AbstractContainerMenu {
    public final EnhancedEnergyStorageBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public EnhancedEnergyStorageMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public EnhancedEnergyStorageMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ENHANCED_ENERGY_STORAGE_MENU.get(), pContainerId);
        blockEntity = ((EnhancedEnergyStorageBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    // Energy status methods (same as Basic, just different capacity values)
    public boolean isActive() { return data.get(0) > 0; }
    public int getCurrentEnergy() { return data.get(0); }
    public int getMaxEnergy() { return data.get(1); }
    public int getPreviousEnergy() { return data.get(2); }

    public float getEnergyPercentage() {
        int max = getMaxEnergy();
        if (max == 0) return 0.0f;
        return (float) getCurrentEnergy() / max;
    }

    public int getScaledEnergy(int scale) {
        int energy = getCurrentEnergy();
        int maxEnergy = getMaxEnergy();
        if (maxEnergy == 0) return 0;
        long scaledEnergy = (long)energy * scale / maxEnergy;
        return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
    }

    public EnergyStatus getEnergyStatus() {
        int current = getCurrentEnergy();
        int previous = getPreviousEnergy();
        int max = getMaxEnergy();

        if (current >= max) return EnergyStatus.FULL;
        else if (current > previous) return EnergyStatus.CHARGING;
        else if (current < previous) return EnergyStatus.DISCHARGING;
        else return EnergyStatus.IDLE;
    }

    // Enhanced tier uses blue color scheme instead of red/yellow/green
    public int getEnergyBarColor() {
        float percentage = getEnergyPercentage();
        if (percentage < 0.33f) return 0xFF0066CC; // Dark blue for low
        else if (percentage < 0.66f) return 0xFF0099FF; // Medium blue for medium
        else return 0xFF00CCFF; // Light blue for high
    }

    public enum EnergyStatus { CHARGING, DISCHARGING, FULL, IDLE }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < 36) {
                if (!this.moveItemStackTo(stack, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, blockEntity.getBlockState().getBlock());
    }

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