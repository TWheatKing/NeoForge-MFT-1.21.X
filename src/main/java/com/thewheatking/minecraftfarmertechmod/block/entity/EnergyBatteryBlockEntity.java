package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.energy.EnergyStorage;
import com.thewheatking.minecraftfarmertechmod.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Block Entity for Energy Battery
 * Stores large amounts of electrical energy
 */
public class EnergyBatteryBlockEntity extends BlockEntity {

    // Large energy storage: 100,000 RF capacity, fast input/output
    private final IEnergyStorage energyStorage = new EnergyStorage(100000, 500, 500, 0);

    public EnergyBatteryBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ENERGY_BATTERY.get(), pPos, pBlockState);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("energy", ((EnergyStorage)energyStorage).serializeNBT());
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("energy")) {
            ((EnergyStorage)energyStorage).deserializeNBT(pTag.getCompound("energy"));
        }
    }
}