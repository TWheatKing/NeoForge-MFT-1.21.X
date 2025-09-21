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
 * Quantum Energy Storage Block Entity - 25,000,000 FE Battery
 *
 * WHAT IT DOES:
 * • Stores energy (25,000,000 FE capacity - 5x Superior)
 * • Receives energy through any type of cable (all sides)
 * • Provides energy out into any type of cable (all sides)
 * • Simple battery - no complex features
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 */
public class QuantumEnergyStorageBlockEntity extends BaseMachineBlockEntity {

    // Quantum Energy Storage Specifications
    private static final int ENERGY_CAPACITY = 25000000;     // 25,000,000 FE total storage
    private static final int ENERGY_TRANSFER_RATE = 50000;   // 50,000 FE/tick in/out

    // Energy flow tracking for GUI display
    private int energyInputLastTick = 0;
    private int energyOutputLastTick = 0;
    private int energyStoredLastTick = 0;

    public QuantumEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.QUANTUM_ENERGY_STORAGE.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, 0);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        int currentEnergyStored = energyStorage.getEnergyStored();

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
        distributeEnergyToNeighbors();

        boolean wasActive = isActive;
        isActive = energyStorage.getEnergyStored() > 0;

        if (wasActive != isActive && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    private void distributeEnergyToNeighbors() {
        if (energyStorage.getEnergyStored() <= 0) return;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);

            var neighborCap = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                    neighborPos, direction.getOpposite());

            if (neighborCap != null && neighborCap.canReceive()) {
                int energyToSend = Math.min(
                        energyStorage.getEnergyStored(),
                        Math.min(ENERGY_TRANSFER_RATE, neighborCap.getMaxEnergyStored() - neighborCap.getEnergyStored())
                );

                if (energyToSend > 0) {
                    int extracted = energyStorage.extractEnergy(energyToSend, false);
                    int received = neighborCap.receiveEnergy(extracted, false);

                    if (received < extracted) {
                        energyStorage.receiveEnergy(extracted - received, false);
                    }

                    if (received > 0) {
                        setChanged();
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected boolean canOperate() { return true; }
    @Override
    protected void performOperation() { }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.quantum_energy_storage");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.QuantumEnergyStorageMenu(containerId, playerInventory, this);
    }

    @Override
    public boolean canConnectEnergy(Direction side) { return true; }
    @Override
    public boolean canReceiveEnergy(Direction side) { return true; }
    @Override
    public boolean canExtractEnergy(Direction side) { return true; }

    public int getCurrentEnergy() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergy() { return energyStorage.getMaxEnergyStored(); }
    public float getEnergyPercentage() {
        if (energyStorage.getMaxEnergyStored() == 0) return 0.0f;
        return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
    }
    public int getEnergyInputRate() { return energyInputLastTick; }
    public int getEnergyOutputRate() { return energyOutputLastTick; }
    public int getTransferRate() { return ENERGY_TRANSFER_RATE; }

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("EnergyInputLastTick", energyInputLastTick);
        tag.putInt("EnergyOutputLastTick", energyOutputLastTick);
        tag.putInt("EnergyStoredLastTick", energyStoredLastTick);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        energyInputLastTick = tag.getInt("EnergyInputLastTick");
        energyOutputLastTick = tag.getInt("EnergyOutputLastTick");
        energyStoredLastTick = tag.getInt("EnergyStoredLastTick");
    }

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        tag.putInt("EnergyInput", energyInputLastTick);
        tag.putInt("EnergyOutput", energyOutputLastTick);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        energyInputLastTick = tag.getInt("EnergyInput");
        energyOutputLastTick = tag.getInt("EnergyOutput");
    }
}