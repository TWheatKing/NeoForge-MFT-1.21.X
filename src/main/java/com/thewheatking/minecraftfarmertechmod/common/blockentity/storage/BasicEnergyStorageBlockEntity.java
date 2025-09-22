package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Basic Energy Storage Block Entity - 50,000 FE Battery (Tier 1)
 * Based on TWheatKing's energy storage framework
 */
public class BasicEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public BasicEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.BASIC_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.BASIC);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.BASIC;
    }
}