package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Quantum Energy Storage - Tier 5 (25M FE capacity)
 */
public class QuantumEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public QuantumEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.QUANTUM_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.QUANTUM);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.QUANTUM;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // return new QuantumEnergyStorageMenu(containerId, playerInventory, this);
        return null; // Placeholder until menu is implemented
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.quantum_energy_storage");
    }
}