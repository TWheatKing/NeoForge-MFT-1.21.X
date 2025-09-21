package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyMonitorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

import static com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities.ENERGY_MONITOR;

public class EnergyMonitorBlockEntity extends BaseMachineBlockEntity {

    // Monitoring data
    private int energyInput = 0;
    private int energyOutput = 0;
    private int averageEnergyUsage = 0;
    private int peakEnergyUsage = 0;
    private int minEnergyUsage = Integer.MAX_VALUE;
    private long totalEnergyProcessed = 0L;

    // System status
    private boolean systemOverloaded = false;
    private float systemLoadPercentage = 0.0f;
    private int connectedDevicesCount = 0;
    private int activeDevicesCount = 0;
    private boolean monitoring = true;

    // Network efficiency
    private float networkEfficiency = 100.0f;
    private int energyLossRate = 0;

    // Alerts and warnings
    private final List<String> activeAlerts = new ArrayList<>();
    private boolean criticalOverload = false;
    private boolean energyStarved = false;
    private boolean systemStable = true;

    // Historical data (circular buffers)
    private final int[] energyHistory = new int[60]; // Last 60 measurements
    private final float[] efficiencyHistory = new float[60];
    private final float[] loadHistory = new float[60];
    private int historyIndex = 0;

    // Temperature and performance
    private int systemTemperature = 20; // Celsius
    private boolean overheating = false;
    private float performanceRating = 100.0f;

    // Energy quality
    private float energyQuality = 100.0f;
    private boolean energyFluctuations = false;
    private int voltageStability = 100;

    // Predictive analytics
    private int predictedEnergyDemand = 0;
    private long estimatedTimeToOverload = -1L;
    private boolean maintenanceRequired = false;

    // Configuration
    private int monitoringRange = 16;
    private boolean autoShutdownEnabled = false;
    private long lastUpdateTime = 0L;
    private int updateFrequency = 20; // Ticks between updates

    // Performance tracking
    private final Queue<Integer> usageHistory = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 100;
    private int tickCounter = 0;

