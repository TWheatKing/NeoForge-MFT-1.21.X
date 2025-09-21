package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.energy.EnhancedMftEnergyNetwork;
import com.thewheatking.minecraftfarmertechmod.common.util.CableUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;

/**
 * CORRECTED: Base energy transmission block entity for cable networks
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/transmission/EnergyTransmissionBlockEntity.java
 * Purpose: Provides foundational cable functionality with network management, energy routing, and loss calculation
 */
public abstract class EnergyTransmissionBlockEntity extends BaseMachineBlockEntity {

    // Cable specifications
    protected final HybridEnergyStorage.TransferTier transferTier;
    protected final int transferRate;
    protected final double energyLossPerBlock;
    protected final boolean isInsulated;

    // Network management
    protected EnhancedMftEnergyNetwork connectedNetwork;
    protected String networkId;
    protected boolean networkDirty = true;

    // Connection management
    protected final Map<Direction, Boolean> connections = new HashMap<>();
    protected final Map<Direction, IEnergyStorage> connectedDevices = new HashMap<>();

    // Performance tracking
    protected int energyTransferred = 0;
    protected double currentLoad = 0.0;
    protected static final int NETWORK_UPDATE_INTERVAL = 20; // Update network every second

    // Visual state
    protected boolean isTransmitting = false;
    protected int transmissionAnimation = 0;

