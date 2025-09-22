package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gold Cable Block Entity - Tier 2 Energy Transmission
 * Transfer Rate: 2048 FE/tick, Explodes at 8192+ FE/tick, Causes electrical damage
 */
public class GoldCableBlockEntity extends EnergyTransmissionBlockEntity {
    private static final int TRANSFER_RATE = 2048;
    private static final int EXPLOSION_THRESHOLD = 8192; // Tier 3 threshold
    private static final float ELECTRICAL_DAMAGE = 3.0f; // Stronger than copper

    public GoldCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.GOLD_CABLE.get(), pos, state, 0, TRANSFER_RATE, TRANSFER_RATE, 0);
    }

    @Override
    protected boolean canCauseElectricalDamage() { return true; }

    @Override
    protected void spawnEnergyFlowParticles() {
        // TODO: Gold particles for gold cables (yellow/golden color)
    }

    public int getTransferRate() { return TRANSFER_RATE; }
    public int getExplosionThreshold() { return EXPLOSION_THRESHOLD; }
    protected float getElectricalDamage() { return ELECTRICAL_DAMAGE; }
}