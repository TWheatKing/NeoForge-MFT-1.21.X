package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Insulated Diamond Cable Block Entity - Tier 3 Enhanced (16384 FE/t)
 */
public class DiamondCableInsulatedBlockEntity extends EnergyTransmissionBlockEntity {

    public DiamondCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.DIAMOND_CABLE_INSULATED.get(), pos, state,
                HybridEnergyStorage.TransferTier.DIAMOND_INSULATED);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn subtle cyan particles for insulated diamond cables (minimal energy loss)
            // Implementation would go here for particle effects
        }
    }
}