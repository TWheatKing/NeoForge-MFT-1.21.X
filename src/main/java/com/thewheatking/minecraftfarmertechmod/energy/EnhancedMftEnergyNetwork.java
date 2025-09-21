package com.thewheatking.minecraftfarmertechmod.energy;

import com.thewheatking.minecraftfarmertechmod.common.util.CableUtils;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission.EnergyTransmissionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enhanced energy network system for managing complex energy distribution
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/energy/EnhancedMftEnergyNetwork.java
 * Purpose: Advanced energy network management with optimization and load balancing
 */
public class EnhancedMftEnergyNetwork {

    // Network management
    private static final Map<String, EnhancedMftEnergyNetwork> activeNetworks = new ConcurrentHashMap<>();
    private static final int MAX_NETWORK_UPDATE_TIME = 50; // Max 50ms per network update

    // Network properties
    private final String networkId;
    private final Level level;
    private final Set<BlockPos> cablePositions;
    private final Map<BlockPos, EnergyNode> energyNodes;
    private final List<EnergyConnection> connections;

    // Performance optimization
    private long lastUpdateTime = 0;
    private boolean isDirty = true;
    private int updatePriority = 1;
    private final Map<BlockPos, Double> nodeLoadCache = new ConcurrentHashMap<>();

    // Energy flow management
    private final Queue<EnergyTransfer> pendingTransfers = new LinkedList<>();
    private int totalEnergyTransferred = 0;
    private double networkEfficiency = 1.0;

    // Network statistics
    private int totalProducers = 0;
    private int totalConsumers = 0;
    private int totalStorage = 0;
    private long energyProduced = 0;
    private long energyConsumed = 0;

    /**
     * Energy node representing a device connected to the network
     */
    public static class EnergyNode {
        public final BlockPos position;
        public final NodeType type;
        public IEnergyStorage energyStorage;
        public HybridEnergyStorage hybridStorage;
        public int priority;
        public double loadFactor;
        public long lastAccessed;

        public enum NodeType {
            PRODUCER,   // Generates energy
            CONSUMER,   // Uses energy
            STORAGE,    // Stores energy
            HYBRID      // Can produce, consume, and store
        }

        public EnergyNode(BlockPos position, NodeType type, IEnergyStorage energyStorage) {
            this.position = position;
            this.type = type;
            this.energyStorage = energyStorage;
            this.priority = 1;
            this.loadFactor = 0.0;
            this.lastAccessed = System.currentTimeMillis();

            if (energyStorage instanceof HybridEnergyStorage hybrid) {
                this.hybridStorage = hybrid;
            }
        }
    }

    /**
     * Energy connection between two nodes
     */
    public static class EnergyConnection {
        public final BlockPos from;
        public final BlockPos to;
        public final List<BlockPos> path;
        public final double efficiency;
        public final int transferRate;
        public double currentLoad;

        public EnergyConnection(BlockPos from, BlockPos to, List<BlockPos> path, double efficiency, int transferRate) {
            this.from = from;
            this.to = to;
            this.path = new ArrayList<>(path);
            this.efficiency = efficiency;
            this.transferRate = transferRate;
            this.currentLoad = 0.0;
        }
    }

    /**
     * Energy transfer request
     */
    public static class EnergyTransfer {
        public final BlockPos from;
        public final BlockPos to;
        public final int amount;
        public final boolean isMft;
        public final int priority;

