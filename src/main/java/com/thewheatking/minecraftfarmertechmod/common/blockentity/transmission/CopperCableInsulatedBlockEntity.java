package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copper Cable Insulated Block Entity - Tier 1 Safe Energy Transmission
 *
 * WHAT IT DOES:
 * • Insulated version of copper cable with enhanced safety features
 * • Transfer Rate: 1024 FE/tick (double the standard copper cable)
 * • NO electrical damage to players/entities (insulated)
 * • Explodes if receiving energy from Tier 2+ (1024+ FE/tick)
 * • No energy loss over distance
 * • Same explosion threshold as standard copper cable
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/transmission/CopperCableInsulatedBlockEntity.java
 */
public class CopperCableInsulatedBlockEntity extends CopperCableBlockEntity {

    // Insulated Copper Cable Specifications
    private static final int INSULATED_TRANSFER_RATE = 682; // 33% faster than standard

    public CopperCableInsulatedBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        // Update the energy storage to handle higher transfer rate
        // Note: Constructor will still use standard rate, need to override in createEnergyStorage
    }

    @Override
    protected void initializeEnergyStorage() {
        // Override to use insulated transfer rate
        energyStorage = createEnergyStorage();
        energyMaxReceive = INSULATED_TRANSFER_RATE;
        energyMaxExtract = INSULATED_TRANSFER_RATE;
    }

    @Override
    protected boolean canCauseElectricalDamage() {
        return false; // Insulated cables don't cause electrical damage
    }

    @Override
    public int getTransferRate() {
        return INSULATED_TRANSFER_RATE;
    }

    // ========== VISUAL EFFECTS OVERRIDES ==========

    @Override
    protected void spawnEnergyFlowParticles() {
        // TODO: Different particle effects for insulated cables
        // Maybe blue particles instead of orange to show insulation
    }

    @Override
    protected void spawnElectricalSparks() {
        // Insulated cables don't spark (or spark much less frequently)
        if (level != null && level.isClientSide() && level.random.nextFloat() < 0.01f) {
            // Very rare, small sparks
            super.spawnElectricalSparks();
        }
    }
}