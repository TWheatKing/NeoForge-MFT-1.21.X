package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Energy Converter Block Entity - Dual-Mode Power Converter
 *
 * WHAT IT DOES:
 * MODE 1 - Cable Tier Protection: Steps down high-tier energy to prevent lower cable explosions
 * MODE 2 - Energy Type Conversion: Converts between FE (electrical) and Stress Units (rotational)
 *
 * • Cable Protection: High-tier input → Safe lower-tier output (prevents cable overload)
 * • FE to Rotational: Electrical energy → Mechanical rotational power (stress units)
 * • Rotational to FE: Mechanical power → Electrical energy
 * • GUI mode switching between protection and conversion modes
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/EnergyConverterBlockEntity.java
 */
public class EnergyConverterBlockEntity extends BaseMachineBlockEntity {

    // Energy Converter Specifications
    private static final int ENERGY_CAPACITY = 75000;        // 75,000 FE capacity
    private static final int ENERGY_TRANSFER_RATE = 2500;    // 2,500 FE/tick transfer
    private static final int INVENTORY_SIZE = 18;            // Upgrade/component slots

    // Conversion Modes
    public enum ConversionMode {
        CABLE_PROTECTION("Cable Protection", "Protects lower-tier cables from overload"),
        FE_TO_ROTATIONAL("FE → Rotational", "Converts electrical energy to rotational power"),
        ROTATIONAL_TO_FE("Rotational → FE", "Converts rotational power to electrical energy");

        private final String displayName;
        private final String description;

        ConversionMode(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    // Current operating mode
    private ConversionMode currentMode = ConversionMode.CABLE_PROTECTION;

    // Cable Protection Mode Settings
    private int inputTierLimit = 10000;      // Max input FE/tick before protection kicks in
    private int outputTierLimit = 1000;     // Max output FE/tick to protect lower cables
    private boolean protectionActive = false;

    // Rotational Power System (Create-like but standalone)
    private int stressUnits = 0;             // Current rotational power stored
    private int maxStressUnits = 32000;      // Max rotational power capacity
    private int rotationalSpeed = 0;         // RPM (rotations per minute)
    private int maxRotationalSpeed = 256;    // Max RPM
    private boolean rotationalActive = false;

    // Conversion Rates
    private static final int FE_TO_SU_RATIO = 2;     // 1 FE = 2 Stress Units
    private static final int SU_TO_FE_RATIO = 1;     // 2 Stress Units = 1 FE
    private static final int CONVERSION_EFFICIENCY = 95; // 95% efficiency (5% loss)

    // Operation State
    private boolean isConverting = false;
    private int conversionProgress = 0;
    private int maxConversionTime = 40;      // 2 seconds per conversion cycle

    // Statistics
    private long totalEnergyConverted = 0L;
    private long totalStressConverted = 0L;
    private int conversionsPerformed = 0;

    public EnergyConverterBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.ENERGY_CONVERTER.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, INVENTORY_SIZE);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        // Perform conversion based on current mode
        switch (currentMode) {
            case CABLE_PROTECTION -> performCableProtection();
            case FE_TO_ROTATIONAL -> performFEToRotationalConversion();
            case ROTATIONAL_TO_FE -> performRotationalToFEConversion();
        }

        // Update working state
        boolean wasWorking = isWorking;
        isWorking = isConverting;