    public EnergyTransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                         HybridEnergyStorage.TransferTier tier) {
        super(type, pos, state, tier.getTransferRate() * 2, tier.getTransferRate(), tier.getTransferRate(), 0);

        this.transferTier = tier;
        this.transferRate = tier.getTransferRate();
        this.energyLossPerBlock = tier.getEnergyLoss();
        this.isInsulated = tier.name().contains("INSULATED");

        // Initialize connections
        for (Direction direction : Direction.values()) {
            connections.put(direction, false);
        }
    }

    @Override
    protected HybridEnergyStorage createEnergyStorage() {
        // Cable acts as a small buffer - capacity equals transfer rate for smooth flow
        return new HybridEnergyStorage(transferTier);
    }

    @Override
    protected ItemStackHandler createInventory() {
        // Cables don't have inventory
        return new ItemStackHandler(0);
    }

    @Override
    protected boolean canOperate() {
        // Cables always operate if they have energy to transmit
        return energyStorage.getEnergyStored() > 0 || hasConnectedDevices();
    }

    @Override
    protected void performOperation() {
        // Cable-specific operation: distribute energy to all connected sides
        distributeEnergyToAllSides();
    }

    @Override
    protected void serverTick() {
        super.serverTick();

        // Update network periodically
        if (tickCounter % NETWORK_UPDATE_INTERVAL == 0 || networkDirty) {
            updateNetwork();
            networkDirty = false;
        }

        // Update connections
        updateConnections();

        // Update visual state
        updateTransmissionState();

        // Reset counters
        if (tickCounter % 20 == 0) {
            energyTransferred = 0;
            currentLoad = 0.0;
        }
    }

    @Override
    protected void clientTick() {
        super.clientTick();

        // Handle client-side visual effects
        if (isTransmitting) {
            transmissionAnimation = (transmissionAnimation + 1) % 40; // 2-second cycle
            spawnTransmissionParticles();
        } else {
            transmissionAnimation = 0;
        }
    }

    protected void updateNetwork() {
        if (level != null) {
            // Get or create network
            connectedNetwork = EnhancedMftEnergyNetwork.getOrCreateNetwork(level, worldPosition);
            networkId = connectedNetwork.getNetworkId();

            // Mark network as dirty if this cable was modified
            connectedNetwork.markDirty();
        }
    }

    protected void updateConnections() {
        if (level == null) return;

        boolean connectionsChanged = false;

        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = worldPosition.relative(direction);
            boolean wasConnected = connections.get(direction);
            boolean isConnected = false;

            if (level.isLoaded(adjacentPos)) {
                // Check for cables
                if (CableUtils.isCable(level, adjacentPos)) {
                    isConnected = true;
                    connectedDevices.remove(direction);
                } else {
                    // Check for energy devices
                    IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                            adjacentPos, direction.getOpposite());

                    if (energyStorage != null) {
                        isConnected = true;
                        connectedDevices.put(direction, energyStorage);
                    } else {
                        connectedDevices.remove(direction);
                    }
                }
            }

            connections.put(direction, isConnected);

            if (wasConnected != isConnected) {
                connectionsChanged = true;
            }
        }

        if (connectionsChanged) {
            networkDirty = true;
            setChanged();
            markUpdated();
        }
    }

    private void distributeEnergyToAllSides() {
        int totalEnergyToDistribute = energyStorage.getEnergyStored();
        if (totalEnergyToDistribute <= 0) return;

        // Find all valid energy receivers
        var receivers = new ArrayList<IEnergyStorage>();
        var directions = new ArrayList<Direction>();

        for (Direction direction : Direction.values()) {
            if (connectedDevices.containsKey(direction)) {
                IEnergyStorage device = connectedDevices.get(direction);
                if (device.canReceive()) {
                    receivers.add(device);
                    directions.add(direction);
                }
            }
        }

        if (receivers.isEmpty()) return;

        // Distribute energy evenly among receivers
        int energyPerReceiver = Math.min(totalEnergyToDistribute / receivers.size(), transferRate);

        for (int i = 0; i < receivers.size(); i++) {
            IEnergyStorage receiver = receivers.get(i);
            int transferAttempt = Math.min(energyPerReceiver, energyStorage.getEnergyStored());

            if (transferAttempt > 0) {
                int actualTransferred = transferEnergyWithLoss(transferAttempt, receiver);
                if (actualTransferred > 0) {
                    energyStorage.extractEnergy(actualTransferred, false);
                    energyTransferred += actualTransferred;
                    markUpdated();
                }
            }
        }

        currentLoad = (double) energyTransferred / transferRate;
        isTransmitting = energyTransferred > 0;
    }

    protected int transferEnergyWithLoss(int amount, IEnergyStorage target) {
        // Calculate energy loss
        double lossMultiplier = isInsulated ? energyLossPerBlock * 0.5 : energyLossPerBlock;
        int actualAmount = (int) (amount * (1.0 - lossMultiplier));

        // Transfer energy
        int received = target.receiveEnergy(actualAmount, false);

        // Return the amount that should be extracted from source (before loss)
        return received > 0 ? (int) (received / (1.0 - lossMultiplier)) : 0;
    }

    protected void updateTransmissionState() {
        boolean wasTransmitting = isTransmitting;
        isTransmitting = energyTransferred > 0 || energyStorage.getEnergyStored() > 0;

        if (wasTransmitting != isTransmitting) {
            setChanged();
            markUpdated();
        }
    }

    protected void spawnTransmissionParticles() {
        // Override in subclasses for specific particle effects
        if (level != null && level.isClientSide()) {
            // Example particle spawning logic would go here
        }
    }

    protected boolean hasConnectedDevices() {
        return !connectedDevices.isEmpty();
    }

    @Override
    protected boolean canOutputEnergy(Direction direction) {
        // Cables can output energy to all connected sides
        return connections.get(direction);
    }

    @Override
    protected boolean canInputEnergy(Direction direction) {
        // Cables can receive energy from all connected sides
        return connections.get(direction);
    }

    // Enhanced energy distribution for cables - override base class method
    @Override
    protected void handleEnergyDistribution() {
        // Cables use their own distribution logic
        distributeEnergyToAllSides();
    }

    // Getters and utility methods

    public int getTransferRate() {
        return transferRate;
    }

    public double getEnergyLossPerBlock() {
        return energyLossPerBlock;
    }

    public boolean isInsulated() {
        return isInsulated;
    }

    public boolean isConnected(Direction direction) {
        return connections.get(direction);
    }

    public Map<Direction, Boolean> getConnections() {
        return new HashMap<>(connections);
    }

    public boolean isTransmitting() {
        return isTransmitting;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public int getEnergyTransferred() {
        return energyTransferred;
    }

    public String getNetworkId() {
        return networkId;
    }

    public EnhancedMftEnergyNetwork getConnectedNetwork() {
        return connectedNetwork;
    }

    public int getTransmissionAnimation() {
        return transmissionAnimation;
    }

    public HybridEnergyStorage.TransferTier getTransferTier() {
        return transferTier;
    }

    // Network management

    public void onNetworkChanged() {
        networkDirty = true;
    }

    public void disconnectFromNetwork() {
        if (connectedNetwork != null && networkId != null) {
            connectedNetwork.markDirty();
        }
        connectedNetwork = null;
        networkId = null;
    }

    public Set<BlockPos> getConnectedCables() {
        if (level != null) {
            return CableUtils.findConnectedCables(level, worldPosition);
        }
        return Set.of();
    }

    // NBT serialization

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalData(tag, registries);

        // Save cable state
        tag.putString("TransferTier", transferTier.name());
        tag.putInt("TransferRate", transferRate);
        tag.putDouble("EnergyLossPerBlock", energyLossPerBlock);
        tag.putBoolean("IsInsulated", isInsulated);
        tag.putBoolean("IsTransmitting", isTransmitting);
        tag.putDouble("CurrentLoad", currentLoad);
        tag.putInt("EnergyTransferred", energyTransferred);

        // Save connections
        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.from3DDataValue(i);
            tag.putBoolean("Connection_" + direction.name(), connections.get(direction));
        }

        // Save network info
        if (networkId != null) {
            tag.putString("NetworkId", networkId);
        }
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditionalData(tag, registries);

        // Load cable state
        isTransmitting = tag.getBoolean("IsTransmitting");
        currentLoad = tag.getDouble("CurrentLoad");
        energyTransferred = tag.getInt("EnergyTransferred");

        // Load connections
        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.from3DDataValue(i);
            if (tag.contains("Connection_" + direction.name())) {
                connections.put(direction, tag.getBoolean("Connection_" + direction.name()));
            }
        }

        // Load network info
        if (tag.contains("NetworkId")) {
            networkId = tag.getString("NetworkId");
        }
    }

    // Cleanup when block is removed
    @Override
    public void setRemoved() {
        super.setRemoved();
        disconnectFromNetwork();
        CableUtils.clearCache();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Cables don't have GUIs by default
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod." + transferTier.name().toLowerCase() + "_cable");
    }

    @Override
    public String getDebugInfo() {
        return String.format("Cable: %s, Transfer Rate: %d FE/t, Energy: %d/%d, Insulated: %s, Load: %.1f%%, Connections: %d",
                transferTier.name(),
                transferRate,
                energyStorage.getEnergyStored(),
                energyStorage.getMaxEnergyStored(),
                isInsulated ? "Yes" : "No",
                currentLoad * 100,
                connections.values().stream().mapToInt(b -> b ? 1 : 0).sum());
    }

    /**
     * Gets energy flow statistics
     */
    public EnergyFlowStats getEnergyFlowStats() {
        return new EnergyFlowStats(
                energyTransferred,
                currentLoad,
                transferRate,
                energyLossPerBlock,
                connections.values().stream().mapToInt(b -> b ? 1 : 0).sum(),
                isTransmitting
        );
    }

    /**
     * Record for energy flow statistics
     */
    public record EnergyFlowStats(
            int energyTransferred,
            double currentLoad,
            int maxTransferRate,
            double energyLoss,
            int activeConnections,
            boolean isTransmitting
    ) {}
}