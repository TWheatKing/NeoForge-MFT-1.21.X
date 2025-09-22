package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Superior Energy Storage Block Entity - 5,000,000 FE Battery (Tier 4)
 * Based on TWheatKing's energy storage framework
 */
public class SuperiorEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public SuperiorEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.SUPERIOR_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.SUPERIOR);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.SUPERIOR;
    }
}