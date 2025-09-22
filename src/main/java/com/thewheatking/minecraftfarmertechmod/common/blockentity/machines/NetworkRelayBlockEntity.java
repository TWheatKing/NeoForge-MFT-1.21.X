package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Network Relay Block Entity - Network-to-Network Linker & Master Dashboard Controller
 *
 * WHAT IT DOES:
 * • Multiblock component that connects to NetworkDashboardBlock
 * • Links one network to another network (network-to-network communication)
 * • Unlocks new tab on connected dashboard showing OTHER network dashboards
 * • Master control center - monitor all machines from multiple networks in one place
 * • Creates "network of networks" management system
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/NetworkRelayBlockEntity.java
 */
public class NetworkRelayBlockEntity extends BaseMachineBlockEntity {

    // Network Relay Specifications
    private static final int ENERGY_CAPACITY = 25000;        // 25,000 FE capacity
    private static final int ENERGY_TRANSFER_RATE = 10000;   // 10,000 FE/tick transfer
    private static final int INVENTORY_SIZE = 9;             // Upgrade/component slots

    // Multiblock State with Dashboard
    private BlockPos connectedDashboardPos = null;
    private boolean isDashboardConnected = false;
    private boolean isMultiblockFormed = false;

    // Network-to-Network Linking System
    private final Set<UUID> linkedNetworks = new HashSet<>();
    private final Map<UUID, NetworkInfo> networkDatabase = new ConcurrentHashMap<>();
    private final Map<UUID, BlockPos> networkDashboardPositions = new HashMap<>();

    // Master Network Control
    private boolean isMasterController = false;
    private UUID masterNetworkId = null;
    private int totalLinkedNetworks = 0;
    private int totalMachinesAcrossNetworks = 0;

    // Network Discovery and Communication
    private boolean isScanning = false;
    private int scanRadius = 64;                              // Range to scan for other networks
    private long lastNetworkScan = 0L;
    private static final long NETWORK_SCAN_INTERVAL = 200L;   // Scan every 10 seconds

    // Cross-Network Statistics
    private long totalCrossNetworkEnergy = 0L;
    private int activeNetworksCount = 0;
    private int unstableNetworksCount = 0;
    private float averageNetworkEfficiency = 100.0f;

    // Network Information Structure
    public static class NetworkInfo {
        public final UUID networkId;
        public final BlockPos dashboardPosition;
        public final String networkName;
        public final long discoveryTime;

        // Network Status
        public boolean isOnline;
        public boolean isStable;
        public int deviceCount;
        public int activeDevices;
        public long totalEnergy;
        public long maxCapacity;
        public float efficiency;
        public long lastUpdate;

        // Communication Status
        public boolean canCommunicate;
        public int signalStrength;        // 0-100
        public long lastCommunication;

        public NetworkInfo(UUID id, BlockPos dashPos, String name) {
            this.networkId = id;
            this.dashboardPosition = dashPos;
            this.networkName = name != null ? name : "Network-" + id.toString().substring(0, 8);
            this.discoveryTime = System.currentTimeMillis();
            this.isOnline = true;
            this.isStable = true;
            this.canCommunicate = true;
            this.signalStrength = 100;
            this.efficiency = 100.0f;
            this.lastUpdate = this.discoveryTime;
            this.lastCommunication = this.discoveryTime;
        }

        public void updateNetworkStatus(NetworkDashboardBlockEntity dashboard) {
            if (dashboard != null && dashboard.isNetworkMapped()) {
                this.isOnline = true;
                this.deviceCount = dashboard.getTotalDevices();
                this.activeDevices = dashboard.getActiveDevices();
                this.totalEnergy = dashboard.getTotalNetworkEnergy();
                this.maxCapacity = dashboard.getMaxNetworkCapacity();
                this.efficiency = dashboard.getNetworkEfficiency();
                this.isStable = dashboard.isNetworkStable();
                this.lastUpdate = System.currentTimeMillis();
                this.lastCommunication = this.lastUpdate;
            } else {
                this.isOnline = false;
                this.canCommunicate = false;
            }
        }
    }

