package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Diamond Cable Block Entity - Tier 3 (8192 FE/t)
 */
public class DiamondCableBlockEntity extends EnergyTransmissionBlockEntity {

    public DiamondCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.DIAMOND_CABLE.get(), pos, state, HybridEnergyStorage.TransferTier.DIAMOND);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn cyan/blue particles for diamond cables
        }
    }
}