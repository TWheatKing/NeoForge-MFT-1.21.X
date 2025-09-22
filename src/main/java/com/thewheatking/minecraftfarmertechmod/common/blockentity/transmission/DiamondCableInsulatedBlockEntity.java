package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Diamond Cable Insulated Block Entity - Tier 3 Safe Energy Transmission
 * Transfer Rate: 10922 FE/tick (33% faster), No electrical damage, Explodes at 32768+ FE/tick
 */
public class DiamondCableInsulatedBlockEntity extends DiamondCableBlockEntity {
    private static final int INSULATED_TRANSFER_RATE = 10922; // 33% faster than standard diamond

    public DiamondCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.DIAMOND_CABLE_INSULATED.get(), pos, state, 0, TRANSFER_RATE, TRANSFER_RATE, 0);
    }

    @Override
    protected void initializeEnergyStorage() {
        energyStorage = createEnergyStorage();
        energyMaxReceive = INSULATED_TRANSFER_RATE;
        energyMaxExtract = INSULATED_TRANSFER_RATE;
    }

    @Override
    protected boolean canCauseElectricalDamage() { return false; }

    @Override
    public int getTransferRate() { return INSULATED_TRANSFER_RATE; }

    @Override
    protected void spawnEnergyFlowParticles() {
        // TODO: Insulated diamond particles (maybe blue-tinted cyan particles)
    }
}
