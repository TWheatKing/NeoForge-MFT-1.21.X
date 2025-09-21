package com.thewheatking.minecraftfarmertechmod.common.capabilities.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * CORRECTED: Adaptive energy storage implementation that can switch between different energy systems
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/capabilities/energy/AdaptiveEnergyStorage.java
 * Purpose: Provides adaptive energy storage that can work with both Forge Energy and MFT systems
 */
public class AdaptiveEnergyStorage implements IEnergyStorage {

    private int energy;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    // MFT compatibility fields
    private double mftEnergy;
    private final double mftCapacity;
    private final double mftMaxReceive;
    private final double mftMaxExtract;

    // Conversion rates between FE and MFT
    private static final double FE_TO_MFT_RATIO = 0.25; // 1 FE = 0.25 MFT
    private static final double MFT_TO_FE_RATIO = 4.0;  // 1 MFT = 4 FE

    // Operating mode
    private EnergyMode mode = EnergyMode.FORGE_ENERGY;

    public enum EnergyMode {
        FORGE_ENERGY,  // Standard Forge Energy (FE)
        MFT_ENERGY,    // Minecraft Farmer Tech Energy
        HYBRID         // Both systems simultaneously
    }

    /**
     * Constructor for Forge Energy only
     */
    public AdaptiveEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = 0;

