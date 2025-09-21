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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

/**
 * Energy Analyzer Block Entity - Cable Flow Monitor & Fuel Database
 *
 * WHAT IT DOES:
 * TAB 1 - Cable Flow Analysis: Monitors energy flow between two connected cables
 * TAB 2 - Fuel Database: Search items to see their energy generation values
 *
 * • Must be placed between two cables (cables on different sides)
 * • Real-time energy flow monitoring (input/output amounts)
 * • Cable efficiency reports and loss calculations
 * • Fuel item database with search functionality
 * • Only shows items that can actually generate energy
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/EnergyAnalyzerBlockEntity.java
 */
public class EnergyAnalyzerBlockEntity extends BaseMachineBlockEntity {

    // Energy Analyzer Specifications
    private static final int ENERGY_CAPACITY = 10000;        // 10,000 FE capacity
    private static final int ENERGY_TRANSFER_RATE = 1000;    // 1,000 FE/tick transfer
    private static final int INVENTORY_SIZE = 9;             // Upgrade/component slots

    // Cable Connection State
    private Direction inputCableSide = null;
    private Direction outputCableSide = null;
    private boolean hasTwoCables = false;
    private boolean isAnalyzing = false;

    // Energy Flow Monitoring (Tab 1)
    private int energyInput = 0;                 // FE/tick coming in
    private int energyOutput = 0;                // FE/tick going out
    private int energyThroughput = 0;            // Total FE/tick passing through
    private float cableEfficiency = 100.0f;     // Efficiency percentage
    private int energyLoss = 0;                  // FE/tick lost to inefficiency

    // Historical Data
    private final Queue<Integer> inputHistory = new LinkedList<>();
    private final Queue<Integer> outputHistory = new LinkedList<>();
    private final Queue<Float> efficiencyHistory = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 60;  // 3 seconds of data

    // Statistics
    private long totalEnergyTransferred = 0L;
    private long totalEnergyLost = 0L;
    private int peakThroughput = 0;
    private float averageEfficiency = 100.0f;

    // Analysis State
    private int analysisUpdateCounter = 0;
    private static final int ANALYSIS_UPDATE_INTERVAL = 5; // Update every 5 ticks

    // Fuel Database (Tab 2) - Built-in fuel value database
    private static final Map<Item, FuelData> FUEL_DATABASE = new HashMap<>();
    private String currentSearchQuery = "";
    private List<FuelData> searchResults = new ArrayList<>();

    // Fuel Data Structure
    public static class FuelData {
        public final Item item;
        public final String itemName;
        public final int burnTime;           // Ticks
        public final int totalFEGenerated;   // Total FE from full burn
        public final int fePerTick;          // FE generated per tick
        public final String fuelType;        // Coal, Wood, etc.

        public FuelData(Item item, String itemName, int burnTime, int fePerTick, String fuelType) {
            this.item = item;
            this.itemName = itemName;
            this.burnTime = burnTime;
            this.fePerTick = fePerTick;
            this.totalFEGenerated = burnTime * fePerTick;
            this.fuelType = fuelType;
        }
    }

