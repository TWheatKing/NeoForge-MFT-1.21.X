package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Netherite Cable Block Entity - Tier 4 Ultimate Energy Transmission
 * Transfer Rate: 32768 FE/tick, NEVER EXPLODES (unlimited capacity), Causes electrical damage
 */
public class NetheriteCableBlockEntity extends EnergyTransmissionBlockEntity {
    private static final int TRANSFER_RATE = 32768;
    private static final int EXPLOSION_THRESHOLD = Integer.MAX_VALUE; // Never explodes!
    private static final float ELECTRICAL_DAMAGE = 5.0f; // Maximum electrical damage

    public NetheriteCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.NETHERITE_CABLE.get(), pos, state, 0, TRANSFER_RATE, TRANSFER_RATE, 0);
    }

    @Override
    protected boolean canCauseElectricalDamage() { return true; }

    @Override
    protected void checkForOverload() {
        // Netherite cables never overload or explode
        overloadTicks = 0;
    }

    @Override
    protected void spawnEnergyFlowParticles() {
        // TODO: Netherite particles for netherite cables (dark purple/black color)
    }

    public int getTransferRate() { return TRANSFER_RATE; }
    public int getExplosionThreshold() { return EXPLOSION_THRESHOLD; }
    protected float getElectricalDamage() { return ELECTRICAL_DAMAGE; }

    @Override
    public boolean isOverloaded() { return false; } // Never overloaded
}