        // Calculate MFT equivalents
        this.mftCapacity = capacity * FE_TO_MFT_RATIO;
        this.mftMaxReceive = maxReceive * FE_TO_MFT_RATIO;
        this.mftMaxExtract = maxExtract * FE_TO_MFT_RATIO;
        this.mftEnergy = 0;
    }

    /**
     * Constructor for hybrid mode with separate FE and MFT values
     */
    public AdaptiveEnergyStorage(int feCapacity, int feMaxReceive, int feMaxExtract,
                                 double mftCapacity, double mftMaxReceive, double mftMaxExtract) {
        this.capacity = feCapacity;
        this.maxReceive = feMaxReceive;
        this.maxExtract = feMaxExtract;
        this.energy = 0;

        this.mftCapacity = mftCapacity;
        this.mftMaxReceive = mftMaxReceive;
        this.mftMaxExtract = mftMaxExtract;
        this.mftEnergy = 0;

        this.mode = EnergyMode.HYBRID;
    }

    /**
     * Sets the operating mode for this energy storage
     */
    public void setMode(EnergyMode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            synchronizeEnergy();
        }
    }

    /**
     * Gets the current operating mode
     */
    public EnergyMode getMode() {
        return mode;
    }

    /**
     * Synchronizes energy values when switching modes
     */
    private void synchronizeEnergy() {
        switch (mode) {
            case MFT_ENERGY:
                // Convert FE to MFT
                mftEnergy = energy * FE_TO_MFT_RATIO;
                break;
            case FORGE_ENERGY:
                // Convert MFT to FE
                energy = (int) (mftEnergy * MFT_TO_FE_RATIO);
                break;
            case HYBRID:
                // Keep both values in sync based on the higher one
                double feAsMft = energy * FE_TO_MFT_RATIO;
                double mftAsFe = mftEnergy * MFT_TO_FE_RATIO;

                if (feAsMft > mftEnergy) {
                    mftEnergy = feAsMft;
                } else if (mftAsFe > energy) {
                    energy = (int) mftAsFe;
                }
                break;
        }
    }

    // Standard IEnergyStorage implementation

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            if (mode == EnergyMode.HYBRID || mode == EnergyMode.MFT_ENERGY) {
                mftEnergy += energyReceived * FE_TO_MFT_RATIO;
                mftEnergy = Math.min(mftEnergy, mftCapacity);
            }
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
            if (mode == EnergyMode.HYBRID || mode == EnergyMode.MFT_ENERGY) {
                mftEnergy -= energyExtracted * FE_TO_MFT_RATIO;
                mftEnergy = Math.max(mftEnergy, 0);
            }
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        switch (mode) {
            case MFT_ENERGY:
                return (int) (mftEnergy * MFT_TO_FE_RATIO);
            case FORGE_ENERGY:
            case HYBRID:
            default:
                return energy;
        }
    }

    @Override
    public int getMaxEnergyStored() {
        switch (mode) {
            case MFT_ENERGY:
                return (int) (mftCapacity * MFT_TO_FE_RATIO);
            case FORGE_ENERGY:
            case HYBRID:
            default:
                return capacity;
        }
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    // MFT Energy System Methods

    /**
     * Receives MFT energy
     */
    public double receiveMftEnergy(double maxReceive, boolean simulate) {
        if (!canReceiveMft()) {
            return 0;
        }

        double energyReceived = Math.min(mftCapacity - mftEnergy, Math.min(this.mftMaxReceive, maxReceive));

        if (!simulate) {
            mftEnergy += energyReceived;
            if (mode == EnergyMode.HYBRID || mode == EnergyMode.FORGE_ENERGY) {
                energy += (int) (energyReceived * MFT_TO_FE_RATIO);
                energy = Math.min(energy, capacity);
            }
        }

        return energyReceived;
    }

    /**
     * Extracts MFT energy
     */
    public double extractMftEnergy(double maxExtract, boolean simulate) {
        if (!canExtractMft()) {
            return 0;
        }

        double energyExtracted = Math.min(mftEnergy, Math.min(this.mftMaxExtract, maxExtract));

        if (!simulate) {
            mftEnergy -= energyExtracted;
            if (mode == EnergyMode.HYBRID || mode == EnergyMode.FORGE_ENERGY) {
                energy -= (int) (energyExtracted * MFT_TO_FE_RATIO);
                energy = Math.max(energy, 0);
            }
        }

        return energyExtracted;
    }

    /**
     * Gets stored MFT energy
     */
    public double getMftEnergyStored() {
        switch (mode) {
            case FORGE_ENERGY:
                return energy * FE_TO_MFT_RATIO;
            case MFT_ENERGY:
            case HYBRID:
            default:
                return mftEnergy;
        }
    }

    /**
     * Gets maximum MFT energy capacity
     */
    public double getMaxMftEnergyStored() {
        switch (mode) {
            case FORGE_ENERGY:
                return capacity * FE_TO_MFT_RATIO;
            case MFT_ENERGY:
            case HYBRID:
            default:
                return mftCapacity;
        }
    }

    /**
     * Checks if can extract MFT energy
     */
    public boolean canExtractMft() {
        return mftMaxExtract > 0;
    }

    /**
     * Checks if can receive MFT energy
     */
    public boolean canReceiveMft() {
        return mftMaxReceive > 0;
    }

    // Utility Methods

    /**
     * Sets the energy directly (for loading from NBT)
     */
    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
        if (mode == EnergyMode.HYBRID || mode == EnergyMode.MFT_ENERGY) {
            this.mftEnergy = this.energy * FE_TO_MFT_RATIO;
        }
    }

    /**
     * Sets the MFT energy directly (for loading from NBT)
     */
    public void setMftEnergyStored(double mftEnergy) {
        this.mftEnergy = Math.max(0, Math.min(mftCapacity, mftEnergy));
        if (mode == EnergyMode.HYBRID || mode == EnergyMode.FORGE_ENERGY) {
            this.energy = (int) (this.mftEnergy * MFT_TO_FE_RATIO);
        }
    }

    /**
     * Gets the energy fill percentage (0.0 to 1.0)
     */
    public double getEnergyPercentage() {
        return switch (mode) {
            case MFT_ENERGY -> mftEnergy / mftCapacity;
            case FORGE_ENERGY, HYBRID -> (double) energy / capacity;
        };
    }

    /**
     * Checks if the storage is full
     */
    public boolean isFull() {
        return switch (mode) {
            case MFT_ENERGY -> mftEnergy >= mftCapacity;
            case FORGE_ENERGY, HYBRID -> energy >= capacity;
        };
    }

    /**
     * Checks if the storage is empty
     */
    public boolean isEmpty() {
        return switch (mode) {
            case MFT_ENERGY -> mftEnergy <= 0;
            case FORGE_ENERGY, HYBRID -> energy <= 0;
        };
    }

    /**
     * Gets energy as a formatted string for display
     */
    public String getEnergyDisplayString() {
        return switch (mode) {
            case MFT_ENERGY -> String.format("%.1f / %.1f MFT", mftEnergy, mftCapacity);
            case FORGE_ENERGY -> String.format("%,d / %,d FE", energy, capacity);
            case HYBRID -> String.format("%,d FE (%.1f MFT) / %,d FE (%.1f MFT)",
                    energy, mftEnergy, capacity, mftCapacity);
        };
    }

    /**
     * Converts FE to MFT
     */
    public static double convertFeToMft(int fe) {
        return fe * FE_TO_MFT_RATIO;
    }

    /**
     * Converts MFT to FE
     */
    public static int convertMftToFe(double mft) {
        return (int) (mft * MFT_TO_FE_RATIO);
    }

    /**
     * Gets the current conversion ratio from FE to MFT
     */
    public static double getFeToMftRatio() {
        return FE_TO_MFT_RATIO;
    }

    /**
     * Gets the current conversion ratio from MFT to FE
     */
    public static double getMftToFeRatio() {
        return MFT_TO_FE_RATIO;
    }
}