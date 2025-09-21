package com.thewheatking.minecraftfarmertechmod.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Network Dashboard Menu for comprehensive network monitoring
 * Based on TWheatKing's MFT framework patterns
 * Minecraft 1.21 | NeoForge 21.0.167
 */
public class NetworkDashboardMenu extends AbstractContainerMenu {
    public final BlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public NetworkDashboardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(10));
    }

    public NetworkDashboardMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MENUS.get().get(), containerId);
        checkContainerDataCount(data, 10);
        this.blockEntity = entity;
        this.level = playerInventory.player.level();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(data);
    }

    public boolean isMonitoring() { return data.get(0) > 0; }
    public int getTotalProduction() { return data.get(1); }
    public int getTotalConsumption() { return data.get(2); }
    public int getNetworkLoad() { return data.get(3); }
    public int getStorageCapacity() { return data.get(4); }
    public int getStoredEnergy() { return data.get(5); }
    public int getActiveGenerators() { return data.get(6); }
    public int getActiveMachines() { return data.get(7); }
    public int getScaledEnergyLevel() {
        int energy = this.data.get(8);
        int maxEnergy = this.data.get(9);
        return maxEnergy != 0 ? energy * 52 / maxEnergy : 0;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, level.getBlockState(blockEntity.getBlockPos()).getBlock());
    }
}