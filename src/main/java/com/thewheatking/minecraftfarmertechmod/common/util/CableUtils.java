package com.thewheatking.minecraftfarmertechmod.common.util;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission.EnergyTransmissionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

/**
 * CORRECTED: Utility class for cable operations and energy transmission
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/util/CableUtils.java
 * Purpose: Provides utility methods for cable network operations and energy pathfinding
 */
public class CableUtils {

    /**
     * Maximum search radius for cable networks to prevent infinite loops
     */
    private static final int MAX_NETWORK_SIZE = 1000;

    /**
     * Cache for recently calculated paths to improve performance
     */
    private static final Map<String, List<BlockPos>> pathCache = new HashMap<>();
    private static final int CACHE_SIZE = 100;

    /**
     * Finds all connected cables in a network starting from a given position
     * @param level The world level
     * @param startPos Starting position for the search
     * @return Set of all connected cable positions
     */
    public static Set<BlockPos> findConnectedCables(Level level, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();

        toVisit.add(startPos);

        while (!toVisit.isEmpty() && visited.size() < MAX_NETWORK_SIZE) {
            BlockPos current = toVisit.poll();

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            // Check all adjacent positions for cables
            for (Direction direction : Direction.values()) {
                BlockPos adjacent = current.relative(direction);

                if (!visited.contains(adjacent) && isCable(level, adjacent)) {
                    toVisit.add(adjacent);
                }
            }
        }

        visited.remove(startPos); // Remove starting position from results
        return visited;
    }

    /**
     * Checks if a block at the given position is a cable
     * @param level The world level
     * @param pos Position to check
     * @return True if the block is a cable
     */
    public static boolean isCable(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof EnergyTransmissionBlockEntity;
    }

