package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;

import static com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities.NETWORK_DASHBOARD;

public class NetworkDashboardBlockEntity extends BaseMachineBlockEntity {
    public NetworkDashboardBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(NETWORK_DASHBOARD.get(), pos, state, 25000, 2000, 2000, 18);
        // Implementation needed
    }

    @Override
    protected boolean canOperate() {
        return false;
    }

    @Override
    protected void performOperation() {

    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("block.minecraftfarmertechmod.network_dashboard");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory, net.minecraft.world.entity.player.Player player) {
        return null; // TODO: Implement menu
    }
}
