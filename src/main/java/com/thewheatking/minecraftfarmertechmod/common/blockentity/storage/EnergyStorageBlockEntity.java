// ================== EnergyStorageBlockEntity.java ==================
// File: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/storage/EnergyStorageBlockEntity.java
// REPLACE your entire EnergyStorageBlockEntity.java with this:

package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * CORRECTED: Base Energy Storage Block Entity - Battery for storing electrical energy
 * Based on TWheatKing's original MFT framework
 */
public abstract class EnergyStorageBlockEntity extends BaseMachineBlockEntity {

    // Energy flow control - configurable through GUI
    protected boolean[] inputSides = new boolean[6];  // Which sides accept energy
    protected boolean[] outputSides = new boolean[6]; // Which sides provide energy

    // Energy flow tracking for GUI display
    protected int energyInputLastTick = 0;
    protected int energyOutputLastTick = 0;
    protected int energyStoredLastTick = 0;

    public EnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, HybridEnergyStorage.EnergyTier tier) {
        super(type, pos, state, tier.getCapacity(), tier.getMaxReceive(), tier.getMaxExtract(), 0);

        // Default configuration: all sides can input/output
        for (int i = 0; i < 6; i++) {
            inputSides[i] = true;
            outputSides[i] = true;
        }
    }

    @Override
    protected HybridEnergyStorage createEnergyStorage() {
        return new HybridEnergyStorage(getStorageTier());
    }

    @Override
    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(0); // Batteries don't need inventory
    }

    @Override
    protected boolean canOperate() {
        return true; // Batteries always operate - they just store and provide energy
    }

    @Override
    protected void performOperation() {
        // Batteries don't have active operations - they just store energy
    }

    @Override
    protected void serverTick() {
        super.serverTick();

        // Track energy changes for GUI display
        int currentEnergyStored = energyStorage.getEnergyStored();

        // Calculate energy flow this tick
        if (currentEnergyStored > energyStoredLastTick) {
            energyInputLastTick = currentEnergyStored - energyStoredLastTick;
            energyOutputLastTick = 0;
        } else if (currentEnergyStored < energyStoredLastTick) {
            energyInputLastTick = 0;
            energyOutputLastTick = energyStoredLastTick - currentEnergyStored;
        } else {
            energyInputLastTick = 0;
            energyOutputLastTick = 0;
        }

        energyStoredLastTick = currentEnergyStored;

        // Auto-distribute energy to connected cables/machines
        distributeEnergyToNeighbors();

        // Update active state based on energy level
        boolean wasActive = isActive;
        isActive = energyStorage.getEnergyStored() > 0;

        // Sync to client if state changed
        if (wasActive != isActive && tickCounter % 20 == 0) {
            setChanged();
            markUpdated();
        }
    }

    /**
     * Automatically distribute energy to neighboring machines/cables
     */
    private void distributeEnergyToNeighbors() {
        if (energyStorage.getEnergyStored() <= 0) return;

        // Try to send energy to all 6 directions
        for (Direction direction : Direction.values()) {
            if (!canOutputEnergy(direction)) continue;

            BlockPos neighborPos = worldPosition.relative(direction);

            // Get energy capability from neighbor
            IEnergyStorage neighborCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    neighborPos, direction.getOpposite());

            if (neighborCap != null && neighborCap.canReceive()) {
                // Calculate how much energy we can send
                int energyToSend = Math.min(
                        energyStorage.getEnergyStored(),
                        Math.min(energyMaxExtract, neighborCap.getMaxEnergyStored() - neighborCap.getEnergyStored())
                );

                if (energyToSend > 0) {
                    // Extract from our storage
                    int extracted = energyStorage.extractEnergy(energyToSend, false);

                    // Send to neighbor
                    int received = neighborCap.receiveEnergy(extracted, false);

                    // Return any energy that wasn't accepted
                    if (received < extracted) {
                        energyStorage.receiveEnergy(extracted - received, false);
                    }

                    if (received > 0) {
                        setChanged();
                        break; // Only send to one neighbor per tick for balanced distribution
                    }
                }
            }
        }
    }

    @Override
    protected boolean canOutputEnergy(Direction direction) {
        // Check if this side is configured for output
        int sideIndex = direction.ordinal();
        return outputSides[sideIndex] && energyStorage.getEnergyStored() > 0;
    }

    @Override
    protected boolean canInputEnergy(Direction direction) {
        // Check if this side is configured for input
        int sideIndex = direction.ordinal();
        return inputSides[sideIndex] && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }

    // Configuration methods for GUI
    public void toggleInputSide(Direction direction) {
        int sideIndex = direction.ordinal();
        inputSides[sideIndex] = !inputSides[sideIndex];
        markUpdated();
    }

    public void toggleOutputSide(Direction direction) {
        int sideIndex = direction.ordinal();
        outputSides[sideIndex] = !outputSides[sideIndex];
        markUpdated();
    }

    public boolean isInputSide(Direction direction) {
        return inputSides[direction.ordinal()];
    }

    public boolean isOutputSide(Direction direction) {
        return outputSides[direction.ordinal()];
    }

    // Set all sides to input/output/both/none
    public void setAllSides(boolean input, boolean output) {
        for (int i = 0; i < 6; i++) {
            inputSides[i] = input;
            outputSides[i] = output;
        }
        markUpdated();
    }

    // GUI Data Methods
    public int getCurrentEnergy() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return energyStorage.getMaxEnergyStored();
    }

    public float getEnergyPercentage() {
        if (energyStorage.getMaxEnergyStored() == 0) return 0.0f;
        return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
    }

    public int getEnergyInputRate() {
        return energyInputLastTick;
    }

    public int getEnergyOutputRate() {
        return energyOutputLastTick;
    }

    public int getTransferRate() {
        return energyMaxExtract;
    }

    public float getEnergyFillPercentage() {
        if (energyStorage.getMaxEnergyStored() == 0) return 0.0f;
        return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
    }

    public int getEnergyFillScaled(int scale) {
        if (energyStorage.getMaxEnergyStored() == 0) return 0;
        return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
    }

    public int getCapacity() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getMaxInputRate() {
        return energyMaxReceive;
    }

    public int getMaxOutputRate() {
        return energyMaxExtract;
    }

    // Helper method to get side configuration as string for GUI
    public String getSideConfigString(Direction direction) {
        boolean input = isInputSide(direction);
        boolean output = isOutputSide(direction);

        if (input && output) return "Both";
        if (input) return "Input";
        if (output) return "Output";
        return "None";
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        // Save side configurations
        for (int i = 0; i < 6; i++) {
            pTag.putBoolean("InputSide" + i, inputSides[i]);
            pTag.putBoolean("OutputSide" + i, outputSides[i]);
        }

        // Save energy flow tracking data
        pTag.putInt("EnergyInputLastTick", energyInputLastTick);
        pTag.putInt("EnergyOutputLastTick", energyOutputLastTick);
        pTag.putInt("EnergyStoredLastTick", energyStoredLastTick);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        // Load side configurations
        for (int i = 0; i < 6; i++) {
            if (pTag.contains("InputSide" + i)) {
                inputSides[i] = pTag.getBoolean("InputSide" + i);
            }
            if (pTag.contains("OutputSide" + i)) {
                outputSides[i] = pTag.getBoolean("OutputSide" + i);
            }
        }

        // Load energy flow tracking data
        energyInputLastTick = pTag.getInt("EnergyInputLastTick");
        energyOutputLastTick = pTag.getInt("EnergyOutputLastTick");
        energyStoredLastTick = pTag.getInt("EnergyStoredLastTick");
    }

    public abstract HybridEnergyStorage.EnergyTier getStorageTier();

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod." + getStorageTier().name().toLowerCase() + "_energy_storage");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // For now, return null - you can implement GUIs later
        return null;
    }

    @Override
    public String getDebugInfo() {
        return String.format("%s, Tier: %s, Energy: %d/%d FE (%.1f%%), I/O: %d/%d FE/t",
                super.getDebugInfo(),
                getStorageTier().name(),
                energyStorage.getEnergyStored(),
                energyStorage.getMaxEnergyStored(),
                getEnergyFillPercentage() * 100,
                getMaxInputRate(),
                getMaxOutputRate());
    }
}