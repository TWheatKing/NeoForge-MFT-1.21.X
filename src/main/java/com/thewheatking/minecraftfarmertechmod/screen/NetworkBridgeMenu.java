package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkBridgeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class NetworkBridgeMenu extends BaseEnergyStorageMenu {

    private final NetworkBridgeBlockEntity bridgeBlockEntity;

    public NetworkBridgeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(getMenuType(), containerId, playerInventory, extraData);
        this.bridgeBlockEntity = (NetworkBridgeBlockEntity) blockEntity;
    }

    public NetworkBridgeMenu(int containerId, Inventory playerInventory, NetworkBridgeBlockEntity blockEntity) {
        super(getMenuType(), containerId, playerInventory, blockEntity);
        this.bridgeBlockEntity = blockEntity;
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
        return bridgeBlockEntity != null &&
                !bridgeBlockEntity.isRemoved() &&
                player.distanceToSqr(bridgeBlockEntity.getBlockPos().getX() + 0.5D,
                        bridgeBlockEntity.getBlockPos().getY() + 0.5D,
                        bridgeBlockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    public NetworkBridgeBlockEntity getBridgeBlockEntity() {
        return bridgeBlockEntity;
    }
}