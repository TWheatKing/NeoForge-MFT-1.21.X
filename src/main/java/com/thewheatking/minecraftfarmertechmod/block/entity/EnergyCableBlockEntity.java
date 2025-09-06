package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.energy.MftEnergyNetwork;
import com.thewheatking.minecraftfarmertechmod.energy.MftEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Block Entity for Energy Cables
 * Handles energy transfer and network discovery
 */
public class EnergyCableBlockEntity extends BlockEntity {

    private final IEnergyStorage energyStorage;
    private MftEnergyNetwork network;
    private int tickCounter = 0;

    public EnergyCableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ENERGY_CABLE.get(), pPos, pBlockState);

        // Cables have small buffer and high transfer rate
        this.energyStorage = new MftEnergyStorage(1000, 1000, 1000, 0);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, EnergyCableBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            pBlockEntity.serverTick(pLevel, pPos, pState);
        }
    }

    private void serverTick(Level pLevel, BlockPos pPos, BlockState pState) {
        tickCounter++;

        // Update network every 20 ticks (1 second)
        if (tickCounter % 20 == 0) {
            if (network == null) {
                network = new MftEnergyNetwork(pLevel);
            }
            network.discoverNetwork(pPos);
            network.distributeEnergy();
        }
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("energy", ((MftEnergyStorage)energyStorage).serializeNBT());
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("energy")) {
            ((MftEnergyStorage)energyStorage).deserializeNBT(pTag.getCompound("energy"));
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // Reset network when cable changes
        network = null;
    }
}