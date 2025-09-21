package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copper Cable Block Entity - Tier 1 (512 FE/t)
 */
public class CopperCableBlockEntity extends EnergyTransmissionBlockEntity {

    public CopperCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.COPPER_CABLE.get(), pos, state, HybridEnergyStorage.TransferTier.COPPER);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn orange particles for copper cables
            // Implementation would go here for particle effects
        }
    }
}