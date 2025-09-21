package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyAnalyzerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class EnergyAnalyzerMenu extends BaseEnergyStorageMenu {

    private final EnergyAnalyzerBlockEntity analyzerBlockEntity;

    public EnergyAnalyzerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(getMenuType(), containerId, playerInventory, extraData);
        this.analyzerBlockEntity = (EnergyAnalyzerBlockEntity) blockEntity;
    }

    public EnergyAnalyzerMenu(int containerId, Inventory playerInventory, EnergyAnalyzerBlockEntity blockEntity) {
        super(getMenuType(), containerId, playerInventory, blockEntity);
        this.analyzerBlockEntity = blockEntity;
    }

    private static MenuType<?> getMenuType() {
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return analyzerBlockEntity != null &&
                !analyzerBlockEntity.isRemoved() &&
                player.distanceToSqr(analyzerBlockEntity.getBlockPos().getX() + 0.5D,
                        analyzerBlockEntity.getBlockPos().getY() + 0.5D,
                        analyzerBlockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    public EnergyAnalyzerBlockEntity getAnalyzerBlockEntity() {
        return analyzerBlockEntity;
    }
}