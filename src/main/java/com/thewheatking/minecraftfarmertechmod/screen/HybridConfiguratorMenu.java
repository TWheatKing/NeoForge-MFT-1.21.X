package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.HybridConfiguratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class HybridConfiguratorMenu extends BaseEnergyStorageMenu {

    private final HybridConfiguratorBlockEntity configuratorBlockEntity;

    public HybridConfiguratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(getMenuType(), containerId, playerInventory, extraData);
        this.configuratorBlockEntity = (HybridConfiguratorBlockEntity) blockEntity;
    }

    public HybridConfiguratorMenu(int containerId, Inventory playerInventory, HybridConfiguratorBlockEntity blockEntity) {
        super(getMenuType(), containerId, playerInventory, blockEntity);
        this.configuratorBlockEntity = blockEntity;
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
        return configuratorBlockEntity != null &&
                !configuratorBlockEntity.isRemoved() &&
                player.distanceToSqr(configuratorBlockEntity.getBlockPos().getX() + 0.5D,
                        configuratorBlockEntity.getBlockPos().getY() + 0.5D,
                        configuratorBlockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    public HybridConfiguratorBlockEntity getConfiguratorBlockEntity() {
        return configuratorBlockEntity;
    }
}