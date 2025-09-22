package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Advanced Energy Storage Block Entity - 1,000,000 FE Battery (Tier 3)
 * Based on TWheatKing's energy storage framework
 */
public class AdvancedEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public AdvancedEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.ADVANCED_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.ADVANCED);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.ADVANCED;
    }
}