        // Sync to client if state changed
        if (wasWorking != isWorking && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Cable Protection Mode: Steps down high energy to safe levels
     */
    private void performCableProtection() {
        protectionActive = false;

        // Check if we're receiving high-tier energy that needs protection
        int energyInput = getEnergyInputThisTick();

        if (energyInput > inputTierLimit) {
            protectionActive = true;
            isConverting = true;

            // Limit output to safe levels for lower-tier cables
            int safeOutput = Math.min(energyInput, outputTierLimit);

            // Store excess energy or dissipate it safely
            int excessEnergy = energyInput - safeOutput;
            if (excessEnergy > 0) {
                // Store what we can, dissipate the rest as "heat loss"
                int storedEnergy = Math.min(excessEnergy,
                        energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
                energyStorage.receiveEnergy(storedEnergy, false);

                // The rest is safely dissipated (heat loss)
                totalEnergyConverted += (excessEnergy - storedEnergy);
            }

            conversionProgress++;
            if (conversionProgress >= maxConversionTime) {
                conversionProgress = 0;
                conversionsPerformed++;
            }
        } else {
            isConverting = false;
            conversionProgress = 0;
        }
    }

    /**
     * FE to Rotational Mode: Convert electrical energy to rotational power
     */
    private void performFEToRotationalConversion() {
        if (energyStorage.getEnergyStored() >= FE_TO_SU_RATIO &&
                stressUnits < maxStressUnits) {

            isConverting = true;
            conversionProgress++;

            if (conversionProgress >= maxConversionTime) {
                // Perform conversion with efficiency loss
                int feToConvert = Math.min(energyStorage.getEnergyStored(),
                        ENERGY_TRANSFER_RATE);
                int suGenerated = (feToConvert * FE_TO_SU_RATIO * CONVERSION_EFFICIENCY) / 100;

                // Consume FE and generate SU
                energyStorage.extractEnergy(feToConvert, false);
                stressUnits = Math.min(maxStressUnits, stressUnits + suGenerated);

                // Update rotational speed based on stress
                rotationalSpeed = Math.min(maxRotationalSpeed,
                        (stressUnits * maxRotationalSpeed) / maxStressUnits);
                rotationalActive = stressUnits > 0;

                // Update statistics
                totalEnergyConverted += feToConvert;
                totalStressConverted += suGenerated;
                conversionsPerformed++;

                conversionProgress = 0;
            }
        } else {
            isConverting = false;
            conversionProgress = 0;

            // Gradual slowdown when not converting
            if (rotationalSpeed > 0) {
                rotationalSpeed = Math.max(0, rotationalSpeed - 2);
                rotationalActive = rotationalSpeed > 0;
            }
        }
    }

    /**
     * Rotational to FE Mode: Convert rotational power to electrical energy
     */
    private void performRotationalToFEConversion() {
        if (stressUnits >= SU_TO_FE_RATIO &&
                energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {

            isConverting = true;
            conversionProgress++;

            if (conversionProgress >= maxConversionTime) {
                // Perform conversion with efficiency loss
                int suToConvert = Math.min(stressUnits, ENERGY_TRANSFER_RATE * FE_TO_SU_RATIO);
                int feGenerated = (suToConvert / FE_TO_SU_RATIO * CONVERSION_EFFICIENCY) / 100;

                // Consume SU and generate FE
                stressUnits = Math.max(0, stressUnits - suToConvert);
                energyStorage.receiveEnergy(feGenerated, false);

                // Update rotational speed
                rotationalSpeed = Math.min(maxRotationalSpeed,
                        (stressUnits * maxRotationalSpeed) / maxStressUnits);
                rotationalActive = stressUnits > 0;

                // Update statistics
                totalStressConverted += suToConvert;
                totalEnergyConverted += feGenerated;
                conversionsPerformed++;

                conversionProgress = 0;
            }
        } else {
            isConverting = false;
            conversionProgress = 0;

            // Gradual slowdown
            if (rotationalSpeed > 0) {
                rotationalSpeed = Math.max(0, rotationalSpeed - 1);
                rotationalActive = rotationalSpeed > 0;
            }
        }
    }

    /**
     * Get energy input this tick (for cable protection mode)
     */
    private int getEnergyInputThisTick() {
        // This would need to be tracked by monitoring energy changes
        // For now, estimate based on energy storage fill rate
        return Math.min(ENERGY_TRANSFER_RATE,
                energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
    }

    @Override
    protected boolean canOperate() {
        return energyStorage.getEnergyStored() > 0 || stressUnits > 0;
    }

    @Override
    protected void performOperation() {
        // Main operation logic is handled in serverTick
        // This method is called by the base class
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.energy_converter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.EnergyConverterMenu(containerId, playerInventory, this);
    }

    // ========== MODE CONTROL ==========

    /**
     * Switch to next conversion mode
     */
    public void switchMode() {
        ConversionMode[] modes = ConversionMode.values();
        int nextIndex = (currentMode.ordinal() + 1) % modes.length;
        currentMode = modes[nextIndex];

        // Reset state when switching modes
        isConverting = false;
        conversionProgress = 0;
        protectionActive = false;

        setChanged();
        syncToClient();
    }

    /**
     * Set specific conversion mode
     */
    public void setMode(ConversionMode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            isConverting = false;
            conversionProgress = 0;
            protectionActive = false;
            setChanged();
            syncToClient();
        }
    }

    // ========== GUI DATA METHODS ==========

    public ConversionMode getCurrentMode() { return currentMode; }
    public boolean isProtectionActive() { return protectionActive; }
    public boolean isRotationalActive() { return rotationalActive; }
    public boolean isConverting() { return isConverting; }

    public int getStressUnits() { return stressUnits; }
    public int getMaxStressUnits() { return maxStressUnits; }
    public int getRotationalSpeed() { return rotationalSpeed; }
    public int getMaxRotationalSpeed() { return maxRotationalSpeed; }

    public int getInputTierLimit() { return inputTierLimit; }
    public int getOutputTierLimit() { return outputTierLimit; }

    public float getConversionProgress() {
        return maxConversionTime == 0 ? 0.0f : (float) conversionProgress / maxConversionTime;
    }

    public float getStressPercentage() {
        return maxStressUnits == 0 ? 0.0f : (float) stressUnits / maxStressUnits;
    }

    public float getRotationalSpeedPercentage() {
        return maxRotationalSpeed == 0 ? 0.0f : (float) rotationalSpeed / maxRotationalSpeed;
    }

    public long getTotalEnergyConverted() { return totalEnergyConverted; }
    public long getTotalStressConverted() { return totalStressConverted; }
    public int getConversionsPerformed() { return conversionsPerformed; }

    public int getConversionRate() {
        return switch (currentMode) {
            case FE_TO_ROTATIONAL -> ENERGY_TRANSFER_RATE;
            case ROTATIONAL_TO_FE -> ENERGY_TRANSFER_RATE / FE_TO_SU_RATIO;
            case CABLE_PROTECTION -> outputTierLimit;
        };
    }

    public int getConversionEfficiency() { return CONVERSION_EFFICIENCY; }

    // ========== CONFIGURATION ==========

    public void setInputTierLimit(int limit) {
        this.inputTierLimit = Math.max(1000, Math.min(50000, limit));
        setChanged();
    }

    public void setOutputTierLimit(int limit) {
        this.outputTierLimit = Math.max(100, Math.min(10000, limit));
        setChanged();
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save conversion state
        tag.putString("ConversionMode", currentMode.name());
        tag.putBoolean("IsConverting", isConverting);
        tag.putInt("ConversionProgress", conversionProgress);

        // Save cable protection settings
        tag.putInt("InputTierLimit", inputTierLimit);
        tag.putInt("OutputTierLimit", outputTierLimit);
        tag.putBoolean("ProtectionActive", protectionActive);

        // Save rotational power state
        tag.putInt("StressUnits", stressUnits);
        tag.putInt("RotationalSpeed", rotationalSpeed);
        tag.putBoolean("RotationalActive", rotationalActive);

        // Save statistics
        tag.putLong("TotalEnergyConverted", totalEnergyConverted);
        tag.putLong("TotalStressConverted", totalStressConverted);
        tag.putInt("ConversionsPerformed", conversionsPerformed);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load conversion state
        try {
            currentMode = ConversionMode.valueOf(tag.getString("ConversionMode"));
        } catch (IllegalArgumentException e) {
            currentMode = ConversionMode.CABLE_PROTECTION;
        }
        isConverting = tag.getBoolean("IsConverting");
        conversionProgress = tag.getInt("ConversionProgress");

        // Load cable protection settings
        inputTierLimit = tag.getInt("InputTierLimit");
        outputTierLimit = tag.getInt("OutputTierLimit");
        protectionActive = tag.getBoolean("ProtectionActive");

        // Load rotational power state
        stressUnits = tag.getInt("StressUnits");
        rotationalSpeed = tag.getInt("RotationalSpeed");
        rotationalActive = tag.getBoolean("RotationalActive");

        // Load statistics
        totalEnergyConverted = tag.getLong("TotalEnergyConverted");
        totalStressConverted = tag.getLong("TotalStressConverted");
        conversionsPerformed = tag.getInt("ConversionsPerformed");
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send conversion data to client for GUI updates
        tag.putString("Mode", currentMode.name());
        tag.putBoolean("Converting", isConverting);
        tag.putInt("Progress", conversionProgress);
        tag.putBoolean("ProtectionActive", protectionActive);
        tag.putBoolean("RotationalActive", rotationalActive);
        tag.putInt("StressUnits", stressUnits);
        tag.putInt("RotationalSpeed", rotationalSpeed);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive conversion data from server
        try {
            currentMode = ConversionMode.valueOf(tag.getString("Mode"));
        } catch (IllegalArgumentException e) {
            currentMode = ConversionMode.CABLE_PROTECTION;
        }
        isConverting = tag.getBoolean("Converting");
        conversionProgress = tag.getInt("Progress");
        protectionActive = tag.getBoolean("ProtectionActive");
        rotationalActive = tag.getBoolean("RotationalActive");
        stressUnits = tag.getInt("StressUnits");
        rotationalSpeed = tag.getInt("RotationalSpeed");
    }

    // ========== ROTATIONAL POWER SYSTEM ==========

    /**
     * Add stress units from external rotational sources
     */
    public int receiveStress(int stressAmount, boolean simulate) {
        int received = Math.min(stressAmount, maxStressUnits - stressUnits);
        if (!simulate) {
            stressUnits += received;
            rotationalSpeed = Math.min(maxRotationalSpeed,
                    (stressUnits * maxRotationalSpeed) / maxStressUnits);
            rotationalActive = stressUnits > 0;
            setChanged();
        }
        return received;
    }

    /**
     * Extract stress units to external rotational consumers
     */
    public int extractStress(int stressAmount, boolean simulate) {
        int extracted = Math.min(stressAmount, stressUnits);
        if (!simulate) {
            stressUnits -= extracted;
            rotationalSpeed = Math.min(maxRotationalSpeed,
                    (stressUnits * maxRotationalSpeed) / maxStressUnits);
            rotationalActive = stressUnits > 0;
            setChanged();
        }
        return extracted;
    }

    /**
     * Check if this converter can connect to rotational networks
     */
    public boolean canConnectRotational(Direction side) {
        return currentMode == ConversionMode.FE_TO_ROTATIONAL ||
                currentMode == ConversionMode.ROTATIONAL_TO_FE;
    }
}