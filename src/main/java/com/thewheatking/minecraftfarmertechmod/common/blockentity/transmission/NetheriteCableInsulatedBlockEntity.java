package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Netherite Cable Insulated Block Entity - Tier 4 Ultimate Safe Energy Transmission
 * Transfer Rate: 43690 FE/tick (33% faster), No electrical damage, NEVER EXPLODES
 */
public class NetheriteCableInsulatedBlockEntity extends NetheriteCableBlockEntity {
    private static final int INSULATED_TRANSFER_RATE = 43690; // 33% faster than standard netherite

    public NetheriteCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
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
        // TODO: Insulated netherite particles (maybe blue-tinted dark purple particles)
    }
}