    /**
     * Gets the transfer rate for a cable at the given position
     * @param level The world level
     * @param pos Position of the cable
     * @return Transfer rate in FE/tick, or 0 if not a cable
     */
    public static int getCableTransferRate(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnergyTransmissionBlockEntity cable) {
            return cable.getTransferRate();
        }
        return 0;
    }

    /**
     * Finds the optimal path between two positions through cables
     * Uses A* pathfinding algorithm with cable transfer rates as weights
     * @param level The world level
     * @param start Starting position
     * @param end Ending position
     * @return List of positions forming the optimal path, or empty if no path exists
     */
    public static List<BlockPos> findOptimalPath(Level level, BlockPos start, BlockPos end) {
        String cacheKey = start.toString() + "->" + end.toString();

        // Check cache first
        if (pathCache.containsKey(cacheKey)) {
            return pathCache.get(cacheKey);
        }

        // A* pathfinding implementation
        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<BlockPos> closedSet = new HashSet<>();
        Map<BlockPos, PathNode> allNodes = new HashMap<>();

        PathNode startNode = new PathNode(start, 0, manhattanDistance(start, end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();

            if (current.pos.equals(end)) {
                List<BlockPos> path = reconstructPath(current);
                cacheResult(cacheKey, path);
                return path;
            }

            closedSet.add(current.pos);

            // Check all adjacent positions
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = current.pos.relative(direction);

                if (closedSet.contains(neighborPos) || !isCable(level, neighborPos)) {
                    continue;
                }

                int transferRate = getCableTransferRate(level, neighborPos);
                double tentativeGScore = current.gScore + (1.0 / Math.max(transferRate, 1)); // Higher transfer rate = lower cost

                PathNode neighbor = allNodes.computeIfAbsent(neighborPos,
                        pos -> new PathNode(pos, Double.MAX_VALUE, manhattanDistance(pos, end)));

                if (tentativeGScore < neighbor.gScore) {
                    neighbor.parent = current;
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = neighbor.gScore + neighbor.hScore;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path found
        List<BlockPos> emptyPath = new ArrayList<>();
        cacheResult(cacheKey, emptyPath);
        return emptyPath;
    }

    /**
     * Calculates Manhattan distance between two positions
     */
    private static double manhattanDistance(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    /**
     * Reconstructs the path from the end node to the start
     */
    private static List<BlockPos> reconstructPath(PathNode endNode) {
        List<BlockPos> path = new ArrayList<>();
        PathNode current = endNode;

        while (current != null) {
            path.add(current.pos);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Caches the result of a path calculation
     */
    private static void cacheResult(String key, List<BlockPos> path) {
        if (pathCache.size() >= CACHE_SIZE) {
            // Remove oldest entry
            String oldestKey = pathCache.keySet().iterator().next();
            pathCache.remove(oldestKey);
        }
        pathCache.put(key, new ArrayList<>(path));
    }

    /**
     * Finds all energy consumers and producers connected to a cable network
     * @param level The world level
     * @param networkPositions All cable positions in the network
     * @return Map of positions to their energy storage capabilities
     */
    public static Map<BlockPos, IEnergyStorage> findEnergyDevices(Level level, Set<BlockPos> networkPositions) {
        Map<BlockPos, IEnergyStorage> devices = new HashMap<>();

        for (BlockPos cablePos : networkPositions) {
            // Check all adjacent positions for energy devices
            for (Direction direction : Direction.values()) {
                BlockPos adjacent = cablePos.relative(direction);

                if (!networkPositions.contains(adjacent)) { // Not a cable
                    BlockEntity blockEntity = level.getBlockEntity(adjacent);
                    if (blockEntity != null) {
                        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, adjacent, direction.getOpposite());
                        if (energyStorage != null) {
                            devices.put(adjacent, energyStorage);
                        }
                    }
                }
            }
        }

        return devices;
    }

    /**
     * Calculates the total energy loss for a given path through cables
     * @param level The world level
     * @param path List of positions forming the path
     * @return Energy loss percentage (0.0 to 1.0)
     */
    public static double calculateEnergyLoss(Level level, List<BlockPos> path) {
        if (path.isEmpty()) {
            return 1.0; // 100% loss if no path
        }

        double totalLoss = 0.0;

        for (BlockPos pos : path) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EnergyTransmissionBlockEntity cable) {
                totalLoss += cable.getEnergyLossPerBlock();
            }
        }

        return Math.min(totalLoss, 1.0); // Cap at 100% loss
    }

    /**
     * Clears the path cache - useful for when the cable network changes
     */
    public static void clearCache() {
        pathCache.clear();
    }

    /**
     * Check if two cable tiers are compatible for connections
     */
    public static boolean areCompatible(com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier tier1,
                                        com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier tier2) {
        // For now, all cables are compatible - can be restricted later if needed
        return true;
    }

    /**
     * Calculate energy loss between different cable tiers (future feature)
     */
    public static float calculateTransferEfficiency(com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier from,
                                                    com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier to) {
        // Currently no loss between different tiers
        // Can be implemented later for more complex cable interactions
        return 1.0f;
    }

    /**
     * Get the maximum transfer rate for a cable network segment
     */
    public static int getNetworkTransferRate(com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier[] cablesInNetwork) {
        // Network is limited by the slowest cable
        int minTransferRate = Integer.MAX_VALUE;
        for (com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier tier : cablesInNetwork) {
            minTransferRate = Math.min(minTransferRate, tier.getTransferRate());
        }
        return minTransferRate;
    }

    /**
     * Easy method to add new cable tiers in the future
     * Just add to the TransferTier enum and they'll work automatically
     */
    public static boolean isValidCableTier(String tierName) {
        try {
            com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier.valueOf(tierName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get display name for cable tier
     */
    public static String getDisplayName(com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier tier) {
        return switch(tier) {
            case COPPER -> "Copper Cable";
            case COPPER_INSULATED -> "Insulated Copper Cable";
            case GOLD -> "Gold Cable";
            case GOLD_INSULATED -> "Insulated Gold Cable";
            case DIAMOND -> "Diamond Cable";
            case DIAMOND_INSULATED -> "Insulated Diamond Cable";
        };
    }

    /**
     * Get color for cable tier (for GUI rendering)
     */
    public static int getTierColor(com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage.TransferTier tier) {
        return switch(tier) {
            case COPPER, COPPER_INSULATED -> 0xFF8C4A; // Orange
            case GOLD, GOLD_INSULATED -> 0xFFD700; // Gold
            case DIAMOND, DIAMOND_INSULATED -> 0x00FFFF; // Cyan
        };
    }

    /**
     * Helper class for A* pathfinding
     */
    private static class PathNode {
        BlockPos pos;
        double gScore; // Cost from start
        double hScore; // Heuristic cost to end
        double fScore; // Total cost
        PathNode parent;

        public PathNode(BlockPos pos, double gScore, double hScore) {
            this.pos = pos;
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof PathNode other)) return false;
            return pos.equals(other.pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }
}