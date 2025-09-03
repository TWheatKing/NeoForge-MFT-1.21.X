package com.thewheatking.minecraftfarmertechmod.energy;

/**
 * Core interface for all energy storage and handling in the mod.
 * Similar to Forge's IEnergyStorage but designed for our specific needs.
 */
public interface IEnergyStorage {

    /**
     * Adds energy to the storage and returns the amount of energy that was accepted.
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate If true, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted.
     */
    int receiveEnergy(int maxReceive, boolean simulate);

    /**
     * Removes energy from the storage and returns the amount of energy that was removed.
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate If true, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted.
     */
    int extractEnergy(int maxExtract, boolean simulate);

    /**
     * Returns the amount of energy currently stored.
     */
    int getEnergyStored();

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    int getMaxEnergyStored();

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    boolean canExtract();

    /**
     * Returns if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    boolean canReceive();
}