package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Enhanced Energy Storage Block Entity - 200,000 FE Battery (Tier 2)
 * Based on TWheatKing's energy storage framework
 */
public class EnhancedEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public EnhancedEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.ENHANCED_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.ENHANCED);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.ENHANCED;
    }
}