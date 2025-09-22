package com.thewheatking.minecraftfarmertechmod.energy;

import com.thewheatking.minecraftfarmertechmod.common.capabilities.energy.AdaptiveEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CORRECTED: Main hybrid energy storage system that manages both Forge Energy and MFT energy
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/energy/HybridEnergyStorage.java
 * Purpose: Core hybrid energy management system with cross-compatibility and optimization
 */
public class HybridEnergyStorage extends AdaptiveEnergyStorage {

    // Priority system for energy types
    public enum EnergyPriority {
        FORGE_ENERGY_FIRST,   // Prefer Forge Energy, fallback to MFT
        MFT_ENERGY_FIRST,     // Prefer MFT Energy, fallback to Forge Energy
        BALANCED,             // Use both equally
        EFFICIENCY_OPTIMIZED  // Automatically choose most efficient
    }

    // Energy tier system for different storage capacities
    public enum EnergyTier {
        BASIC(50000, 1000, 1000),           // 50k FE, 1k FE/t
        ENHANCED(200000, 5000, 5000),       // 200k FE, 5k FE/t
        ADVANCED(1000000, 20000, 20000),    // 1M FE, 20k FE/t
        SUPERIOR(5000000, 100000, 100000),  // 5M FE, 100k FE/t
        QUANTUM(25000000, 500000, 500000);  // 25M FE, 500k FE/t

        private final int capacity;
        private final int maxReceive;
        private final int maxExtract;

        EnergyTier(int capacity, int maxReceive, int maxExtract) {
            this.capacity = capacity;
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
        }

        public int getCapacity() { return capacity; }
        public int getMaxReceive() { return maxReceive; }
        public int getMaxExtract() { return maxExtract; }
    }

    // Transfer tier system for cables
    public enum TransferTier {
        // Copper Tier (Tier 1) - Basic energy transmission
        COPPER(512, 0.02, 2048),                           // 512 FE/t, 2% loss, explodes at 2048+ FE/t
        COPPER_INSULATED((int)(512 * 1.33), 0.01, 2048),  // ~682 FE/t, 1% loss, explodes at 2048+ FE/t

        // Gold Tier (Tier 2) - Improved energy transmission
        GOLD(2048, 0.015, 8192),                           // 2048 FE/t, 1.5% loss, explodes at 8192+ FE/t
        GOLD_INSULATED((int)(2048 * 1.33), 0.005, 8192),  // ~2730 FE/t, 0.5% loss, explodes at 8192+ FE/t

        // Diamond Tier (Tier 3) - High-end energy transmission
        DIAMOND(8192, 0.01, 32768),                        // 8192 FE/t, 1% loss, explodes at 32768+ FE/t
        DIAMOND_INSULATED((int)(8192 * 1.33), 0.001, 32768), // ~10922 FE/t, 0.1% loss, explodes at 32768+ FE/t

        // Netherite Tier (Tier 4) - Ultimate energy transmission - NEVER EXPLODES
        NETHERITE(32768, 0.005, -1),                       // 32768 FE/t, 0.5% loss, no explosion limit
        NETHERITE_INSULATED((int)(32768 * 1.33), 0.001, -1); // ~43690 FE/t, 0.1% loss, no explosion limit

        private final int transferRate;
        private final double energyLoss;
        private final int explosionThreshold; // -1 means never explodes

        TransferTier(int transferRate, double energyLoss, int explosionThreshold) {
            this.transferRate = transferRate;
            this.energyLoss = energyLoss;
            this.explosionThreshold = explosionThreshold;
        }

        public int getTransferRate() {
            return transferRate;
        }

        public double getEnergyLoss() {
            return energyLoss;
        }

        /**
         * Gets the explosion threshold for this cable tier
         * @return Energy threshold in FE/t, or -1 if cable never explodes
         */
        public int getExplosionThreshold() {
            return explosionThreshold;
        }

        /**
         * Checks if this cable tier can explode when overloaded
         * @return true if cable can explode, false if it's explosion-proof
         */
        public boolean canExplode() {
            return explosionThreshold > 0;
        }

        /**
         * Checks if the given energy amount would cause this cable to overload
         * @param energyAmount The energy being transmitted in FE/t
         * @return true if this energy level would cause explosion
         */
        public boolean isOverloaded(int energyAmount) {
            return canExplode() && energyAmount >= explosionThreshold;
        }
    }

    // Energy buffer zones for optimization
    private static final double LOW_ENERGY_THRESHOLD = 0.1;  // 10%
    private static final double HIGH_ENERGY_THRESHOLD = 0.9; // 90%

    // System configuration
    private EnergyPriority priority = EnergyPriority.BALANCED;
    private boolean autoConversion = true;
    private boolean energyBalancing = true;
    private double conversionEfficiency = 0.95; // 5% loss during conversion

