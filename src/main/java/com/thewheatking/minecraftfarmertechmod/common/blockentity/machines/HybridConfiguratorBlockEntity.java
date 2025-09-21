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
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.EnumMap;
import java.util.Map;

/**
 * Hybrid Configurator Block Entity - Smart Energy Distributor (Tier 4)
 *
 * WHAT IT DOES:
 * • ONE INPUT (bottom face only) - accepts high-tier energy
 * • FIVE OUTPUTS (top, north, south, east, west) - each configurable to different cable tiers
 * • GUI configuration for each output face's cable tier limit
 * • Prevents cable explosions by stepping down energy to appropriate levels
 * • Tier 4 specs: 5,000,000 FE capacity, 10,000 FE/tick transfer
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/HybridConfiguratorBlockEntity.java
 */
public class HybridConfiguratorBlockEntity extends BaseMachineBlockEntity {

    // Tier 4 (Superior) Specifications
    private static final int ENERGY_CAPACITY = 5000000;      // 5,000,000 FE capacity
    private static final int ENERGY_TRANSFER_RATE = 10000;   // 10,000 FE/tick transfer
    private static final int INVENTORY_SIZE = 9;             // Upgrade/component slots

    // Input/Output Configuration
    private static final Direction INPUT_FACE = Direction.DOWN;  // Bottom face input only
    private static final Direction[] OUTPUT_FACES = {
            Direction.UP,     // Top
            Direction.NORTH,  // North
            Direction.SOUTH,  // South
            Direction.EAST,   // East
            Direction.WEST    // West
    };

    // Cable Tier Definitions
    public enum CableTier {
        COPPER_BASIC("Copper Basic", 1000, "Low-tier copper cable"),
        COPPER_INSULATED("Copper Insulated", 2000, "Insulated copper cable"),
        GOLD_BASIC("Gold Basic", 5000, "Mid-tier gold cable"),
        GOLD_INSULATED("Gold Insulated", 10000, "Insulated gold cable"),
        DIAMOND_BASIC("Diamond Basic", 25000, "High-tier diamond cable"),
        DIAMOND_INSULATED("Diamond Insulated", 50000, "Insulated diamond cable"),
        NETHERITE("Netherite", 100000, "Ultra high-tier netherite cable"),
        NO_LIMIT("No Limit", Integer.MAX_VALUE, "No energy limit");

        private final String displayName;
        private final int maxTransfer;
        private final String description;

        CableTier(String displayName, int maxTransfer, String description) {
            this.displayName = displayName;
            this.maxTransfer = maxTransfer;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxTransfer() { return maxTransfer; }
        public String getDescription() { return description; }
    }

    // Face Configuration (each output face has a cable tier setting)
    private final Map<Direction, CableTier> faceConfigurations = new EnumMap<>(Direction.class);
    private final Map<Direction, Boolean> faceEnabled = new EnumMap<>(Direction.class);

    // Energy Distribution State
    private final Map<Direction, Integer> faceEnergyOutput = new EnumMap<>(Direction.class);
    private int totalEnergyDistributed = 0;
    private boolean isDistributing = false;

    // Distribution Statistics
    private long totalEnergyProcessed = 0L;
    private final Map<Direction, Long> faceEnergyTotals = new EnumMap<>(Direction.class);
    private int distributionCycles = 0;

    public HybridConfiguratorBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.HYBRID_CONFIGURATOR.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, INVENTORY_SIZE);

