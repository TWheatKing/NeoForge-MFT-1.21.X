package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Insulated Netherite Cable Block Entity - Tier 4 Enhanced (~43,690 FE/t) - EXPLOSION PROOF
 * Ultimate insulated energy transmission cable with even lower loss
 */
public class NetheriteCableInsulatedBlockEntity extends EnergyTransmissionBlockEntity {

    public NetheriteCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.NETHERITE_CABLE_INSULATED.get(), pos, state,
                HybridEnergyStorage.TransferTier.NETHERITE_INSULATED);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn very subtle dark purple particles for insulated netherite (minimal energy loss)
            // Implementation would go here for particle effects
        }
    }

    /**
     * Insulated Netherite cables never explode - override safety check
     */
    @Override
    protected boolean isEnergyLevelSafe(int energyAmount) {
        return true; // Netherite insulated is always safe, no matter the energy level
    }
}