    public EnergyMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ENERGY_MONITOR.get(), pos, state, 50000, 1000, 1000, 9);
        // Initialize arrays
        Arrays.fill(energyHistory, 0);
        Arrays.fill(efficiencyHistory, 100.0f);
        Arrays.fill(loadHistory, 0.0f);
    }

    @Override
    protected boolean canOperate() {
        return monitoring && energyStorage.getEnergyStored() > 10; // Needs minimal energy to monitor
    }

    @Override
    protected void performOperation() {
        if (++tickCounter >= updateFrequency) {
            updateMonitoringData();
            tickCounter = 0;
            lastUpdateTime = level.getGameTime();
        }
    }

    private void updateMonitoringData() {
        // Scan for connected energy devices in range
        scanEnergyNetwork();

        // Update energy flow measurements
        updateEnergyFlow();

        // Calculate system statistics
        calculateSystemMetrics();

        // Check for alerts and warnings
        checkSystemAlerts();

        // Update historical data
        updateHistoricalData();

        // Predictive analysis
        updatePredictions();

        setChanged();
    }

    private void scanEnergyNetwork() {
        connectedDevicesCount = 0;
        activeDevicesCount = 0;
        int totalInput = 0;
        int totalOutput = 0;

        // Scan in a cube around the monitor
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -monitoringRange; x <= monitoringRange; x++) {
            for (int y = -monitoringRange; y <= monitoringRange; y++) {
                for (int z = -monitoringRange; z <= monitoringRange; z++) {
                    mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);

                    if (level.getBlockEntity(mutablePos) instanceof IEnergyStorage energyBE) {
                        connectedDevicesCount++;

                        // Check if device is active (has energy or is processing)
                        if (energyBE.getEnergyStored() > 0 || energyBE.canReceive() || energyBE.canExtract()) {
                            activeDevicesCount++;
                        }

                        // Estimate input/output based on capability
                        if (energyBE.canExtract()) {
                            totalOutput += Math.min(energyBE.getEnergyStored(), 1000); // Estimate
                        }
                        if (energyBE.canReceive()) {
                            totalInput += Math.min(energyBE.getMaxEnergyStored() - energyBE.getEnergyStored(), 1000);
                        }
                    }
                }
            }
        }

        energyInput = totalInput;
        energyOutput = totalOutput;
    }

    private void updateEnergyFlow() {
        int currentUsage = Math.abs(energyInput - energyOutput);

        // Update usage history
        usageHistory.offer(currentUsage);
        if (usageHistory.size() > MAX_HISTORY_SIZE) {
            usageHistory.poll();
        }

        // Calculate statistics
        if (!usageHistory.isEmpty()) {
            averageEnergyUsage = (int) usageHistory.stream().mapToInt(Integer::intValue).average().orElse(0);
            peakEnergyUsage = usageHistory.stream().mapToInt(Integer::intValue).max().orElse(0);
            minEnergyUsage = usageHistory.stream().mapToInt(Integer::intValue).min().orElse(0);
        }

        totalEnergyProcessed += currentUsage;
    }

    private void calculateSystemMetrics() {
        // System load percentage
        int maxCapacity = connectedDevicesCount * 1000; // Estimated max capacity
        systemLoadPercentage = maxCapacity > 0 ? (float) (energyInput + energyOutput) / maxCapacity * 100 : 0;

        // Network efficiency (simplified calculation)
        if (energyInput > 0) {
            energyLossRate = Math.max(0, energyInput - energyOutput);
            networkEfficiency = Math.max(0, 100.0f - (float) energyLossRate / energyInput * 100);
        } else {
            networkEfficiency = 100.0f;
        }

        // System temperature (based on load)
        systemTemperature = 20 + (int) (systemLoadPercentage * 0.5f); // Base 20°C + load heating
        overheating = systemTemperature > 80;

        // Performance rating
        performanceRating = Math.max(0, 100.0f - systemLoadPercentage * 0.5f - (overheating ? 20 : 0));

        // Energy quality (affected by fluctuations and overload)
        energyQuality = Math.max(0, 100.0f - (systemOverloaded ? 30 : 0) - (energyFluctuations ? 10 : 0));
        voltageStability = (int) Math.max(0, 100 - systemLoadPercentage * 0.3f);
    }

    private void checkSystemAlerts() {
        activeAlerts.clear();

        // Check for overload
        systemOverloaded = systemLoadPercentage > 85.0f;
        criticalOverload = systemLoadPercentage > 95.0f;

        if (criticalOverload) {
            activeAlerts.add("CRITICAL: System overload detected!");
        } else if (systemOverloaded) {
            activeAlerts.add("WARNING: System approaching overload");
        }

        // Check for energy starvation
        energyStarved = activeDevicesCount > 0 && averageEnergyUsage < peakEnergyUsage * 0.3;
        if (energyStarved) {
            activeAlerts.add("WARNING: Insufficient energy supply");
        }

        // Check overheating
        if (overheating) {
            activeAlerts.add("WARNING: System overheating");
        }

        // Check for fluctuations
        if (usageHistory.size() > 10) {
            int variance = peakEnergyUsage - minEnergyUsage;
            energyFluctuations = variance > averageEnergyUsage * 0.5;
            if (energyFluctuations) {
                activeAlerts.add("NOTICE: Energy fluctuations detected");
            }
        }

        // Maintenance check
        maintenanceRequired = totalEnergyProcessed > 1000000L || overheating;
        if (maintenanceRequired) {
            activeAlerts.add("NOTICE: Maintenance recommended");
        }

        systemStable = activeAlerts.isEmpty();

        // Auto shutdown if critical and enabled
        if (autoShutdownEnabled && criticalOverload) {
            triggerEmergencyShutdown();
        }
    }

    private void updateHistoricalData() {
        energyHistory[historyIndex] = averageEnergyUsage;
        efficiencyHistory[historyIndex] = networkEfficiency;
        loadHistory[historyIndex] = systemLoadPercentage;

        historyIndex = (historyIndex + 1) % energyHistory.length;
    }

    private void updatePredictions() {
        if (usageHistory.size() >= 10) {
            // Simple trend analysis
            List<Integer> recent = new ArrayList<>(usageHistory).subList(
                    Math.max(0, usageHistory.size() - 10), usageHistory.size());

            double trend = calculateTrend(recent);
            predictedEnergyDemand = (int) Math.max(0, averageEnergyUsage + trend * 10);

            // Estimate time to overload
            if (trend > 0) {
                double currentLoad = systemLoadPercentage;
                double timeToOverload = (95.0 - currentLoad) / trend;
                estimatedTimeToOverload = timeToOverload > 0 ? (long) (timeToOverload * updateFrequency) : -1L;
            } else {
                estimatedTimeToOverload = -1L;
            }
        }
    }

    private double calculateTrend(List<Integer> data) {
        if (data.size() < 2) return 0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = data.size();

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += data.get(i);
            sumXY += i * data.get(i);
            sumX2 += i * i;
        }

        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }

    // Public getter methods for the menu
    public int getEnergyInput() { return energyInput; }
    public int getEnergyOutput() { return energyOutput; }
    public int getAverageEnergyUsage() { return averageEnergyUsage; }
    public int getPeakEnergyUsage() { return peakEnergyUsage; }
    public int getMinEnergyUsage() { return minEnergyUsage == Integer.MAX_VALUE ? 0 : minEnergyUsage; }
    public long getTotalEnergyProcessed() { return totalEnergyProcessed; }

    public boolean isSystemOverloaded() { return systemOverloaded; }
    public float getSystemLoadPercentage() { return systemLoadPercentage; }
    public int getConnectedDevicesCount() { return connectedDevicesCount; }
    public int getActiveDevicesCount() { return activeDevicesCount; }

    public float getNetworkEfficiency() { return networkEfficiency; }
    public int getEnergyLossRate() { return energyLossRate; }

    public boolean hasActiveAlerts() { return !activeAlerts.isEmpty(); }
    public int getAlertCount() { return activeAlerts.size(); }
    public String[] getActiveAlerts() { return activeAlerts.toArray(new String[0]); }

    public boolean isCriticalOverload() { return criticalOverload; }
    public boolean isEnergyStarved() { return energyStarved; }
    public boolean isSystemStable() { return systemStable; }

    public int[] getEnergyHistory() { return energyHistory.clone(); }
    public float[] getEfficiencyHistory() { return efficiencyHistory.clone(); }
    public float[] getLoadHistory() { return loadHistory.clone(); }

    public int getSystemTemperature() { return systemTemperature; }
    public boolean isOverheating() { return overheating; }
    public float getPerformanceRating() { return performanceRating; }

    public float getEnergyQuality() { return energyQuality; }
    public boolean hasEnergyFluctuations() { return energyFluctuations; }
    public int getVoltageStability() { return voltageStability; }

    public int getPredictedEnergyDemand() { return predictedEnergyDemand; }
    public long getEstimatedTimeToOverload() { return estimatedTimeToOverload; }
    public boolean isMaintenanceRequired() { return maintenanceRequired; }

    public int getMonitoringRange() { return monitoringRange; }
    public boolean isAutoShutdownEnabled() { return autoShutdownEnabled; }
    public boolean isMonitoring() { return monitoring; }
    public long getLastUpdateTime() { return lastUpdateTime; }
    public int getUpdateFrequency() { return updateFrequency; }

    // Control methods
    public void triggerEmergencyShutdown() {
        monitoring = false;
        activeAlerts.add("SYSTEM: Emergency shutdown activated");
        // Additional shutdown logic here
    }

    public void resetAlerts() {
        activeAlerts.clear();
        systemStable = true;
    }

    public void startDiagnostics() {
        // Reset statistics and start fresh monitoring
        Arrays.fill(energyHistory, 0);
        Arrays.fill(efficiencyHistory, 100.0f);
        Arrays.fill(loadHistory, 0.0f);
        usageHistory.clear();
        totalEnergyProcessed = 0L;
        activeAlerts.add("SYSTEM: Diagnostics started");
    }

    public void calibrateSystem() {
        // Recalibrate monitoring parameters
        updateFrequency = 20;
        monitoringRange = 16;
        activeAlerts.add("SYSTEM: Calibration complete");
    }

    public void setMonitoringRange(int range) {
        this.monitoringRange = Math.max(1, Math.min(32, range));
        setChanged();
    }

    public void setAutoShutdownEnabled(boolean enabled) {
        this.autoShutdownEnabled = enabled;
        setChanged();
    }

    public String generateSystemReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Energy Monitor System Report ===\n");
        report.append("Connected Devices: ").append(connectedDevicesCount).append("\n");
        report.append("Active Devices: ").append(activeDevicesCount).append("\n");
        report.append("Energy Input: ").append(energyInput).append(" FE/t\n");
        report.append("Energy Output: ").append(energyOutput).append(" FE/t\n");
        report.append("System Load: ").append(String.format("%.1f%%", systemLoadPercentage)).append("\n");
        report.append("Network Efficiency: ").append(String.format("%.1f%%", networkEfficiency)).append("\n");
        report.append("System Temperature: ").append(systemTemperature).append("°C\n");
        report.append("Performance Rating: ").append(String.format("%.1f%%", performanceRating)).append("\n");
        report.append("Active Alerts: ").append(activeAlerts.size()).append("\n");
        return report.toString();
    }

    public String[] getDetailedMetrics() {
        return new String[] {
                "Total Energy Processed: " + totalEnergyProcessed + " FE",
                "Peak Usage: " + peakEnergyUsage + " FE/t",
                "Min Usage: " + getMinEnergyUsage() + " FE/t",
                "Energy Quality: " + String.format("%.1f%%", energyQuality),
                "Voltage Stability: " + voltageStability + "%",
                "Predicted Demand: " + predictedEnergyDemand + " FE/t",
                "Maintenance Required: " + (maintenanceRequired ? "Yes" : "No")
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.energy_monitor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EnergyMonitorMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("energyInput", energyInput);
        tag.putInt("energyOutput", energyOutput);
        tag.putInt("averageEnergyUsage", averageEnergyUsage);
        tag.putInt("peakEnergyUsage", peakEnergyUsage);
        tag.putInt("minEnergyUsage", minEnergyUsage);
        tag.putLong("totalEnergyProcessed", totalEnergyProcessed);
        tag.putFloat("systemLoadPercentage", systemLoadPercentage);
        tag.putInt("connectedDevicesCount", connectedDevicesCount);
        tag.putInt("activeDevicesCount", activeDevicesCount);
        tag.putBoolean("monitoring", monitoring);
        tag.putFloat("networkEfficiency", networkEfficiency);
        tag.putInt("energyLossRate", energyLossRate);
        tag.putInt("monitoringRange", monitoringRange);
        tag.putBoolean("autoShutdownEnabled", autoShutdownEnabled);
        tag.putLong("lastUpdateTime", lastUpdateTime);
        tag.putInt("updateFrequency", updateFrequency);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energyInput = tag.getInt("energyInput");
        energyOutput = tag.getInt("energyOutput");
        averageEnergyUsage = tag.getInt("averageEnergyUsage");
        peakEnergyUsage = tag.getInt("peakEnergyUsage");
        minEnergyUsage = tag.getInt("minEnergyUsage");
        totalEnergyProcessed = tag.getLong("totalEnergyProcessed");
        systemLoadPercentage = tag.getFloat("systemLoadPercentage");
        connectedDevicesCount = tag.getInt("connectedDevicesCount");
        activeDevicesCount = tag.getInt("activeDevicesCount");
        monitoring = tag.getBoolean("monitoring");
        networkEfficiency = tag.getFloat("networkEfficiency");
        energyLossRate = tag.getInt("energyLossRate");
        monitoringRange = tag.getInt("monitoringRange");
        autoShutdownEnabled = tag.getBoolean("autoShutdownEnabled");
        lastUpdateTime = tag.getLong("lastUpdateTime");
        updateFrequency = tag.getInt("updateFrequency");
    }
}