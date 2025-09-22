package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Insulated Gold Cable Block Entity - Tier 2 Enhanced (2730 FE/t)
 */
public class GoldCableInsulatedBlockEntity extends EnergyTransmissionBlockEntity {

    public GoldCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.GOLD_CABLE_INSULATED.get(), pos, state,
                HybridEnergyStorage.TransferTier.GOLD_INSULATED);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn dimmer golden particles for insulated gold cables
        }
    }
}