    // Performance tracking
    private final Map<String, Long> operationTimings = new ConcurrentHashMap<>();
    private final Map<String, Integer> operationCounts = new ConcurrentHashMap<>();
    private long lastOptimizationTime = 0;
    private static final long OPTIMIZATION_INTERVAL = 1000; // 1 second

    // Energy flow tracking
    private int lastFeReceived = 0;
    private int lastFeExtracted = 0;
    private double lastMftReceived = 0;
    private double lastMftExtracted = 0;

    // Connected systems
    private final Set<IEnergyStorage> connectedFeStorages = new HashSet<>();
    private final Set<HybridEnergyStorage> connectedMftStorages = new HashSet<>();

    /**
     * Constructor using energy tier
     */
    public HybridEnergyStorage(EnergyTier tier) {
        super(tier.getCapacity(), tier.getMaxReceive(), tier.getMaxExtract(),
                tier.getCapacity() * getFeToMftRatio(),
                tier.getMaxReceive() * getFeToMftRatio(),
                tier.getMaxExtract() * getFeToMftRatio());
        setMode(EnergyMode.HYBRID);
    }

    /**
     * Constructor using transfer tier (for cables)
     */
    public HybridEnergyStorage(TransferTier tier) {
        super(tier.getTransferRate() * 2, tier.getTransferRate(), tier.getTransferRate(),
                tier.getTransferRate() * 2 * getFeToMftRatio(),
                tier.getTransferRate() * getFeToMftRatio(),
                tier.getTransferRate() * getFeToMftRatio());
        setMode(EnergyMode.HYBRID);
    }

    /**
     * Standard constructor for hybrid energy storage
     */
    public HybridEnergyStorage(int feCapacity, int feMaxReceive, int feMaxExtract,
                               double mftCapacity, double mftMaxReceive, double mftMaxExtract) {
        super(feCapacity, feMaxReceive, feMaxExtract, mftCapacity, mftMaxReceive, mftMaxExtract);
        setMode(EnergyMode.HYBRID);
    }

    /**
     * Constructor with priority setting
     */
    public HybridEnergyStorage(int feCapacity, int feMaxReceive, int feMaxExtract,
                               double mftCapacity, double mftMaxReceive, double mftMaxExtract,
                               EnergyPriority priority) {
        this(feCapacity, feMaxReceive, feMaxExtract, mftCapacity, mftMaxReceive, mftMaxExtract);
        this.priority = priority;
    }

    /**
     * Enhanced energy receiving with priority system
     */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        long startTime = System.nanoTime();

