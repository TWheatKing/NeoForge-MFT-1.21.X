package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Diamond Cable Block Entity - Tier 3 Energy Transmission
 * Transfer Rate: 8192 FE/tick, Explodes at 32768+ FE/tick, Causes electrical damage
 */
public class DiamondCableBlockEntity extends EnergyTransmissionBlockEntity {
    private static final int TRANSFER_RATE = 8192;
    private static final int EXPLOSION_THRESHOLD = 32768; // Tier 4 threshold
    private static final float ELECTRICAL_DAMAGE = 4.0f; // Even stronger electrical damage

    public DiamondCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.DIAMOND_CABLE.get(), pos, state, 0, TRANSFER_RATE, TRANSFER_RATE, 0);
    }

    @Override
    protected boolean canCauseElectricalDamage() { return true; }

    @Override
    protected void spawnEnergyFlowParticles() {
        // TODO: Diamond particles for diamond cables (cyan/light blue color)
    }

    public int getTransferRate() { return TRANSFER_RATE; }
    public int getExplosionThreshold() { return EXPLOSION_THRESHOLD; }
    protected float getElectricalDamage() { return ELECTRICAL_DAMAGE; }
}