    public NetworkRelayBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.NETWORK_RELAY.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, INVENTORY_SIZE);

        // Generate unique network ID for this relay's network
        this.masterNetworkId = UUID.randomUUID();
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        // Check for NetworkDashboard multiblock connection
        checkDashboardConnection();

        // Periodic network discovery and communication
        long currentTime = level.getGameTime();
        if (currentTime - lastNetworkScan >= NETWORK_SCAN_INTERVAL) {
            if (canPerformNetworkOperations()) {
                performNetworkDiscovery();
                updateCrossNetworkStatistics();
            }
            lastNetworkScan = currentTime;
        }

        // Update master controller status
        updateMasterControllerStatus();

        // Maintain network communications
        maintainNetworkCommunications();

        // Update working state
        boolean wasWorking = isWorking;
        isWorking = isDashboardConnected && (isScanning || totalLinkedNetworks > 0);

        // Sync to client if state changed
        if (wasWorking != isWorking && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Check for connected NetworkDashboard to form multiblock
     */
    private void checkDashboardConnection() {
        boolean foundDashboard = false;
        BlockPos dashboardPos = null;

        // Check all 6 directions for NetworkDashboard
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);

            if (level.getBlockEntity(neighborPos) instanceof NetworkDashboardBlockEntity dashboard) {
                foundDashboard = true;
                dashboardPos = neighborPos;
                break;
            }
        }

        // Update multiblock state
        boolean wasConnected = isDashboardConnected;
        isDashboardConnected = foundDashboard;
        connectedDashboardPos = dashboardPos;
        isMultiblockFormed = foundDashboard;

        // Register this network when dashboard connects
        if (foundDashboard && !wasConnected) {
            registerOwnNetwork();
            addDebugMessage("Network Relay connected to Dashboard - Master Network registered");
        } else if (!foundDashboard && wasConnected) {
            unregisterOwnNetwork();
            addDebugMessage("Network Relay disconnected from Dashboard - Master Network unregistered");
        }

        if (wasConnected != isDashboardConnected) {
            setChanged();
        }
    }

    /**
     * Check if network operations can be performed
     */
    private boolean canPerformNetworkOperations() {
        return energyStorage.getEnergyStored() >= 500 && isDashboardConnected;
    }

    /**
     * Perform network discovery to find other NetworkRelay + Dashboard combinations
     */
    private void performNetworkDiscovery() {
        isScanning = true;

        // Scan for other NetworkRelay blocks within range
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int discoveredNetworks = 0;

        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int y = -scanRadius; y <= scanRadius; y++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);

                    // Skip our own position
                    if (mutablePos.equals(worldPosition)) continue;

                    // Check if there's a NetworkRelay at this position
                    if (level.getBlockEntity(mutablePos) instanceof NetworkRelayBlockEntity otherRelay) {
                        if (otherRelay.isDashboardConnected() && otherRelay.isMultiblockFormed()) {

                            // Discover this network
                            UUID otherNetworkId = otherRelay.getMasterNetworkId();
                            BlockPos otherDashboardPos = otherRelay.getConnectedDashboardPos();

                            if (otherNetworkId != null && !otherNetworkId.equals(masterNetworkId)) {
                                discoverNetwork(otherNetworkId, otherDashboardPos, mutablePos.immutable());
                                discoveredNetworks++;
                            }
                        }
                    }
                }
            }
        }

        if (discoveredNetworks > 0) {
            addDebugMessage("Discovered " + discoveredNetworks + " other networks");
        }

        isScanning = false;
    }

    /**
     * Discover and register a new network
     */
    private void discoverNetwork(UUID networkId, BlockPos dashboardPos, BlockPos relayPos) {
        if (!linkedNetworks.contains(networkId)) {
            // Create network info
            String networkName = "Remote Network " + (linkedNetworks.size() + 1);
            NetworkInfo networkInfo = new NetworkInfo(networkId, dashboardPos, networkName);

            // Link the network
            linkedNetworks.add(networkId);
            networkDatabase.put(networkId, networkInfo);
            networkDashboardPositions.put(networkId, dashboardPos);

            // Consume energy for network linking
            energyStorage.extractEnergy(1000, false);

            addDebugMessage("Linked to network: " + networkName);
        }

        // Update network status
        updateNetworkStatus(networkId);
    }

    /**
     * Update status of a specific linked network
     */
    private void updateNetworkStatus(UUID networkId) {
        NetworkInfo networkInfo = networkDatabase.get(networkId);
        if (networkInfo == null) return;

        BlockPos dashboardPos = networkInfo.dashboardPosition;
        if (dashboardPos != null && level.getBlockEntity(dashboardPos) instanceof NetworkDashboardBlockEntity dashboard) {
            networkInfo.updateNetworkStatus(dashboard);

            // Calculate signal strength based on distance
            double distance = Math.sqrt(worldPosition.distSqr(dashboardPos));
            networkInfo.signalStrength = Math.max(0, Math.min(100, (int) (100 - (distance / scanRadius * 50))));

            networkInfo.canCommunicate = networkInfo.signalStrength > 10; // Minimum 10% signal for communication
        } else {
            // Network is offline
            networkInfo.isOnline = false;
            networkInfo.canCommunicate = false;
            networkInfo.signalStrength = 0;
        }
    }

    /**
     * Update cross-network statistics
     */
    private void updateCrossNetworkStatistics() {
        totalLinkedNetworks = linkedNetworks.size();
        totalMachinesAcrossNetworks = 0;
        totalCrossNetworkEnergy = 0L;
        activeNetworksCount = 0;
        unstableNetworksCount = 0;
        float totalEfficiency = 0.0f;
        int efficiencyCount = 0;

        // Include our own network
        if (connectedDashboardPos != null && level.getBlockEntity(connectedDashboardPos) instanceof NetworkDashboardBlockEntity ownDashboard) {
            if (ownDashboard.isNetworkMapped()) {
                totalMachinesAcrossNetworks += ownDashboard.getTotalDevices();
                totalCrossNetworkEnergy += ownDashboard.getTotalNetworkEnergy();
                activeNetworksCount++;
                if (!ownDashboard.isNetworkStable()) unstableNetworksCount++;
                totalEfficiency += ownDashboard.getNetworkEfficiency();
                efficiencyCount++;
            }
        }

        // Update all linked networks
        for (UUID networkId : linkedNetworks) {
            updateNetworkStatus(networkId);
            NetworkInfo networkInfo = networkDatabase.get(networkId);

            if (networkInfo != null && networkInfo.isOnline) {
                totalMachinesAcrossNetworks += networkInfo.deviceCount;
                totalCrossNetworkEnergy += networkInfo.totalEnergy;
                activeNetworksCount++;
                if (!networkInfo.isStable) unstableNetworksCount++;
                totalEfficiency += networkInfo.efficiency;
                efficiencyCount++;
            }
        }

        // Calculate average efficiency
        averageNetworkEfficiency = efficiencyCount > 0 ? totalEfficiency / efficiencyCount : 100.0f;
    }

    /**
     * Update master controller status
     */
    private void updateMasterControllerStatus() {
        isMasterController = isDashboardConnected && totalLinkedNetworks > 0;
    }

    /**
     * Maintain communications with linked networks
     */
    private void maintainNetworkCommunications() {
        if (!isMasterController) return;

        // Small energy cost for maintaining cross-network communication
        int communicationCost = Math.max(1, totalLinkedNetworks * 2);
        energyStorage.extractEnergy(communicationCost, false);
    }

    /**
     * Register our own network in the system
     */
    private void registerOwnNetwork() {
        if (connectedDashboardPos != null) {
            NetworkInfo ownNetwork = new NetworkInfo(masterNetworkId, connectedDashboardPos, "Master Network");
            networkDatabase.put(masterNetworkId, ownNetwork);
            addDebugMessage("Master network registered with ID: " + masterNetworkId.toString().substring(0, 8));
        }
    }

    /**
     * Unregister our own network
     */
    private void unregisterOwnNetwork() {
        networkDatabase.remove(masterNetworkId);
        addDebugMessage("Master network unregistered");
    }

    /**
     * Manually link to a specific network
     */
    public boolean linkToNetwork(UUID networkId, BlockPos dashboardPos, BlockPos relayPos) {
        if (!linkedNetworks.contains(networkId) && energyStorage.getEnergyStored() >= 1000) {
            discoverNetwork(networkId, dashboardPos, relayPos);
            return true;
        }
        return false;
    }

    /**
     * Unlink from a specific network
     */
    public boolean unlinkFromNetwork(UUID networkId) {
        if (linkedNetworks.remove(networkId)) {
            networkDatabase.remove(networkId);
            networkDashboardPositions.remove(networkId);
            addDebugMessage("Unlinked from network: " + networkId.toString().substring(0, 8));
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    protected boolean canOperate() {
        return energyStorage.getEnergyStored() >= 100 && isDashboardConnected;
    }

    @Override
    protected void performOperation() {
        // Main operation logic is handled in serverTick
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.network_relay");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.NetworkRelayMenu(containerId, playerInventory, this);
    }

    // ========== GUI DATA METHODS ==========

    // Multiblock Status
    public boolean isDashboardConnected() { return isDashboardConnected; }
    public boolean isMultiblockFormed() { return isMultiblockFormed; }
    public BlockPos getConnectedDashboardPos() { return connectedDashboardPos; }

    // Master Controller Status
    public boolean isMasterController() { return isMasterController; }
    public UUID getMasterNetworkId() { return masterNetworkId; }
    public boolean isScanning() { return isScanning; }
    public int getScanRadius() { return scanRadius; }

    // Network Statistics
    public int getTotalLinkedNetworks() { return totalLinkedNetworks; }
    public int getTotalMachinesAcrossNetworks() { return totalMachinesAcrossNetworks; }
    public long getTotalCrossNetworkEnergy() { return totalCrossNetworkEnergy; }
    public int getActiveNetworksCount() { return activeNetworksCount; }
    public int getUnstableNetworksCount() { return unstableNetworksCount; }
    public float getAverageNetworkEfficiency() { return averageNetworkEfficiency; }

    // Network Database Access
    public Set<UUID> getLinkedNetworks() { return new HashSet<>(linkedNetworks); }
    public Map<UUID, NetworkInfo> getNetworkDatabase() { return new HashMap<>(networkDatabase); }
    public NetworkInfo getNetworkInfo(UUID networkId) { return networkDatabase.get(networkId); }

    // Control Methods
    public void setScanRadius(int radius) {
        this.scanRadius = Math.max(16, Math.min(128, radius));
        setChanged();
    }

    public void forceNetworkScan() {
        if (canPerformNetworkOperations()) {
            lastNetworkScan = 0L; // Force immediate scan
            addDebugMessage("Manual network scan initiated");
        }
    }

    public void clearAllNetworkLinks() {
        linkedNetworks.clear();
        networkDatabase.clear();
        networkDashboardPositions.clear();
        // Keep master network
        registerOwnNetwork();
        addDebugMessage("All network links cleared");
        setChanged();
    }

    // ========== DEBUG METHODS ==========

    private void addDebugMessage(String message) {
        if (level != null && !level.isClientSide()) {
            // TODO: Add to debug log or send to nearby players
            System.out.println("[NetworkRelay] " + message);
        }
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save multiblock state
        tag.putBoolean("IsDashboardConnected", isDashboardConnected);
        tag.putBoolean("IsMultiblockFormed", isMultiblockFormed);
        tag.putBoolean("IsMasterController", isMasterController);

        if (connectedDashboardPos != null) {
            tag.putLong("ConnectedDashboardPos", connectedDashboardPos.asLong());
        }

        if (masterNetworkId != null) {
            tag.putUUID("MasterNetworkId", masterNetworkId);
        }

        // Save network links
        ListTag networksTag = new ListTag();
        for (UUID networkId : linkedNetworks) {
            CompoundTag networkTag = new CompoundTag();
            networkTag.putUUID("NetworkId", networkId);

            NetworkInfo info = networkDatabase.get(networkId);
            if (info != null) {
                networkTag.putString("NetworkName", info.networkName);
                networkTag.putLong("DashboardPos", info.dashboardPosition.asLong());
                networkTag.putBoolean("IsOnline", info.isOnline);
                networkTag.putBoolean("IsStable", info.isStable);
                networkTag.putInt("DeviceCount", info.deviceCount);
                networkTag.putFloat("Efficiency", info.efficiency);
                networkTag.putInt("SignalStrength", info.signalStrength);
                networkTag.putLong("LastUpdate", info.lastUpdate);
            }

            networksTag.add(networkTag);
        }
        tag.put("LinkedNetworks", networksTag);

        // Save scanning state
        tag.putBoolean("IsScanning", isScanning);
        tag.putInt("ScanRadius", scanRadius);
        tag.putLong("LastNetworkScan", lastNetworkScan);

        // Save statistics
        tag.putInt("TotalLinkedNetworks", totalLinkedNetworks);
        tag.putInt("TotalMachinesAcrossNetworks", totalMachinesAcrossNetworks);
        tag.putLong("TotalCrossNetworkEnergy", totalCrossNetworkEnergy);
        tag.putInt("ActiveNetworksCount", activeNetworksCount);
        tag.putInt("UnstableNetworksCount", unstableNetworksCount);
        tag.putFloat("AverageNetworkEfficiency", averageNetworkEfficiency);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load multiblock state
        isDashboardConnected = tag.getBoolean("IsDashboardConnected");
        isMultiblockFormed = tag.getBoolean("IsMultiblockFormed");
        isMasterController = tag.getBoolean("IsMasterController");

        if (tag.contains("ConnectedDashboardPos")) {
            connectedDashboardPos = BlockPos.of(tag.getLong("ConnectedDashboardPos"));
        }

        if (tag.hasUUID("MasterNetworkId")) {
            masterNetworkId = tag.getUUID("MasterNetworkId");
        }

        // Load network links
        linkedNetworks.clear();
        networkDatabase.clear();
        networkDashboardPositions.clear();

        if (tag.contains("LinkedNetworks")) {
            ListTag networksTag = tag.getList("LinkedNetworks", Tag.TAG_COMPOUND);
            for (int i = 0; i < networksTag.size(); i++) {
                CompoundTag networkTag = networksTag.getCompound(i);
                UUID networkId = networkTag.getUUID("NetworkId");

                String networkName = networkTag.getString("NetworkName");
                BlockPos dashboardPos = BlockPos.of(networkTag.getLong("DashboardPos"));

                linkedNetworks.add(networkId);
                networkDashboardPositions.put(networkId, dashboardPos);

                // Recreate network info
                NetworkInfo info = new NetworkInfo(networkId, dashboardPos, networkName);
                info.isOnline = networkTag.getBoolean("IsOnline");
                info.isStable = networkTag.getBoolean("IsStable");
                info.deviceCount = networkTag.getInt("DeviceCount");
                info.efficiency = networkTag.getFloat("Efficiency");
                info.signalStrength = networkTag.getInt("SignalStrength");
                info.lastUpdate = networkTag.getLong("LastUpdate");

                networkDatabase.put(networkId, info);
            }
        }

        // Load scanning state
        isScanning = tag.getBoolean("IsScanning");
        scanRadius = tag.getInt("ScanRadius");
        if (scanRadius == 0) scanRadius = 64; // Default value
        lastNetworkScan = tag.getLong("LastNetworkScan");

        // Load statistics
        totalLinkedNetworks = tag.getInt("TotalLinkedNetworks");
        totalMachinesAcrossNetworks = tag.getInt("TotalMachinesAcrossNetworks");
        totalCrossNetworkEnergy = tag.getLong("TotalCrossNetworkEnergy");
        activeNetworksCount = tag.getInt("ActiveNetworksCount");
        unstableNetworksCount = tag.getInt("UnstableNetworksCount");
        averageNetworkEfficiency = tag.getFloat("AverageNetworkEfficiency");
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send relay data to client for GUI updates
        tag.putBoolean("DashboardConnected", isDashboardConnected);
        tag.putBoolean("MultiblockFormed", isMultiblockFormed);
        tag.putBoolean("MasterController", isMasterController);
        tag.putBoolean("Scanning", isScanning);
        tag.putInt("LinkedNetworks", totalLinkedNetworks);
        tag.putInt("TotalMachines", totalMachinesAcrossNetworks);
        tag.putFloat("AvgEfficiency", averageNetworkEfficiency);
        tag.putInt("ActiveNetworks", activeNetworksCount);
        tag.putInt("UnstableNetworks", unstableNetworksCount);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive relay data from server
        isDashboardConnected = tag.getBoolean("DashboardConnected");
        isMultiblockFormed = tag.getBoolean("MultiblockFormed");
        isMasterController = tag.getBoolean("MasterController");
        isScanning = tag.getBoolean("Scanning");
        totalLinkedNetworks = tag.getInt("LinkedNetworks");
        totalMachinesAcrossNetworks = tag.getInt("TotalMachines");
        averageNetworkEfficiency = tag.getFloat("AvgEfficiency");
        activeNetworksCount = tag.getInt("ActiveNetworks");
        unstableNetworksCount = tag.getInt("UnstableNetworks");
    }
}