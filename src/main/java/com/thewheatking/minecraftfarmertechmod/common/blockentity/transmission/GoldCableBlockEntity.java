package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gold Cable Block Entity - Tier 2 (2048 FE/t)
 */
public class GoldCableBlockEntity extends EnergyTransmissionBlockEntity {

    public GoldCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.GOLD_CABLE.get(), pos, state, HybridEnergyStorage.TransferTier.GOLD);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn golden particles for gold cables
        }
    }
}