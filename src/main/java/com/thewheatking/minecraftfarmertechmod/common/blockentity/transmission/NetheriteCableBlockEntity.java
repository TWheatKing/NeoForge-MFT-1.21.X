package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Netherite Cable Block Entity - Tier 4 (32,768 FE/t) - EXPLOSION PROOF
 * Ultimate energy transmission cable that never explodes regardless of energy load
 */
public class NetheriteCableBlockEntity extends EnergyTransmissionBlockEntity {

    public NetheriteCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.NETHERITE_CABLE.get(), pos, state, HybridEnergyStorage.TransferTier.NETHERITE);
    }

    @Override
    protected void spawnTransmissionParticles() {
        if (level != null && level.isClientSide() && isTransmitting()) {
            // Spawn purple/dark particles for netherite cables - very subtle since they're premium
            // Implementation would go here for particle effects
        }
    }

    /**
     * Netherite cables never explode - override safety check
     */
    @Override
    protected boolean isEnergyLevelSafe(int energyAmount) {
        return true; // Netherite is always safe, no matter the energy level
    }
}