        try {
            int received = 0;

            switch (priority) {
                case FORGE_ENERGY_FIRST:
                    received = receiveForgeEnergyFirst(maxReceive, simulate);
                    break;
                case MFT_ENERGY_FIRST:
                    received = receiveMftEnergyFirst(maxReceive, simulate);
                    break;
                case BALANCED:
                    received = receiveBalanced(maxReceive, simulate);
                    break;
                case EFFICIENCY_OPTIMIZED:
                    received = receiveOptimized(maxReceive, simulate);
                    break;
            }

            if (!simulate) {
                lastFeReceived = received;
                triggerOptimization();
            }

            return received;

        } finally {
            recordOperation("receiveEnergy", startTime);
        }
    }

    /**
     * Enhanced energy extraction with priority system
     */
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long startTime = System.nanoTime();

        try {
            int extracted = 0;

            switch (priority) {
                case FORGE_ENERGY_FIRST:
                    extracted = extractForgeEnergyFirst(maxExtract, simulate);
                    break;
                case MFT_ENERGY_FIRST:
                    extracted = extractMftEnergyFirst(maxExtract, simulate);
                    break;
                case BALANCED:
                    extracted = extractBalanced(maxExtract, simulate);
                    break;
                case EFFICIENCY_OPTIMIZED:
                    extracted = extractOptimized(maxExtract, simulate);
                    break;
            }

            if (!simulate) {
                lastFeExtracted = extracted;
                triggerOptimization();
            }

            return extracted;

        } finally {
            recordOperation("extractEnergy", startTime);
        }
    }

    // Priority-based receive methods

    private int receiveForgeEnergyFirst(int maxReceive, boolean simulate) {
        // Try standard FE first
        int feReceived = super.receiveEnergy(maxReceive, simulate);

        if (feReceived < maxReceive && autoConversion) {
            // Convert remaining from MFT if available space
            int remaining = maxReceive - feReceived;
            double mftToConvert = remaining * getFeToMftRatio() * conversionEfficiency;
            double mftReceived = super.receiveMftEnergy(mftToConvert, simulate);
            int convertedFe = (int) (mftReceived * getMftToFeRatio());
            feReceived += convertedFe;
        }

        return feReceived;
    }

    private int receiveMftEnergyFirst(int maxReceive, boolean simulate) {
        // Convert to MFT and try MFT system first
        double mftToReceive = maxReceive * getFeToMftRatio();
        double mftReceived = super.receiveMftEnergy(mftToReceive, simulate);
        int feEquivalent = (int) (mftReceived * getMftToFeRatio());

        if (feEquivalent < maxReceive && autoConversion) {
            // Try standard FE for remaining
            int remaining = maxReceive - feEquivalent;
            int feReceived = super.receiveEnergy(remaining, simulate);
            feEquivalent += feReceived;
        }

        return feEquivalent;
    }

    private int receiveBalanced(int maxReceive, boolean simulate) {
        int halfReceive = maxReceive / 2;

        // Split equally between both systems
        int feReceived = super.receiveEnergy(halfReceive, simulate);
        double mftToReceive = halfReceive * getFeToMftRatio();
        double mftReceived = super.receiveMftEnergy(mftToReceive, simulate);
        int mftAsFeReceived = (int) (mftReceived * getMftToFeRatio());

        return feReceived + mftAsFeReceived;
    }

    private int receiveOptimized(int maxReceive, boolean simulate) {
        double currentRatio = getEnergyPercentage();

        if (currentRatio < LOW_ENERGY_THRESHOLD) {
            // Low energy - use fastest method
            return receiveForgeEnergyFirst(maxReceive, simulate);
        } else if (currentRatio > HIGH_ENERGY_THRESHOLD) {
            // High energy - use most efficient conversion
            return receiveMftEnergyFirst(maxReceive, simulate);
        } else {
            // Balanced energy - use balanced approach
            return receiveBalanced(maxReceive, simulate);
        }
    }

    // Priority-based extract methods

    private int extractForgeEnergyFirst(int maxExtract, boolean simulate) {
        int feExtracted = super.extractEnergy(maxExtract, simulate);

        if (feExtracted < maxExtract && autoConversion) {
            int remaining = maxExtract - feExtracted;
            double mftToExtract = remaining * getFeToMftRatio();
            double mftExtracted = super.extractMftEnergy(mftToExtract, simulate);
            int convertedFe = (int) (mftExtracted * getMftToFeRatio() * conversionEfficiency);
            feExtracted += convertedFe;
        }

        return feExtracted;
    }

    private int extractMftEnergyFirst(int maxExtract, boolean simulate) {
        double mftToExtract = maxExtract * getFeToMftRatio();
        double mftExtracted = super.extractMftEnergy(mftToExtract, simulate);
        int feEquivalent = (int) (mftExtracted * getMftToFeRatio());

        if (feEquivalent < maxExtract && autoConversion) {
            int remaining = maxExtract - feEquivalent;
            int feExtracted = super.extractEnergy(remaining, simulate);
            feEquivalent += feExtracted;
        }

        return feEquivalent;
    }

    private int extractBalanced(int maxExtract, boolean simulate) {
        int halfExtract = maxExtract / 2;

        int feExtracted = super.extractEnergy(halfExtract, simulate);
        double mftToExtract = halfExtract * getFeToMftRatio();
        double mftExtracted = super.extractMftEnergy(mftToExtract, simulate);
        int mftAsFeExtracted = (int) (mftExtracted * getMftToFeRatio());

        return feExtracted + mftAsFeExtracted;
    }

    private int extractOptimized(int maxExtract, boolean simulate) {
        double currentRatio = getEnergyPercentage();

        if (currentRatio > HIGH_ENERGY_THRESHOLD) {
            // High energy - extract from most abundant
            return extractBalanced(maxExtract, simulate);
        } else if (currentRatio < LOW_ENERGY_THRESHOLD) {
            // Low energy - preserve most efficient
            return extractMftEnergyFirst(maxExtract, simulate);
        } else {
            // Balanced energy
            return extractForgeEnergyFirst(maxExtract, simulate);
        }
    }

    // System Management Methods

    /**
     * Connects another energy storage to this hybrid system
     */
    public void connectEnergyStorage(IEnergyStorage storage) {
        if (storage instanceof HybridEnergyStorage hybrid) {
            connectedMftStorages.add(hybrid);
        } else {
            connectedFeStorages.add(storage);
        }
    }

    /**
     * Disconnects an energy storage from this hybrid system
     */
    public void disconnectEnergyStorage(IEnergyStorage storage) {
        if (storage instanceof HybridEnergyStorage hybrid) {
            connectedMftStorages.remove(hybrid);
        } else {
            connectedFeStorages.remove(storage);
        }
    }

    /**
     * Triggers system optimization if enough time has passed
     */
    private void triggerOptimization() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastOptimizationTime > OPTIMIZATION_INTERVAL) {
            optimizeSystem();
            lastOptimizationTime = currentTime;
        }
    }

    /**
     * Optimizes the hybrid energy system based on usage patterns
     */
    public void optimizeSystem() {
        // Analyze usage patterns
        analyzeUsagePatterns();

        // Optimize conversion efficiency
        optimizeConversionEfficiency();

        // Clean up old performance data
        cleanupPerformanceData();
    }

    private void analyzeUsagePatterns() {
        // Determine if FE or MFT is used more frequently
        int totalFeOperations = operationCounts.getOrDefault("receiveEnergy", 0) +
                operationCounts.getOrDefault("extractEnergy", 0);
        int totalMftOperations = operationCounts.getOrDefault("receiveMftEnergy", 0) +
                operationCounts.getOrDefault("extractMftEnergy", 0);

        if (priority == EnergyPriority.EFFICIENCY_OPTIMIZED) {
            if (totalFeOperations > totalMftOperations * 2) {
                // Heavy FE usage - optimize for FE
                conversionEfficiency = Math.min(0.98, conversionEfficiency + 0.01);
            } else if (totalMftOperations > totalFeOperations * 2) {
                // Heavy MFT usage - optimize for MFT
                conversionEfficiency = Math.min(0.98, conversionEfficiency + 0.01);
            }
        }
    }

    private void optimizeConversionEfficiency() {
        // Adjust conversion efficiency based on system load
        double energyRatio = getEnergyPercentage();

        if (energyRatio < LOW_ENERGY_THRESHOLD) {
            // Low energy - increase efficiency to conserve
            conversionEfficiency = Math.min(0.99, conversionEfficiency + 0.005);
        } else if (energyRatio > HIGH_ENERGY_THRESHOLD) {
            // High energy - can afford lower efficiency for speed
            conversionEfficiency = Math.max(0.90, conversionEfficiency - 0.005);
        }
    }

    private void cleanupPerformanceData() {
        if (operationCounts.size() > 1000) {
            operationCounts.clear();
            operationTimings.clear();
        }
    }

    private void recordOperation(String operation, long startTime) {
        long duration = System.nanoTime() - startTime;
        operationTimings.put(operation, duration);
        operationCounts.merge(operation, 1, Integer::sum);
    }

    // Configuration Methods

    public void setPriority(EnergyPriority priority) {
        this.priority = priority;
    }

    public EnergyPriority getPriority() {
        return priority;
    }

    public void setAutoConversion(boolean autoConversion) {
        this.autoConversion = autoConversion;
    }

    public boolean isAutoConversionEnabled() {
        return autoConversion;
    }

    public void setEnergyBalancing(boolean energyBalancing) {
        this.energyBalancing = energyBalancing;
    }

    public boolean isEnergyBalancingEnabled() {
        return energyBalancing;
    }

    public void setConversionEfficiency(double efficiency) {
        this.conversionEfficiency = Math.max(0.5, Math.min(1.0, efficiency));
    }

    public double getConversionEfficiency() {
        return conversionEfficiency;
    }

    // Performance Information

    public Map<String, Long> getOperationTimings() {
        return new HashMap<>(operationTimings);
    }

    public Map<String, Integer> getOperationCounts() {
        return new HashMap<>(operationCounts);
    }

    public int getConnectedStorageCount() {
        return connectedFeStorages.size() + connectedMftStorages.size();
    }

    // NBT Serialization

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putInt("energy", getEnergyStored());
        tag.putDouble("mftEnergy", getMftEnergyStored());
        tag.putString("priority", priority.name());
        tag.putBoolean("autoConversion", autoConversion);
        tag.putBoolean("energyBalancing", energyBalancing);
        tag.putDouble("conversionEfficiency", conversionEfficiency);
        tag.putString("mode", getMode().name());

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        setEnergyStored(tag.getInt("energy"));
        setMftEnergyStored(tag.getDouble("mftEnergy"));

        if (tag.contains("priority")) {
            try {
                priority = EnergyPriority.valueOf(tag.getString("priority"));
            } catch (IllegalArgumentException e) {
                priority = EnergyPriority.BALANCED;
            }
        }

        autoConversion = tag.getBoolean("autoConversion");
        energyBalancing = tag.getBoolean("energyBalancing");
        conversionEfficiency = tag.getDouble("conversionEfficiency");

        if (tag.contains("mode")) {
            try {
                setMode(EnergyMode.valueOf(tag.getString("mode")));
            } catch (IllegalArgumentException e) {
                setMode(EnergyMode.HYBRID);
            }
        }
    }
}