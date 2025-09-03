package com.thewheatking.minecraftfarmertechmod.energy;

import net.minecraft.nbt.CompoundTag;

/**
 * Default implementation of IEnergyStorage.
 * This is the basic energy storage implementation that most blocks will use.
 */
public class EnergyStorage implements IEnergyStorage {

    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    /**
     * Sets the energy stored to the given amount
     */
    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    /**
     * Gets the percentage of energy stored (0.0 to 1.0)
     */
    public float getEnergyStoredPercentage() {
        return capacity > 0 ? (float) energy / (float) capacity : 0.0f;
    }

    /**
     * Serializes the energy data to NBT
     */
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Energy", energy);
        return nbt;
    }

    /**
     * Deserializes the energy data from NBT
     */
    public void deserializeNBT(CompoundTag nbt) {
        setEnergyStored(nbt.getInt("Energy"));
    }
}