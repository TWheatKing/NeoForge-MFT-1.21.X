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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Network Dashboard Block Entity - Ultimate Network Analysis & Control Center
 *
 * WHAT IT DOES:
 * TAB 1 - Network Overview: Visual network topology mapping with draggable interface
 * TAB 2 - Remote Control: Machine control & performance analysis (requires NetworkBridge multiblock)
 *
 * • Scans entire energy network when connected via any cable tier
 * • Creates visual map with block face textures and names as symbols
 * • Clickable/draggable network topology display
 * • Performance monitoring and stress analysis of all network devices
 * • Remote control capabilities when NetworkBridge is connected (multiblock)
 * • Real-time network health and optimization suggestions
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/NetworkDashboardBlockEntity.java
 */
public class NetworkDashboardBlockEntity extends BaseMachineBlockEntity {

    // Network Dashboard Specifications
    private static final int ENERGY_CAPACITY = 25000;        // 25,000 FE capacity
    private static final int ENERGY_TRANSFER_RATE = 2000;    // 2,000 FE/tick transfer
    private static final int INVENTORY_SIZE = 18;            // Upgrade/component slots

    // Network Scanning Configuration
    private static final int MAX_SCAN_RANGE = 128;           // Maximum blocks to scan from dashboard
    private static final int SCAN_INTERVAL = 100;            // Ticks between network rescans
    private static final int PERFORMANCE_UPDATE_INTERVAL = 20; // Performance analysis update rate

    // Network Device Data Structure
    public static class NetworkDevice {
        public final BlockPos position;
        public final String blockName;
        public final ResourceLocation blockId;
        public final String deviceType;           // "machine", "cable", "storage", "generator"
        public final Map<String, Object> properties;

        // Performance Data
        public int energyStored;
        public int maxEnergyStored;
        public int energyInput;
        public int energyOutput;
        public float efficiency;
        public boolean isActive;
        public boolean isOverloaded;
        public boolean isUnderperforming;
        public float stressLevel;                 // 0.0 - 1.0

        // Visual Mapping Data
        public int mapX, mapY;                    // Position on visual map
        public boolean isSelected;
        public String statusColor;               // For GUI coloring

        // Connection Data
        public final Set<BlockPos> connectedDevices;
        public final Map<Direction, BlockPos> directConnections;

        public NetworkDevice(BlockPos pos, String name, ResourceLocation id, String type) {
            this.position = pos;
            this.blockName = name;
            this.blockId = id;
            this.deviceType = type;
            this.properties = new HashMap<>();
            this.connectedDevices = new HashSet<>();
            this.directConnections = new EnumMap<>(Direction.class);

            // Initialize performance data
            this.efficiency = 100.0f;
            this.statusColor = "green";
        }

        public void updatePerformanceData(IEnergyStorage energyStorage, BlockEntity blockEntity) {
            if (energyStorage != null) {
                this.energyStored = energyStorage.getEnergyStored();
                this.maxEnergyStored = energyStorage.getMaxEnergyStored();
            }

            // Calculate stress level based on energy utilization
            if (maxEnergyStored > 0) {
                float utilizationRatio = (float) energyStored / maxEnergyStored;
                this.stressLevel = utilizationRatio;

                // Determine performance status
                this.isOverloaded = utilizationRatio > 0.95f;
                this.isUnderperforming = utilizationRatio < 0.1f && energyOutput == 0;

                // Set status color
                if (isOverloaded) {
                    this.statusColor = "red";
                } else if (isUnderperforming) {
                    this.statusColor = "yellow";
                } else if (isActive) {
                    this.statusColor = "green";
                } else {
                    this.statusColor = "gray";
                }
            }
        }
    }

    // Network Topology Data
    private final Map<BlockPos, NetworkDevice> networkDevices = new ConcurrentHashMap<>();
    private final Map<BlockPos, Set<BlockPos>> networkConnections = new ConcurrentHashMap<>();
    private final List<NetworkDevice> performanceIssues = new ArrayList<>();

    // Network Statistics
    private int totalDevices = 0;
    private int activeDevices = 0;
    private int overloadedDevices = 0;
    private int underperformingDevices = 0;
    private long totalNetworkEnergy = 0L;
    private long maxNetworkCapacity = 0L;
    private float networkEfficiency = 100.0f;
    private boolean networkStable = true;