    public EnergyAnalyzerBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.ENERGY_ANALYZER.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, INVENTORY_SIZE);

        initializeFuelDatabase();
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        // Scan for connected cables
        scanConnectedCables();

        // Perform energy flow analysis if we have two cables
        if (hasTwoCables) {
            performEnergyAnalysis();
        } else {
            resetAnalysisData();
        }

        // Update analysis data periodically
        if (++analysisUpdateCounter >= ANALYSIS_UPDATE_INTERVAL) {
            updateAnalysisStatistics();
            analysisUpdateCounter = 0;
        }

        // Update working state
        boolean wasWorking = isWorking;
        isWorking = isAnalyzing && hasTwoCables;

        // Sync to client if state changed
        if (wasWorking != isWorking && tickCounter % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Scan for cables connected to different sides
     */
    private void scanConnectedCables() {
        Direction newInputSide = null;
        Direction newOutputSide = null;
        int cableCount = 0;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);

            // Check if neighbor has energy capability (is a cable or energy device)
            var energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    neighborPos, direction.getOpposite());

            if (energyCap != null) {
                cableCount++;
                if (newInputSide == null) {
                    newInputSide = direction;
                } else if (newOutputSide == null) {
                    newOutputSide = direction;
                }
            }
        }

        // Update cable connection state
        inputCableSide = newInputSide;
        outputCableSide = newOutputSide;
        hasTwoCables = (cableCount >= 2 && inputCableSide != null && outputCableSide != null);

        if (!hasTwoCables) {
            isAnalyzing = false;
        }
    }

    /**
     * Perform energy flow analysis between the two connected cables
     */
    private void performEnergyAnalysis() {
        if (!hasTwoCables || inputCableSide == null || outputCableSide == null) return;

        isAnalyzing = true;

        // Get energy capabilities of both cables
        BlockPos inputPos = worldPosition.relative(inputCableSide);
        BlockPos outputPos = worldPosition.relative(outputCableSide);

        var inputCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                inputPos, inputCableSide.getOpposite());
        var outputCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                outputPos, outputCableSide.getOpposite());

        if (inputCap != null && outputCap != null) {
            // Measure energy flow by monitoring energy transfer
            measureEnergyFlow(inputCap, outputCap);

            // Calculate efficiency and losses
            calculateEfficiency();

            // Update historical data
            updateHistoricalData();
        }
    }

    /**
     * Measure energy flow between input and output cables
     */
    private void measureEnergyFlow(IEnergyStorage inputCable, IEnergyStorage outputCable) {
        // Estimate input by checking how much energy the input cable can provide
        energyInput = Math.min(ENERGY_TRANSFER_RATE, inputCable.extractEnergy(Integer.MAX_VALUE, true));

        // Estimate output by checking how much energy the output cable can accept
        int outputCapacity = outputCable.receiveEnergy(Integer.MAX_VALUE, true);
        energyOutput = Math.min(energyInput, outputCapacity);

        // Calculate throughput
        energyThroughput = energyOutput;

        // Track peak throughput
        peakThroughput = Math.max(peakThroughput, energyThroughput);

        // Update totals
        totalEnergyTransferred += energyThroughput;
    }

    /**
     * Calculate cable efficiency and energy losses
     */
    private void calculateEfficiency() {
        if (energyInput > 0) {
            energyLoss = energyInput - energyOutput;
            cableEfficiency = Math.max(0, 100.0f - ((float) energyLoss / energyInput) * 100.0f);
            totalEnergyLost += energyLoss;
        } else {
            energyLoss = 0;
            cableEfficiency = 100.0f;
        }
    }

    /**
     * Update historical data for trending
     */
    private void updateHistoricalData() {
        // Add new data points
        inputHistory.offer(energyInput);
        outputHistory.offer(energyOutput);
        efficiencyHistory.offer(cableEfficiency);

        // Maintain history size
        while (inputHistory.size() > MAX_HISTORY_SIZE) {
            inputHistory.poll();
        }
        while (outputHistory.size() > MAX_HISTORY_SIZE) {
            outputHistory.poll();
        }
        while (efficiencyHistory.size() > MAX_HISTORY_SIZE) {
            efficiencyHistory.poll();
        }
    }

    /**
     * Update analysis statistics
     */
    private void updateAnalysisStatistics() {
        if (!efficiencyHistory.isEmpty()) {
            averageEfficiency = (float) efficiencyHistory.stream()
                    .mapToDouble(Float::doubleValue)
                    .average()
                    .orElse(100.0);
        }
    }

    /**
     * Reset analysis data when cables disconnect
     */
    private void resetAnalysisData() {
        energyInput = 0;
        energyOutput = 0;
        energyThroughput = 0;
        energyLoss = 0;
        cableEfficiency = 100.0f;
        isAnalyzing = false;
    }

    @Override
    protected boolean canOperate() {
        return hasTwoCables && energyStorage.getEnergyStored() > 10;
    }

    @Override
    protected void performOperation() {
        // Main operation logic is handled in serverTick
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.energy_analyzer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.EnergyAnalyzerMenu(containerId, playerInventory, this);
    }

    // ========== FUEL DATABASE SYSTEM ==========

    /**
     * Initialize the built-in fuel database
     */
    private void initializeFuelDatabase() {
        if (!FUEL_DATABASE.isEmpty()) return; // Already initialized

        // Coal and Charcoal
        FUEL_DATABASE.put(Items.COAL, new FuelData(Items.COAL, "Coal", 1600, 40, "Coal"));
        FUEL_DATABASE.put(Items.CHARCOAL, new FuelData(Items.CHARCOAL, "Charcoal", 1600, 40, "Coal"));
        FUEL_DATABASE.put(Items.COAL_BLOCK, new FuelData(Items.COAL_BLOCK, "Coal Block", 16000, 40, "Coal"));

        // Wood Items
        FUEL_DATABASE.put(Items.STICK, new FuelData(Items.STICK, "Stick", 100, 20, "Wood"));
        FUEL_DATABASE.put(Items.WOODEN_PICKAXE, new FuelData(Items.WOODEN_PICKAXE, "Wooden Pickaxe", 200, 20, "Wood"));
        FUEL_DATABASE.put(Items.WOODEN_AXE, new FuelData(Items.WOODEN_AXE, "Wooden Axe", 200, 20, "Wood"));
        FUEL_DATABASE.put(Items.WOODEN_SHOVEL, new FuelData(Items.WOODEN_SHOVEL, "Wooden Shovel", 200, 20, "Wood"));
        FUEL_DATABASE.put(Items.WOODEN_HOE, new FuelData(Items.WOODEN_HOE, "Wooden Hoe", 200, 20, "Wood"));
        FUEL_DATABASE.put(Items.WOODEN_SWORD, new FuelData(Items.WOODEN_SWORD, "Wooden Sword", 200, 20, "Wood"));

        // Planks and Logs
        FUEL_DATABASE.put(Items.OAK_PLANKS, new FuelData(Items.OAK_PLANKS, "Oak Planks", 300, 25, "Wood"));
        FUEL_DATABASE.put(Items.BIRCH_PLANKS, new FuelData(Items.BIRCH_PLANKS, "Birch Planks", 300, 25, "Wood"));
        FUEL_DATABASE.put(Items.SPRUCE_PLANKS, new FuelData(Items.SPRUCE_PLANKS, "Spruce Planks", 300, 25, "Wood"));
        FUEL_DATABASE.put(Items.OAK_LOG, new FuelData(Items.OAK_LOG, "Oak Log", 300, 30, "Wood"));
        FUEL_DATABASE.put(Items.BIRCH_LOG, new FuelData(Items.BIRCH_LOG, "Birch Log", 300, 30, "Wood"));

        // Blaze and Lava
        FUEL_DATABASE.put(Items.BLAZE_ROD, new FuelData(Items.BLAZE_ROD, "Blaze Rod", 2400, 60, "Blaze"));
        FUEL_DATABASE.put(Items.LAVA_BUCKET, new FuelData(Items.LAVA_BUCKET, "Lava Bucket", 20000, 80, "Lava"));

        // Paper and Books
        FUEL_DATABASE.put(Items.PAPER, new FuelData(Items.PAPER, "Paper", 100, 15, "Paper"));
        FUEL_DATABASE.put(Items.BOOK, new FuelData(Items.BOOK, "Book", 300, 15, "Paper"));

        // Initialize search results with all items
        searchResults = new ArrayList<>(FUEL_DATABASE.values());
        searchResults.sort(Comparator.comparing(fuel -> fuel.itemName));
    }

    /**
     * Search fuel database by item name
     */
    public void searchFuelDatabase(String query) {
        currentSearchQuery = query.toLowerCase().trim();
        searchResults.clear();

        if (currentSearchQuery.isEmpty()) {
            // Show all items if no search query
            searchResults = new ArrayList<>(FUEL_DATABASE.values());
        } else {
            // Filter by name containing query
            for (FuelData fuelData : FUEL_DATABASE.values()) {
                if (fuelData.itemName.toLowerCase().contains(currentSearchQuery)) {
                    searchResults.add(fuelData);
                }
            }
        }

        // Sort results by name
        searchResults.sort(Comparator.comparing(fuel -> fuel.itemName));
        setChanged();
    }

    /**
     * Get fuel data for a specific item
     */
    public FuelData getFuelData(Item item) {
        return FUEL_DATABASE.get(item);
    }

    /**
     * Check if an item can be used as fuel
     */
    public boolean isFuelItem(Item item) {
        return FUEL_DATABASE.containsKey(item);
    }

    // ========== GUI DATA METHODS ==========

    // Tab 1 - Cable Analysis
    public boolean hasTwoCables() { return hasTwoCables; }
    public boolean isAnalyzing() { return isAnalyzing; }
    public Direction getInputCableSide() { return inputCableSide; }
    public Direction getOutputCableSide() { return outputCableSide; }

    public int getEnergyInput() { return energyInput; }
    public int getEnergyOutput() { return energyOutput; }
    public int getEnergyThroughput() { return energyThroughput; }
    public float getCableEfficiency() { return cableEfficiency; }
    public int getEnergyLoss() { return energyLoss; }

    public long getTotalEnergyTransferred() { return totalEnergyTransferred; }
    public long getTotalEnergyLost() { return totalEnergyLost; }
    public int getPeakThroughput() { return peakThroughput; }
    public float getAverageEfficiency() { return averageEfficiency; }

    public int[] getInputHistory() { return inputHistory.stream().mapToInt(Integer::intValue).toArray(); }
    public int[] getOutputHistory() { return outputHistory.stream().mapToInt(Integer::intValue).toArray(); }
    public float[] getEfficiencyHistory() { return efficiencyHistory.stream().mapToFloat(Float::floatValue).toArray(); }

    // Tab 2 - Fuel Database
    public String getCurrentSearchQuery() { return currentSearchQuery; }
    public List<FuelData> getSearchResults() { return new ArrayList<>(searchResults); }
    public int getSearchResultCount() { return searchResults.size(); }
    public int getTotalFuelTypes() { return FUEL_DATABASE.size(); }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save cable connection state
        if (inputCableSide != null) tag.putString("InputCableSide", inputCableSide.name());
        if (outputCableSide != null) tag.putString("OutputCableSide", outputCableSide.name());
        tag.putBoolean("HasTwoCables", hasTwoCables);
        tag.putBoolean("IsAnalyzing", isAnalyzing);

        // Save analysis data
        tag.putInt("EnergyInput", energyInput);
        tag.putInt("EnergyOutput", energyOutput);
        tag.putInt("EnergyThroughput", energyThroughput);
        tag.putFloat("CableEfficiency", cableEfficiency);
        tag.putInt("EnergyLoss", energyLoss);

        // Save statistics
        tag.putLong("TotalEnergyTransferred", totalEnergyTransferred);
        tag.putLong("TotalEnergyLost", totalEnergyLost);
        tag.putInt("PeakThroughput", peakThroughput);
        tag.putFloat("AverageEfficiency", averageEfficiency);

        // Save search query
        tag.putString("SearchQuery", currentSearchQuery);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load cable connection state
        if (tag.contains("InputCableSide")) {
            try {
                inputCableSide = Direction.valueOf(tag.getString("InputCableSide"));
            } catch (IllegalArgumentException e) {
                inputCableSide = null;
            }
        }
        if (tag.contains("OutputCableSide")) {
            try {
                outputCableSide = Direction.valueOf(tag.getString("OutputCableSide"));
            } catch (IllegalArgumentException e) {
                outputCableSide = null;
            }
        }
        hasTwoCables = tag.getBoolean("HasTwoCables");
        isAnalyzing = tag.getBoolean("IsAnalyzing");

        // Load analysis data
        energyInput = tag.getInt("EnergyInput");
        energyOutput = tag.getInt("EnergyOutput");
        energyThroughput = tag.getInt("EnergyThroughput");
        cableEfficiency = tag.getFloat("CableEfficiency");
        energyLoss = tag.getInt("EnergyLoss");

        // Load statistics
        totalEnergyTransferred = tag.getLong("TotalEnergyTransferred");
        totalEnergyLost = tag.getLong("TotalEnergyLost");
        peakThroughput = tag.getInt("PeakThroughput");
        averageEfficiency = tag.getFloat("AverageEfficiency");

        // Load search query and update results
        currentSearchQuery = tag.getString("SearchQuery");
        if (!currentSearchQuery.isEmpty()) {
            searchFuelDatabase(currentSearchQuery);
        }
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send analysis data to client for GUI updates
        tag.putBoolean("HasCables", hasTwoCables);
        tag.putBoolean("Analyzing", isAnalyzing);
        tag.putInt("Input", energyInput);
        tag.putInt("Output", energyOutput);
        tag.putFloat("Efficiency", cableEfficiency);
        tag.putString("Search", currentSearchQuery);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive analysis data from server
        hasTwoCables = tag.getBoolean("HasCables");
        isAnalyzing = tag.getBoolean("Analyzing");
        energyInput = tag.getInt("Input");
        energyOutput = tag.getInt("Output");
        cableEfficiency = tag.getFloat("Efficiency");

        String searchQuery = tag.getString("Search");
        if (!searchQuery.equals(currentSearchQuery)) {
            searchFuelDatabase(searchQuery);
        }
    }
}