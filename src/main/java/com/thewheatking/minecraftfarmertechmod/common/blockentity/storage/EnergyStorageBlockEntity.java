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
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * CORRECTED: Base Energy Storage Block Entity - Battery for storing electrical energy
 * Accepts energy from generators/cables and provides it to machines
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/storage/EnergyStorageBlockEntity.java
 * Purpose: Base class for all energy storage devices with configurable input/output sides
 */
public abstract class EnergyStorageBlockEntity extends BaseMachineBlockEntity {

    // Energy flow control - configurable through GUI
    protected boolean[] inputSides = new boolean[6];  // Which sides accept energy
    protected boolean[] outputSides = new boolean[6]; // Which sides provide energy

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
        // Batteries don't need inventory (no item charging for now)
        return new ItemStackHandler(0);
    }

    @Override
    protected boolean canOperate() {
        // Batteries always operate - they just store and provide energy
        return true;
    }

    @Override
    protected void performOperation() {
        // Batteries don't have active operations - they just store energy
        // Energy distribution is handled by the base class
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

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalData(tag, registries);

        // Save side configurations
        for (int i = 0; i < 6; i++) {
            tag.putBoolean("InputSide" + i, inputSides[i]);
            tag.putBoolean("OutputSide" + i, outputSides[i]);
        }
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditionalData(tag, registries);

        // Load side configurations
        for (int i = 0; i < 6; i++) {
            if (tag.contains("InputSide" + i)) {
                inputSides[i] = tag.getBoolean("InputSide" + i);
            }
            if (tag.contains("OutputSide" + i)) {
                outputSides[i] = tag.getBoolean("OutputSide" + i);
            }
        }
    }

    // Getters for GUI and information
    public float getEnergyFillPercentage() {
        if (energyStorage.getMaxEnergyStored() == 0) return 0.0f;
        return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
    }

    public int getEnergyFillScaled(int scale) {
        if (energyStorage.getMaxEnergyStored() == 0) return 0;
        return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
    }

    public abstract HybridEnergyStorage.EnergyTier getStorageTier();

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
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod." + getStorageTier().name().toLowerCase() + "_energy_storage");
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