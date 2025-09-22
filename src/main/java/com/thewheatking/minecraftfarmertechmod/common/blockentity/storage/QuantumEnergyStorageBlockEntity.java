package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Quantum Energy Storage Block Entity - 25,000,000 FE Battery (Tier 5)
 * Based on TWheatKing's energy storage framework
 */
public class QuantumEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public QuantumEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.QUANTUM_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.QUANTUM);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.QUANTUM;
    }
}