    // Scanning State
    private boolean isScanning = false;
    private boolean networkMapped = false;
    private int scanProgress = 0;
    private int maxScanProgress = 100;
    private long lastScanTime = 0L;
    private long lastPerformanceUpdate = 0L;

    // Multiblock State (NetworkBridge connection)
    private BlockPos connectedBridgePos = null;
    private boolean hasNetworkBridge = false;
    private boolean remoteControlEnabled = false;

    // Visual Map Data
    private int mapCenterX = 0;
    private int mapCenterY = 0;
    private float mapZoom = 1.0f;
    private NetworkDevice selectedDevice = null;

    // Network Alerts
    private final List<String> networkAlerts = new ArrayList<>();
    private final Queue<String> recentEvents = new LinkedList<>();
    private static final int MAX_RECENT_EVENTS = 50;

    public NetworkDashboardBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.NETWORK_DASHBOARD.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, INVENTORY_SIZE);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        long currentTime = level.getGameTime();

        // Periodic network scanning
        if (currentTime - lastScanTime >= SCAN_INTERVAL) {
            if (canScanNetwork()) {
                performNetworkScan();
            }
            lastScanTime = currentTime;
        }

        // Performance analysis update
        if (currentTime - lastPerformanceUpdate >= PERFORMANCE_UPDATE_INTERVAL) {
            updateNetworkPerformance();
            lastPerformanceUpdate = currentTime;
        }

        // Check for NetworkBridge multiblock connection
        checkNetworkBridgeConnection();

        // Update working state
        boolean wasWorking = isWorking;
        isWorking = isScanning || (networkMapped && activeDevices > 0);

        // Sync to client if state changed
        if (wasWorking != isWorking && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Check if network scanning can be performed
     */
    private boolean canScanNetwork() {
        return energyStorage.getEnergyStored() >= 100 && !isScanning;
    }

    /**
     * Perform comprehensive network scan and topology mapping
     */
    private void performNetworkScan() {
        isScanning = true;
        scanProgress = 0;
        networkDevices.clear();
        networkConnections.clear();
        performanceIssues.clear();

        addRecentEvent("Starting network scan...");

        // Use breadth-first search to map entire network
        Queue<BlockPos> scanQueue = new LinkedList<>();
        Set<BlockPos> scannedPositions = new HashSet<>();

        // Start scanning from dashboard position
        scanQueue.add(worldPosition);
        scannedPositions.add(worldPosition);

        int scannedCount = 0;
        while (!scanQueue.isEmpty() && scannedCount < MAX_SCAN_RANGE) {
            BlockPos currentPos = scanQueue.poll();
            scannedCount++;

            // Scan current position
            NetworkDevice device = scanDeviceAtPosition(currentPos);
            if (device != null) {
                networkDevices.put(currentPos, device);

                // Scan neighboring positions for connections
                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = currentPos.relative(direction);

                    if (!scannedPositions.contains(neighborPos) &&
                            isWithinScanRange(neighborPos) &&
                            hasEnergyConnection(neighborPos)) {

                        scanQueue.add(neighborPos);
                        scannedPositions.add(neighborPos);

                        // Record connection
                        device.directConnections.put(direction, neighborPos);
                        networkConnections.computeIfAbsent(currentPos, k -> new HashSet<>()).add(neighborPos);
                        networkConnections.computeIfAbsent(neighborPos, k -> new HashSet<>()).add(currentPos);
                    }
                }
            }

            // Update scan progress
            scanProgress = (scannedCount * 100) / Math.min(MAX_SCAN_RANGE, scanQueue.size() + scannedCount);
        }

        // Calculate visual map positions
        calculateMapPositions();

        // Update network statistics
        updateNetworkStatistics();

        isScanning = false;
        networkMapped = true;

        addRecentEvent("Network scan completed. Found " + networkDevices.size() + " devices.");
        setChanged();
    }

    /**
     * Scan a specific position for energy-related devices
     */
    private NetworkDevice scanDeviceAtPosition(BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return null;

        // Check if block has energy capability
        var energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
        if (energyCap == null) return null;

        // Get block information
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        String blockName = block.getName().getString();
        ResourceLocation blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);

        // Determine device type
        String deviceType = classifyDevice(blockEntity, blockName);

        // Create network device
        NetworkDevice device = new NetworkDevice(pos, blockName, blockId, deviceType);
        device.updatePerformanceData(energyCap, blockEntity);

        // Set active state
        if (blockEntity instanceof BaseMachineBlockEntity machine) {
            device.isActive = machine.isActive();
            device.efficiency = (float) machine.getEfficiencyMultiplier() * 100;
        }

        return device;
    }

    /**
     * Classify device type based on block entity
     */
    private String classifyDevice(BlockEntity blockEntity, String blockName) {
        String name = blockName.toLowerCase();

        if (name.contains("generator")) return "generator";
        if (name.contains("storage") || name.contains("battery")) return "storage";
        if (name.contains("cable") || name.contains("wire")) return "cable";
        if (name.contains("controller") || name.contains("monitor")) return "controller";
        if (name.contains("converter") || name.contains("transformer")) return "converter";

        return "machine"; // Default type
    }

    /**
     * Check if position is within scan range
     */
    private boolean isWithinScanRange(BlockPos pos) {
        return worldPosition.closerThan(pos, MAX_SCAN_RANGE);
    }

    /**
     * Check if position has energy connection capability
     */
    private boolean hasEnergyConnection(BlockPos pos) {
        var energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
        return energyCap != null;
    }

    /**
     * Calculate visual map positions for network topology display
     */
    private void calculateMapPositions() {
        if (networkDevices.isEmpty()) return;

        // Simple grid-based layout algorithm
        // In a real implementation, this would use graph layout algorithms

        List<NetworkDevice> devices = new ArrayList<>(networkDevices.values());
        int gridSize = (int) Math.ceil(Math.sqrt(devices.size()));

        for (int i = 0; i < devices.size(); i++) {
            NetworkDevice device = devices.get(i);
            device.mapX = (i % gridSize) * 50; // 50 pixel spacing
            device.mapY = (i / gridSize) * 50;
        }
    }

    /**
     * Update network performance analysis
     */
    private void updateNetworkPerformance() {
        if (!networkMapped) return;

        performanceIssues.clear();
        totalDevices = networkDevices.size();
        activeDevices = 0;
        overloadedDevices = 0;
        underperformingDevices = 0;
        totalNetworkEnergy = 0L;
        maxNetworkCapacity = 0L;

        // Analyze each device
        for (NetworkDevice device : networkDevices.values()) {
            // Update device performance data
            BlockEntity blockEntity = level.getBlockEntity(device.position);
            if (blockEntity != null) {
                var energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK, device.position, null);
                device.updatePerformanceData(energyCap, blockEntity);
            }

            // Update counters
            if (device.isActive) activeDevices++;
            if (device.isOverloaded) {
                overloadedDevices++;
                performanceIssues.add(device);
            }
            if (device.isUnderperforming) {
                underperformingDevices++;
                performanceIssues.add(device);
            }

            totalNetworkEnergy += device.energyStored;
            maxNetworkCapacity += device.maxEnergyStored;
        }

        // Calculate network efficiency
        if (maxNetworkCapacity > 0) {
            networkEfficiency = ((float) totalNetworkEnergy / maxNetworkCapacity) * 100;
        }

        // Check network stability
        networkStable = (overloadedDevices == 0) && (underperformingDevices < totalDevices * 0.1);

        // Generate alerts
        updateNetworkAlerts();
    }

    /**
     * Update network alerts based on performance analysis
     */
    private void updateNetworkAlerts() {
        networkAlerts.clear();

        if (overloadedDevices > 0) {
            networkAlerts.add("WARNING: " + overloadedDevices + " devices overloaded");
        }

        if (underperformingDevices > totalDevices * 0.2) {
            networkAlerts.add("NOTICE: High number of underperforming devices");
        }

        if (networkEfficiency < 50.0f) {
            networkAlerts.add("WARNING: Network efficiency below 50%");
        }

        if (!networkStable) {
            networkAlerts.add("ALERT: Network instability detected");
        }

        if (networkAlerts.isEmpty()) {
            networkAlerts.add("All systems operational");
        }
    }

    /**
     * Check for NetworkBridge multiblock connection
     */
    private void checkNetworkBridgeConnection() {
        // Scan nearby for NetworkBridge block entity
        hasNetworkBridge = false;
        connectedBridgePos = null;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);

            // Check if neighbor is a NetworkBridge
            if (neighbor instanceof NetworkBridgeBlockEntity bridge) {
                hasNetworkBridge = true;
                connectedBridgePos = neighborPos;
                break;
            }
        }

        // Enable remote control only if bridge is connected
        remoteControlEnabled = hasNetworkBridge;
    }

    /**
     * Update network statistics
     */
    private void updateNetworkStatistics() {
        totalDevices = networkDevices.size();
        // Other statistics updated in updateNetworkPerformance()
    }

    /**
     * Add event to recent events log
     */
    private void addRecentEvent(String event) {
        recentEvents.offer("[" + (level != null ? level.getGameTime() : 0) + "] " + event);
        if (recentEvents.size() > MAX_RECENT_EVENTS) {
            recentEvents.poll();
        }
    }

    @Override
    protected boolean canOperate() {
        return energyStorage.getEnergyStored() >= 50;
    }

    @Override
    protected void performOperation() {
        // Main operation logic is handled in serverTick
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.network_dashboard");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.NetworkDashboardMenu(containerId, playerInventory, this);
    }

    // ========== GUI DATA METHODS ==========

    // Tab 1 - Network Overview
    public boolean isNetworkMapped() { return networkMapped; }
    public boolean isScanning() { return isScanning; }
    public int getScanProgress() { return scanProgress; }
    public int getTotalDevices() { return totalDevices; }
    public int getActiveDevices() { return activeDevices; }
    public Map<BlockPos, NetworkDevice> getNetworkDevices() { return new HashMap<>(networkDevices); }
    public Map<BlockPos, Set<BlockPos>> getNetworkConnections() { return new HashMap<>(networkConnections); }

    // Tab 2 - Remote Control (requires NetworkBridge)
    public boolean hasNetworkBridge() { return hasNetworkBridge; }
    public boolean isRemoteControlEnabled() { return remoteControlEnabled; }
    public BlockPos getConnectedBridgePos() { return connectedBridgePos; }
    public int getOverloadedDevices() { return overloadedDevices; }
    public int getUnderperformingDevices() { return underperformingDevices; }
    public List<NetworkDevice> getPerformanceIssues() { return new ArrayList<>(performanceIssues); }

    // Network Statistics
    public long getTotalNetworkEnergy() { return totalNetworkEnergy; }
    public long getMaxNetworkCapacity() { return maxNetworkCapacity; }
    public float getNetworkEfficiency() { return networkEfficiency; }
    public boolean isNetworkStable() { return networkStable; }
    public List<String> getNetworkAlerts() { return new ArrayList<>(networkAlerts); }
    public String[] getRecentEvents() { return recentEvents.toArray(new String[0]); }

    // Visual Map Control
    public void setMapCenter(int x, int y) { this.mapCenterX = x; this.mapCenterY = y; }
    public void setMapZoom(float zoom) { this.mapZoom = Math.max(0.1f, Math.min(3.0f, zoom)); }
    public void selectDevice(BlockPos pos) {
        selectedDevice = networkDevices.get(pos);
        setChanged();
    }
    public NetworkDevice getSelectedDevice() { return selectedDevice; }

    // Control Methods
    public void requestNetworkScan() {
        if (!isScanning && canScanNetwork()) {
            lastScanTime = 0; // Force immediate scan
            addRecentEvent("Manual network scan requested");
        }
    }

    public void clearNetworkData() {
        networkDevices.clear();
        networkConnections.clear();
        performanceIssues.clear();
        networkMapped = false;
        addRecentEvent("Network data cleared");
        setChanged();
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save scanning state
        tag.putBoolean("IsScanning", isScanning);
        tag.putBoolean("NetworkMapped", networkMapped);
        tag.putInt("ScanProgress", scanProgress);
        tag.putLong("LastScanTime", lastScanTime);

        // Save multiblock state
        tag.putBoolean("HasNetworkBridge", hasNetworkBridge);
        tag.putBoolean("RemoteControlEnabled", remoteControlEnabled);
        if (connectedBridgePos != null) {
            tag.putLong("ConnectedBridgePos", connectedBridgePos.asLong());
        }

        // Save network statistics
        tag.putInt("TotalDevices", totalDevices);
        tag.putInt("ActiveDevices", activeDevices);
        tag.putInt("OverloadedDevices", overloadedDevices);
        tag.putInt("UnderperformingDevices", underperformingDevices);
        tag.putLong("TotalNetworkEnergy", totalNetworkEnergy);
        tag.putLong("MaxNetworkCapacity", maxNetworkCapacity);
        tag.putFloat("NetworkEfficiency", networkEfficiency);
        tag.putBoolean("NetworkStable", networkStable);

        // Save visual map settings
        tag.putInt("MapCenterX", mapCenterX);
        tag.putInt("MapCenterY", mapCenterY);
        tag.putFloat("MapZoom", mapZoom);

        // Save recent events
        ListTag eventsTag = new ListTag();
        for (String event : recentEvents) {
            CompoundTag eventTag = new CompoundTag();
            eventTag.putString("Event", event);
            eventsTag.add(eventTag);
        }
        tag.put("RecentEvents", eventsTag);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load scanning state
        isScanning = tag.getBoolean("IsScanning");
        networkMapped = tag.getBoolean("NetworkMapped");
        scanProgress = tag.getInt("ScanProgress");
        lastScanTime = tag.getLong("LastScanTime");

        // Load multiblock state
        hasNetworkBridge = tag.getBoolean("HasNetworkBridge");
        remoteControlEnabled = tag.getBoolean("RemoteControlEnabled");
        if (tag.contains("ConnectedBridgePos")) {
            connectedBridgePos = BlockPos.of(tag.getLong("ConnectedBridgePos"));
        }

        // Load network statistics
        totalDevices = tag.getInt("TotalDevices");
        activeDevices = tag.getInt("ActiveDevices");
        overloadedDevices = tag.getInt("OverloadedDevices");
        underperformingDevices = tag.getInt("UnderperformingDevices");
        totalNetworkEnergy = tag.getLong("TotalNetworkEnergy");
        maxNetworkCapacity = tag.getLong("MaxNetworkCapacity");
        networkEfficiency = tag.getFloat("NetworkEfficiency");
        networkStable = tag.getBoolean("NetworkStable");

        // Load visual map settings
        mapCenterX = tag.getInt("MapCenterX");
        mapCenterY = tag.getInt("MapCenterY");
        mapZoom = tag.getFloat("MapZoom");

        // Load recent events
        recentEvents.clear();
        if (tag.contains("RecentEvents")) {
            ListTag eventsTag = tag.getList("RecentEvents", Tag.TAG_COMPOUND);
            for (int i = 0; i < eventsTag.size(); i++) {
                CompoundTag eventTag = eventsTag.getCompound(i);
                recentEvents.offer(eventTag.getString("Event"));
            }
        }
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send dashboard data to client for GUI updates
        tag.putBoolean("Scanning", isScanning);
        tag.putBoolean("Mapped", networkMapped);
        tag.putInt("Progress", scanProgress);
        tag.putBoolean("HasBridge", hasNetworkBridge);
        tag.putBoolean("RemoteControl", remoteControlEnabled);
        tag.putInt("TotalDevices", totalDevices);
        tag.putInt("ActiveDevices", activeDevices);
        tag.putFloat("Efficiency", networkEfficiency);
        tag.putBoolean("Stable", networkStable);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive dashboard data from server
        isScanning = tag.getBoolean("Scanning");
        networkMapped = tag.getBoolean("Mapped");
        scanProgress = tag.getInt("Progress");
        hasNetworkBridge = tag.getBoolean("HasBridge");
        remoteControlEnabled = tag.getBoolean("RemoteControl");
        totalDevices = tag.getInt("TotalDevices");
        activeDevices = tag.getInt("ActiveDevices");
        networkEfficiency = tag.getFloat("Efficiency");
        networkStable = tag.getBoolean("Stable");
    }
}