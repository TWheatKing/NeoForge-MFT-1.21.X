package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

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
 * Basic Energy Storage Block Entity - Simple 50,000 FE Battery
 *
 * WHAT IT DOES:
 * • Stores energy (50,000 FE capacity)
 * • Receives energy through any type of cable (all sides)
 * • Provides energy out into any type of cable (all sides)
 * • Simple battery - no complex features
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/storage/BasicEnergyStorageBlockEntity.java
 */
public class BasicEnergyStorageBlockEntity extends BaseMachineBlockEntity {

    // Basic Energy Storage Specifications
    private static final int ENERGY_CAPACITY = 50000;        // 50,000 FE total storage
    private static final int ENERGY_TRANSFER_RATE = 1000;    // 1,000 FE/tick in/out

    // Energy flow tracking for GUI display
    private int energyInputLastTick = 0;
    private int energyOutputLastTick = 0;
    private int energyStoredLastTick = 0;

    public BasicEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.BASIC_ENERGY_STORAGE.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, 0); // No inventory slots
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

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
        if (wasActive != isActive && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Automatically distribute energy to neighboring machines/cables
     */
    private void distributeEnergyToNeighbors() {
        if (energyStorage.getEnergyStored() <= 0) return;

        // Try to send energy to all 6 directions
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);

            // Get energy capability from neighbor
            var neighborCap = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                    neighborPos, direction.getOpposite());

            if (neighborCap != null && neighborCap.canReceive()) {
                // Calculate how much energy we can send
                int energyToSend = Math.min(
                        energyStorage.getEnergyStored(),
                        Math.min(ENERGY_TRANSFER_RATE, neighborCap.getMaxEnergyStored() - neighborCap.getEnergyStored())
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
    protected boolean canOperate() {
        // Simple storage doesn't need to "operate" - it's always ready
        return true;
    }

    @Override
    protected void performOperation() {
        // Simple storage doesn't perform operations - it just stores energy
        // Energy input/output is handled automatically by capabilities
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.basic_energy_storage");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.BasicEnergyStorageMenu(containerId, playerInventory, this);
    }

    // ========== ENERGY CAPABILITY OVERRIDES ==========

    @Override
    public boolean canConnectEnergy(Direction side) {
        // All sides can connect to cables/machines
        return true;
    }

    @Override
    public boolean canReceiveEnergy(Direction side) {
        // All sides can receive energy
        return true;
    }

    @Override
    public boolean canExtractEnergy(Direction side) {
        // All sides can provide energy
        return true;
    }

    // ========== GUI DATA METHODS ==========

    /**
     * Get current energy stored (for GUI display)
     */
    public int getCurrentEnergy() {
        return energyStorage.getEnergyStored();
    }

    /**
     * Get maximum energy capacity (for GUI display)
     */
    public int getMaxEnergy() {
        return energyStorage.getMaxEnergyStored();
    }

    /**
     * Get energy percentage (for GUI progress bar)
     */
    public float getEnergyPercentage() {
        if (energyStorage.getMaxEnergyStored() == 0) return 0.0f;
        return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
    }

    /**
     * Get energy input rate for this tick (for GUI display)
     */
    public int getEnergyInputRate() {
        return energyInputLastTick;
    }

    /**
     * Get energy output rate for this tick (for GUI display)
     */
    public int getEnergyOutputRate() {
        return energyOutputLastTick;
    }

    /**
     * Get energy transfer rate capability (for GUI display)
     */
    public int getTransferRate() {
        return ENERGY_TRANSFER_RATE;
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save energy flow tracking data
        tag.putInt("EnergyInputLastTick", energyInputLastTick);
        tag.putInt("EnergyOutputLastTick", energyOutputLastTick);
        tag.putInt("EnergyStoredLastTick", energyStoredLastTick);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load energy flow tracking data
        energyInputLastTick = tag.getInt("EnergyInputLastTick");
        energyOutputLastTick = tag.getInt("EnergyOutputLastTick");
        energyStoredLastTick = tag.getInt("EnergyStoredLastTick");
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send energy data to client for GUI updates
        tag.putInt("EnergyInput", energyInputLastTick);
        tag.putInt("EnergyOutput", energyOutputLastTick);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive energy data from server
        energyInputLastTick = tag.getInt("EnergyInput");
        energyOutputLastTick = tag.getInt("EnergyOutput");
    }
}