package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;

import static com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities.ENERGY_ANALYZER;

public class EnergyAnalyzerBlockEntity extends BaseMachineBlockEntity {
    public EnergyAnalyzerBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(ENERGY_ANALYZER.get(), pos, state, 10000, 1000, 1000, 9);
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
        return net.minecraft.network.chat.Component.translatable("block.minecraftfarmertechmod.energy_analyzer");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory, net.minecraft.world.entity.player.Player player) {
        return null; // TODO: Implement menu
    }
}