        public EnergyTransfer(BlockPos from, BlockPos to, int amount, boolean isMft, int priority) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.isMft = isMft;
            this.priority = priority;
        }
    }

    /**
     * Creates a new network or returns existing one
     */
    public static EnhancedMftEnergyNetwork getOrCreateNetwork(Level level, BlockPos startPos) {
        String networkId = generateNetworkId(level, startPos);

        return activeNetworks.computeIfAbsent(networkId, id -> {
            EnhancedMftEnergyNetwork network = new EnhancedMftEnergyNetwork(id, level, startPos);
            network.scanNetwork();
            return network;
        });
    }

    /**
     * Removes a network from active management
     */
    public static void removeNetwork(String networkId) {
        activeNetworks.remove(networkId);
    }

    /**
     * Updates all active networks
     */
    public static void updateAllNetworks() {
        List<CompletableFuture<Void>> updateTasks = new ArrayList<>();

        for (EnhancedMftEnergyNetwork network : activeNetworks.values()) {
            updateTasks.add(CompletableFuture.runAsync(network::updateNetwork));
        }

        // Wait for all networks to update (with timeout)
        CompletableFuture.allOf(updateTasks.toArray(new CompletableFuture[0]))
                .orTimeout(MAX_NETWORK_UPDATE_TIME * activeNetworks.size(),
                        java.util.concurrent.TimeUnit.MILLISECONDS)
                .join();
    }

    private static String generateNetworkId(Level level, BlockPos pos) {
        return level.dimension().location().toString() + "_" + pos.toShortString();
    }

    private EnhancedMftEnergyNetwork(String networkId, Level level, BlockPos startPos) {
        this.networkId = networkId;
        this.level = level;
        this.cablePositions = CableUtils.findConnectedCables(level, startPos);
        this.energyNodes = new ConcurrentHashMap<>();
        this.connections = new CopyOnWriteArrayList<>();

        // Add the starting position if it's a cable
        if (CableUtils.isCable(level, startPos)) {
            this.cablePositions.add(startPos);
        }
    }

    /**
     * Scans the network to find all connected energy devices
     */
    public void scanNetwork() {
        energyNodes.clear();
        connections.clear();

        // Find all energy devices connected to the cable network
        Map<BlockPos, IEnergyStorage> devices = CableUtils.findEnergyDevices(level, cablePositions);

        // Create nodes for each device
        for (Map.Entry<BlockPos, IEnergyStorage> entry : devices.entrySet()) {
            BlockPos pos = entry.getKey();
            IEnergyStorage storage = entry.getValue();

            EnergyNode.NodeType type = determineNodeType(storage);
            EnergyNode node = new EnergyNode(pos, type, storage);
            energyNodes.put(pos, node);
        }

        // Create connections between nodes
        createConnections();

        // Update statistics
        updateNetworkStatistics();

        isDirty = false;
    }

    private EnergyNode.NodeType determineNodeType(IEnergyStorage storage) {
        boolean canReceive = storage.canReceive();
        boolean canExtract = storage.canExtract();

        if (canReceive && canExtract) {
            return EnergyNode.NodeType.HYBRID;
        } else if (canReceive) {
            return EnergyNode.NodeType.CONSUMER;
        } else if (canExtract) {
            return EnergyNode.NodeType.PRODUCER;
        } else {
            return EnergyNode.NodeType.STORAGE;
        }
    }

    private void createConnections() {
        List<BlockPos> nodePositions = new ArrayList<>(energyNodes.keySet());

        for (int i = 0; i < nodePositions.size(); i++) {
            for (int j = i + 1; j < nodePositions.size(); j++) {
                BlockPos pos1 = nodePositions.get(i);
                BlockPos pos2 = nodePositions.get(j);

                List<BlockPos> path = CableUtils.findOptimalPath(level, pos1, pos2);
                if (!path.isEmpty()) {
                    double efficiency = 1.0 - CableUtils.calculateEnergyLoss(level, path);
                    int transferRate = calculatePathTransferRate(path);

                    connections.add(new EnergyConnection(pos1, pos2, path, efficiency, transferRate));
                    connections.add(new EnergyConnection(pos2, pos1, path, efficiency, transferRate));
                }
            }
        }
    }

    private int calculatePathTransferRate(List<BlockPos> path) {
        int minTransferRate = Integer.MAX_VALUE;

        for (BlockPos pos : path) {
            int rate = CableUtils.getCableTransferRate(level, pos);
            minTransferRate = Math.min(minTransferRate, rate);
        }

        return minTransferRate == Integer.MAX_VALUE ? 0 : minTransferRate;
    }

    /**
     * Updates the network energy distribution
     */
    public void updateNetwork() {
        long startTime = System.currentTimeMillis();

        try {
            if (isDirty) {
                scanNetwork();
            }

            // Process pending transfers
            processPendingTransfers();

            // Distribute energy optimally
            distributeEnergy();

            // Update node priorities based on usage
            updateNodePriorities();

            // Calculate network efficiency
            calculateNetworkEfficiency();

            lastUpdateTime = System.currentTimeMillis();

        } catch (Exception e) {
            // Log error and mark network for rescanning
            isDirty = true;
        }
    }

    private void processPendingTransfers() {
        while (!pendingTransfers.isEmpty()) {
            EnergyTransfer transfer = pendingTransfers.poll();
            processEnergyTransfer(transfer);
        }
    }

    private void processEnergyTransfer(EnergyTransfer transfer) {
        EnergyNode fromNode = energyNodes.get(transfer.from);
        EnergyNode toNode = energyNodes.get(transfer.to);

        if (fromNode == null || toNode == null) return;

        // Find the best connection between nodes
        EnergyConnection bestConnection = findBestConnection(transfer.from, transfer.to);
        if (bestConnection == null) return;

        // Calculate actual transfer amount considering efficiency and capacity
        int maxTransfer = Math.min(transfer.amount, bestConnection.transferRate);
        int actualTransfer = 0;

        if (transfer.isMft && fromNode.hybridStorage != null && toNode.hybridStorage != null) {
            // MFT energy transfer
            double mftAmount = maxTransfer * HybridEnergyStorage.getFeToMftRatio();
            double extracted = fromNode.hybridStorage.extractMftEnergy(mftAmount, false);
            double received = toNode.hybridStorage.receiveMftEnergy(extracted * bestConnection.efficiency, false);
            actualTransfer = (int) (received * HybridEnergyStorage.getMftToFeRatio());
        } else {
            // Standard FE transfer
            int extracted = fromNode.energyStorage.extractEnergy(maxTransfer, false);
            int received = toNode.energyStorage.receiveEnergy((int) (extracted * bestConnection.efficiency), false);
            actualTransfer = received;
        }

        // Update connection load
        bestConnection.currentLoad += (double) actualTransfer / bestConnection.transferRate;
        totalEnergyTransferred += actualTransfer;

        // Update node access times
        fromNode.lastAccessed = System.currentTimeMillis();
        toNode.lastAccessed = System.currentTimeMillis();
    }

    private EnergyConnection findBestConnection(BlockPos from, BlockPos to) {
        return connections.stream()
                .filter(conn -> conn.from.equals(from) && conn.to.equals(to))
                .filter(conn -> conn.currentLoad < 1.0) // Not at capacity
                .max(Comparator.comparingDouble(conn -> conn.efficiency * (1.0 - conn.currentLoad)))
                .orElse(null);
    }

    private void distributeEnergy() {
        // Get all producers with available energy
        List<EnergyNode> producers = energyNodes.values().stream()
                .filter(node -> node.type == EnergyNode.NodeType.PRODUCER ||
                        node.type == EnergyNode.NodeType.HYBRID)
                .filter(node -> node.energyStorage.canExtract() && node.energyStorage.getEnergyStored() > 0)
                .sorted(Comparator.comparingInt((EnergyNode n) -> n.priority).reversed())
                .toList();

        // Get all consumers that need energy
        List<EnergyNode> consumers = energyNodes.values().stream()
                .filter(node -> node.type == EnergyNode.NodeType.CONSUMER ||
                        node.type == EnergyNode.NodeType.HYBRID ||
                        node.type == EnergyNode.NodeType.STORAGE)
                .filter(node -> node.energyStorage.canReceive() &&
                        node.energyStorage.getEnergyStored() < node.energyStorage.getMaxEnergyStored())
                .sorted(Comparator.comparingInt((EnergyNode n) -> n.priority).reversed())
                .toList();

        // Distribute energy from producers to consumers
        for (EnergyNode producer : producers) {
            for (EnergyNode consumer : consumers) {
                if (producer.position.equals(consumer.position)) continue;

                int availableEnergy = producer.energyStorage.getEnergyStored();
                int neededEnergy = consumer.energyStorage.getMaxEnergyStored() - consumer.energyStorage.getEnergyStored();

                if (availableEnergy > 0 && neededEnergy > 0) {
                    int transferAmount = Math.min(availableEnergy, neededEnergy);

                    // Determine if this should be an MFT transfer
                    boolean useMft = producer.hybridStorage != null && consumer.hybridStorage != null &&
                            (producer.hybridStorage.getPriority() == HybridEnergyStorage.EnergyPriority.MFT_ENERGY_FIRST ||
                                    consumer.hybridStorage.getPriority() == HybridEnergyStorage.EnergyPriority.MFT_ENERGY_FIRST);

                    EnergyTransfer transfer = new EnergyTransfer(producer.position, consumer.position,
                            transferAmount, useMft,
                            Math.max(producer.priority, consumer.priority));
                    pendingTransfers.offer(transfer);
                }
            }
        }
    }

    private void updateNodePriorities() {
        for (EnergyNode node : energyNodes.values()) {
            // Increase priority for frequently accessed nodes
            long timeSinceAccess = System.currentTimeMillis() - node.lastAccessed;
            if (timeSinceAccess < 1000) { // Less than 1 second
                node.priority = Math.min(node.priority + 1, 10);
            } else if (timeSinceAccess > 10000) { // More than 10 seconds
                node.priority = Math.max(node.priority - 1, 1);
            }

            // Adjust priority based on energy level
            double energyRatio = (double) node.energyStorage.getEnergyStored() /
                    Math.max(node.energyStorage.getMaxEnergyStored(), 1);

            if (node.type == EnergyNode.NodeType.PRODUCER && energyRatio > 0.8) {
                node.priority += 1; // High energy producers get higher priority
            } else if (node.type == EnergyNode.NodeType.CONSUMER && energyRatio < 0.2) {
                node.priority += 1; // Low energy consumers get higher priority
            }
        }
    }

    private void calculateNetworkEfficiency() {
        if (connections.isEmpty()) {
            networkEfficiency = 1.0;
            return;
        }

        double totalEfficiency = connections.stream()
                .mapToDouble(conn -> conn.efficiency * (1.0 - conn.currentLoad))
                .average()
                .orElse(1.0);

        networkEfficiency = totalEfficiency;

        // Reset connection loads for next cycle
        connections.forEach(conn -> conn.currentLoad = 0.0);
    }

    private void updateNetworkStatistics() {
        totalProducers = (int) energyNodes.values().stream()
                .filter(node -> node.type == EnergyNode.NodeType.PRODUCER).count();

        totalConsumers = (int) energyNodes.values().stream()
                .filter(node -> node.type == EnergyNode.NodeType.CONSUMER).count();

        totalStorage = (int) energyNodes.values().stream()
                .filter(node -> node.type == EnergyNode.NodeType.STORAGE ||
                        node.type == EnergyNode.NodeType.HYBRID).count();
    }

    /**
     * Requests energy transfer between two positions
     */
    public boolean requestEnergyTransfer(BlockPos from, BlockPos to, int amount, boolean preferMft) {
        if (!energyNodes.containsKey(from) || !energyNodes.containsKey(to)) {
            return false;
        }

        EnergyTransfer transfer = new EnergyTransfer(from, to, amount, preferMft, 5);
        pendingTransfers.offer(transfer);
        return true;
    }

    /**
     * Marks the network as dirty (needs rescanning)
     */
    public void markDirty() {
        isDirty = true;
    }

    /**
     * Gets network statistics
     */
    public NetworkStats getNetworkStats() {
        return new NetworkStats(
                energyNodes.size(),
                cablePositions.size(),
                connections.size(),
                totalEnergyTransferred,
                networkEfficiency,
                totalProducers,
                totalConsumers,
                totalStorage
        );
    }

    /**
     * Network statistics record
     */
    public record NetworkStats(
            int totalNodes,
            int totalCables,
            int totalConnections,
            int totalEnergyTransferred,
            double networkEfficiency,
            int totalProducers,
            int totalConsumers,
            int totalStorage
    ) {}

    /**
     * Gets all energy nodes in the network
     */
    public Collection<EnergyNode> getEnergyNodes() {
        return new ArrayList<>(energyNodes.values());
    }

    /**
     * Gets all connections in the network
     */
    public List<EnergyConnection> getConnections() {
        return new ArrayList<>(connections);
    }

    /**
     * Gets the network ID
     */
    public String getNetworkId() {
        return networkId;
    }

    /**
     * Checks if the network contains a specific position
     */
    public boolean containsPosition(BlockPos pos) {
        return energyNodes.containsKey(pos) || cablePositions.contains(pos);
    }

    /**
     * Gets the optimal path between two positions in the network
     */
    public List<BlockPos> getOptimalPath(BlockPos from, BlockPos to) {
        return CableUtils.findOptimalPath(level, from, to);
    }
}