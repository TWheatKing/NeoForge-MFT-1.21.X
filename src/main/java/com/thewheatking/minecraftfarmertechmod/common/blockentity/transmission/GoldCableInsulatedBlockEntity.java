package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Gold Cable Insulated Block Entity - Tier 2 Safe Energy Transmission
 * Transfer Rate: 2730 FE/tick (33% faster), No electrical damage, Explodes at 8192+ FE/tick
 */
public class GoldCableInsulatedBlockEntity extends GoldCableBlockEntity {
    private static final int INSULATED_TRANSFER_RATE = 2730; // 33% faster than standard gold

    public GoldCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
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
        // TODO: Insulated gold particles (maybe blue-tinted gold particles)
    }
}