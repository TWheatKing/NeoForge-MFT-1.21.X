package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Insulated Copper Cable Block Entity - Tier 1 Enhanced (682 FE/t)
 */
public class CopperCableInsulatedBlockEntity extends EnergyTransmissionBlockEntity {

    public CopperCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.COPPER_CABLE_INSULATED.get(), pos, state,
                HybridEnergyStorage.TransferTier.COPPER_INSULATED);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn dimmer orange particles for insulated copper cables
        }
    }
}