        initializeFaceConfigurations();
    }

    /**
     * Initialize face configurations with default settings
     */
    private void initializeFaceConfigurations() {
        for (Direction face : OUTPUT_FACES) {
            faceConfigurations.put(face, CableTier.COPPER_BASIC); // Default to basic tier
            faceEnabled.put(face, true);                          // Enable all faces by default
            faceEnergyOutput.put(face, 0);
            faceEnergyTotals.put(face, 0L);
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        // Perform energy distribution if we have energy
        if (energyStorage.getEnergyStored() > 0) {
            performEnergyDistribution();
        } else {
            isDistributing = false;
            totalEnergyDistributed = 0;

            // Reset face outputs
            for (Direction face : OUTPUT_FACES) {
                faceEnergyOutput.put(face, 0);
            }
        }

        // Update working state
        boolean wasWorking = isWorking;
        isWorking = isDistributing;

        // Sync to client if state changed
        if (wasWorking != isWorking && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Distribute energy to configured output faces
     */
    private void performEnergyDistribution() {
        isDistributing = false;
        totalEnergyDistributed = 0;

        // Calculate total demand from all enabled faces
        int totalDemand = 0;
        Map<Direction, Integer> faceDemands = new EnumMap<>(Direction.class);

        for (Direction face : OUTPUT_FACES) {
            if (!faceEnabled.get(face)) {
                faceEnergyOutput.put(face, 0);
                continue;
            }

            // Check if there's a device connected to this face
            BlockPos neighborPos = worldPosition.relative(face);
            var neighborCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    neighborPos, face.getOpposite());

            if (neighborCap != null && neighborCap.canReceive()) {
                // Calculate demand based on face tier limit and neighbor capacity
                CableTier faceTier = faceConfigurations.get(face);
                int maxFaceTransfer = Math.min(faceTier.getMaxTransfer(), ENERGY_TRANSFER_RATE);
                int neighborDemand = neighborCap.receiveEnergy(maxFaceTransfer, true);

                int faceDemand = Math.min(maxFaceTransfer, neighborDemand);
                faceDemands.put(face, faceDemand);
                totalDemand += faceDemand;
            } else {
                faceDemands.put(face, 0);
                faceEnergyOutput.put(face, 0);
            }
        }

        // Distribute energy proportionally if we have demand
        if (totalDemand > 0) {
            isDistributing = true;
            distributionCycles++;

            int availableEnergy = Math.min(energyStorage.getEnergyStored(), ENERGY_TRANSFER_RATE);

            for (Direction face : OUTPUT_FACES) {
                if (!faceEnabled.get(face) || faceDemands.get(face) == 0) {
                    faceEnergyOutput.put(face, 0);
                    continue;
                }

                // Calculate proportional energy allocation
                int faceDemand = faceDemands.get(face);
                int faceAllocation = Math.min(faceDemand,
                        (int) ((long) availableEnergy * faceDemand / totalDemand));

                // Send energy to the connected device
                BlockPos neighborPos = worldPosition.relative(face);
                var neighborCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                        neighborPos, face.getOpposite());

                if (neighborCap != null && neighborCap.canReceive()) {
                    int energySent = neighborCap.receiveEnergy(faceAllocation, false);

                    if (energySent > 0) {
                        // Extract energy from our storage
                        energyStorage.extractEnergy(energySent, false);

                        // Track statistics
                        faceEnergyOutput.put(face, energySent);
                        faceEnergyTotals.put(face, faceEnergyTotals.get(face) + energySent);
                        totalEnergyDistributed += energySent;
                        totalEnergyProcessed += energySent;
                    } else {
                        faceEnergyOutput.put(face, 0);
                    }
                } else {
                    faceEnergyOutput.put(face, 0);
                }
            }
        }
    }

    @Override
    protected boolean canOperate() {
        return energyStorage.getEnergyStored() > 0;
    }

    @Override
    protected void performOperation() {
        // Main operation logic is handled in serverTick
    }

    @Override
    public boolean canConnectEnergy(Direction side) {
        // Only bottom face can receive energy
        return side == INPUT_FACE;
    }

    @Override
    public boolean canReceiveEnergy(Direction side) {
        // Only bottom face can receive energy
        return side == INPUT_FACE;
    }

    @Override
    public boolean canExtractEnergy(Direction side) {
        // Output faces can provide energy (handled by distribution logic)
        return side != INPUT_FACE && faceEnabled.getOrDefault(side, false);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.hybrid_configurator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.HybridConfiguratorMenu(containerId, playerInventory, this);
    }

    // ========== FACE CONFIGURATION METHODS ==========

    /**
     * Set cable tier for a specific face
     */
    public void setFaceTier(Direction face, CableTier tier) {
        if (face != INPUT_FACE && tier != null) {
            faceConfigurations.put(face, tier);
            setChanged();
            syncToClient();
        }
    }

    /**
     * Get cable tier for a specific face
     */
    public CableTier getFaceTier(Direction face) {
        return faceConfigurations.getOrDefault(face, CableTier.COPPER_BASIC);
    }

    /**
     * Enable/disable a specific face
     */
    public void setFaceEnabled(Direction face, boolean enabled) {
        if (face != INPUT_FACE) {
            faceEnabled.put(face, enabled);
            if (!enabled) {
                faceEnergyOutput.put(face, 0);
            }
            setChanged();
            syncToClient();
        }
    }

    /**
     * Check if a face is enabled
     */
    public boolean isFaceEnabled(Direction face) {
        return faceEnabled.getOrDefault(face, false);
    }

    /**
     * Cycle to next tier for a face
     */
    public void cycleFaceTier(Direction face) {
        if (face == INPUT_FACE) return;

        CableTier currentTier = getFaceTier(face);
        CableTier[] tiers = CableTier.values();
        int nextIndex = (currentTier.ordinal() + 1) % tiers.length;
        setFaceTier(face, tiers[nextIndex]);
    }

    // ========== GUI DATA METHODS ==========

    public Direction[] getOutputFaces() { return OUTPUT_FACES.clone(); }
    public Direction getInputFace() { return INPUT_FACE; }

    public boolean isDistributing() { return isDistributing; }
    public int getTotalEnergyDistributed() { return totalEnergyDistributed; }
    public long getTotalEnergyProcessed() { return totalEnergyProcessed; }
    public int getDistributionCycles() { return distributionCycles; }

    public int getFaceEnergyOutput(Direction face) {
        return faceEnergyOutput.getOrDefault(face, 0);
    }

    public long getFaceEnergyTotal(Direction face) {
        return faceEnergyTotals.getOrDefault(face, 0L);
    }

    public CableTier[] getAllTiers() { return CableTier.values(); }

    public Map<Direction, CableTier> getAllFaceConfigurations() {
        return new EnumMap<>(faceConfigurations);
    }

    public Map<Direction, Boolean> getAllFaceEnabledStates() {
        return new EnumMap<>(faceEnabled);
    }

    public String getFaceDisplayName(Direction face) {
        return switch (face) {
            case UP -> "Top";
            case DOWN -> "Bottom (Input)";
            case NORTH -> "North";
            case SOUTH -> "South";
            case EAST -> "East";
            case WEST -> "West";
        };
    }

    public float getDistributionEfficiency() {
        if (totalEnergyProcessed == 0) return 100.0f;
        // Calculate efficiency based on successful distributions
        return Math.min(100.0f, (float) totalEnergyDistributed / ENERGY_TRANSFER_RATE * 100.0f);
    }

    // ========== RESET AND UTILITY METHODS ==========

    public void resetStatistics() {
        totalEnergyProcessed = 0L;
        distributionCycles = 0;
        for (Direction face : OUTPUT_FACES) {
            faceEnergyTotals.put(face, 0L);
        }
        setChanged();
    }

    public void resetAllFacesToDefault() {
        for (Direction face : OUTPUT_FACES) {
            faceConfigurations.put(face, CableTier.COPPER_BASIC);
            faceEnabled.put(face, true);
        }
        setChanged();
        syncToClient();
    }

    public void disableAllFaces() {
        for (Direction face : OUTPUT_FACES) {
            faceEnabled.put(face, false);
        }
        setChanged();
        syncToClient();
    }

    public void enableAllFaces() {
        for (Direction face : OUTPUT_FACES) {
            faceEnabled.put(face, true);
        }
        setChanged();
        syncToClient();
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save face configurations
        CompoundTag faceConfigTag = new CompoundTag();
        CompoundTag faceEnabledTag = new CompoundTag();
        CompoundTag faceTotalsTag = new CompoundTag();

        for (Direction face : OUTPUT_FACES) {
            faceConfigTag.putString(face.name(), faceConfigurations.get(face).name());
            faceEnabledTag.putBoolean(face.name(), faceEnabled.get(face));
            faceTotalsTag.putLong(face.name(), faceEnergyTotals.get(face));
        }

        tag.put("FaceConfigurations", faceConfigTag);
        tag.put("FaceEnabled", faceEnabledTag);
        tag.put("FaceTotals", faceTotalsTag);

        // Save distribution state
        tag.putBoolean("IsDistributing", isDistributing);
        tag.putInt("TotalEnergyDistributed", totalEnergyDistributed);
        tag.putLong("TotalEnergyProcessed", totalEnergyProcessed);
        tag.putInt("DistributionCycles", distributionCycles);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load face configurations
        if (tag.contains("FaceConfigurations")) {
            CompoundTag faceConfigTag = tag.getCompound("FaceConfigurations");
            for (Direction face : OUTPUT_FACES) {
                if (faceConfigTag.contains(face.name())) {
                    try {
                        CableTier tier = CableTier.valueOf(faceConfigTag.getString(face.name()));
                        faceConfigurations.put(face, tier);
                    } catch (IllegalArgumentException e) {
                        faceConfigurations.put(face, CableTier.COPPER_BASIC);
                    }
                }
            }
        }

        if (tag.contains("FaceEnabled")) {
            CompoundTag faceEnabledTag = tag.getCompound("FaceEnabled");
            for (Direction face : OUTPUT_FACES) {
                faceEnabled.put(face, faceEnabledTag.getBoolean(face.name()));
            }
        }

        if (tag.contains("FaceTotals")) {
            CompoundTag faceTotalsTag = tag.getCompound("FaceTotals");
            for (Direction face : OUTPUT_FACES) {
                faceEnergyTotals.put(face, faceTotalsTag.getLong(face.name()));
            }
        }

        // Load distribution state
        isDistributing = tag.getBoolean("IsDistributing");
        totalEnergyDistributed = tag.getInt("TotalEnergyDistributed");
        totalEnergyProcessed = tag.getLong("TotalEnergyProcessed");
        distributionCycles = tag.getInt("DistributionCycles");
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send configuration data to client for GUI updates
        tag.putBoolean("Distributing", isDistributing);
        tag.putInt("TotalDistributed", totalEnergyDistributed);

        // Send face states
        for (Direction face : OUTPUT_FACES) {
            tag.putString("Tier_" + face.name(), faceConfigurations.get(face).name());
            tag.putBoolean("Enabled_" + face.name(), faceEnabled.get(face));
            tag.putInt("Output_" + face.name(), faceEnergyOutput.get(face));
        }
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive configuration data from server
        isDistributing = tag.getBoolean("Distributing");
        totalEnergyDistributed = tag.getInt("TotalDistributed");

        // Receive face states
        for (Direction face : OUTPUT_FACES) {
            if (tag.contains("Tier_" + face.name())) {
                try {
                    CableTier tier = CableTier.valueOf(tag.getString("Tier_" + face.name()));
                    faceConfigurations.put(face, tier);
                } catch (IllegalArgumentException e) {
                    faceConfigurations.put(face, CableTier.COPPER_BASIC);
                }
            }

            faceEnabled.put(face, tag.getBoolean("Enabled_" + face.name()));
            faceEnergyOutput.put(face, tag.getInt("Output_" + face.name()));
        }
    }
}