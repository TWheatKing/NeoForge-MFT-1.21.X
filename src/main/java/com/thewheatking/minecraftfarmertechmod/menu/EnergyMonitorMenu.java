package com.thewheatking.minecraftfarmertechmod.menu;


import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyMonitorBlockEntity;
import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class EnergyMonitorMenu extends BaseEnergyStorageMenu {

    private final EnergyMonitorBlockEntity monitorBlockEntity;

    public EnergyMonitorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(getMenuType(), containerId, playerInventory, extraData);
        this.monitorBlockEntity = (EnergyMonitorBlockEntity) blockEntity;

        // No inventory slots needed - this is a monitoring interface only
    }

    // Constructor for server-side creation
    public EnergyMonitorMenu(int containerId, Inventory playerInventory, EnergyMonitorBlockEntity blockEntity) {
        super(getMenuType(), containerId, playerInventory, blockEntity);
        this.monitorBlockEntity = blockEntity;
    }

    private static MenuType<?> getMenuType() {
        // This will be set when the menu type is registered
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No inventory slots to move items between
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return monitorBlockEntity != null &&
                !monitorBlockEntity.isRemoved() &&
                player.distanceToSqr(monitorBlockEntity.getBlockPos().getX() + 0.5D,
                        monitorBlockEntity.getBlockPos().getY() + 0.5D,
                        monitorBlockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    // Getter methods for client-side UI to access monitor data
    public EnergyMonitorBlockEntity getMonitorBlockEntity() {
        return monitorBlockEntity;
    }

    // Energy Flow Monitoring
    public int getEnergyInput() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEnergyInput() : 0;
    }

    public int getEnergyOutput() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEnergyOutput() : 0;
    }

    public int getNetEnergyFlow() {
        return getEnergyInput() - getEnergyOutput();
    }

    public int getEnergyThroughput() {
        return getEnergyInput() + getEnergyOutput();
    }

    // Usage Statistics
    public int getAverageEnergyUsage() {
        return monitorBlockEntity != null ? monitorBlockEntity.getAverageEnergyUsage() : 0;
    }

    public int getPeakEnergyUsage() {
        return monitorBlockEntity != null ? monitorBlockEntity.getPeakEnergyUsage() : 0;
    }

    public int getMinEnergyUsage() {
        return monitorBlockEntity != null ? monitorBlockEntity.getMinEnergyUsage() : 0;
    }

    public long getTotalEnergyProcessed() {
        return monitorBlockEntity != null ? monitorBlockEntity.getTotalEnergyProcessed() : 0L;
    }

    // System Health Monitoring
    public boolean isSystemOverloaded() {
        return monitorBlockEntity != null && monitorBlockEntity.isSystemOverloaded();
    }

    public float getSystemLoadPercentage() {
        return monitorBlockEntity != null ? monitorBlockEntity.getSystemLoadPercentage() : 0.0f;
    }

    public int getConnectedDevicesCount() {
        return monitorBlockEntity != null ? monitorBlockEntity.getConnectedDevicesCount() : 0;
    }

    public int getActiveDevicesCount() {
        return monitorBlockEntity != null ? monitorBlockEntity.getActiveDevicesCount() : 0;
    }

    // Network Efficiency
    public float getNetworkEfficiency() {
        return monitorBlockEntity != null ? monitorBlockEntity.getNetworkEfficiency() : 0.0f;
    }

    public int getEnergyLossRate() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEnergyLossRate() : 0;
    }

    // Alert System
    public boolean hasActiveAlerts() {
        return monitorBlockEntity != null && monitorBlockEntity.hasActiveAlerts();
    }

    public int getAlertCount() {
        return monitorBlockEntity != null ? monitorBlockEntity.getAlertCount() : 0;
    }

    public String[] getActiveAlerts() {
        return monitorBlockEntity != null ? monitorBlockEntity.getActiveAlerts() : new String[0];
    }

    // Critical System Status
    public boolean isCriticalOverload() {
        return monitorBlockEntity != null && monitorBlockEntity.isCriticalOverload();
    }

    public boolean isEnergyStarved() {
        return monitorBlockEntity != null && monitorBlockEntity.isEnergyStarved();
    }

    public boolean isSystemStable() {
        return monitorBlockEntity != null && monitorBlockEntity.isSystemStable();
    }

    // Historical Data (last few measurements)
    public int[] getEnergyHistory() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEnergyHistory() : new int[0];
    }

    public float[] getEfficiencyHistory() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEfficiencyHistory() : new float[0];
    }

    public float[] getLoadHistory() {
        return monitorBlockEntity != null ? monitorBlockEntity.getLoadHistory() : new float[0];
    }

    // Temperature and Performance Monitoring
    public int getSystemTemperature() {
        return monitorBlockEntity != null ? monitorBlockEntity.getSystemTemperature() : 0;
    }

    public boolean isOverheating() {
        return monitorBlockEntity != null && monitorBlockEntity.isOverheating();
    }

    public float getPerformanceRating() {
        return monitorBlockEntity != null ? monitorBlockEntity.getPerformanceRating() : 0.0f;
    }

    // Energy Quality Monitoring
    public float getEnergyQuality() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEnergyQuality() : 100.0f;
    }

    public boolean hasEnergyFluctuations() {
        return monitorBlockEntity != null && monitorBlockEntity.hasEnergyFluctuations();
    }

    public int getVoltageStability() {
        return monitorBlockEntity != null ? monitorBlockEntity.getVoltageStability() : 100;
    }

    // Predictive Analytics
    public int getPredictedEnergyDemand() {
        return monitorBlockEntity != null ? monitorBlockEntity.getPredictedEnergyDemand() : 0;
    }

    public long getEstimatedTimeToOverload() {
        return monitorBlockEntity != null ? monitorBlockEntity.getEstimatedTimeToOverload() : -1L;
    }

    public boolean isMaintenanceRequired() {
        return monitorBlockEntity != null && monitorBlockEntity.isMaintenanceRequired();
    }

    // Control Methods (for emergency actions)
    public void triggerEmergencyShutdown() {
        if (monitorBlockEntity != null) {
            // Send packet to server to trigger emergency shutdown
            // Implementation depends on your networking system
        }
    }

    public void resetAlerts() {
        if (monitorBlockEntity != null) {
            // Send packet to server to reset all alerts
            // Implementation depends on your networking system
        }
    }

    public void startDiagnostics() {
        if (monitorBlockEntity != null) {
            // Send packet to server to start system diagnostics
            // Implementation depends on your networking system
        }
    }

    public void calibrateSystem() {
        if (monitorBlockEntity != null) {
            // Send packet to server to calibrate monitoring system
            // Implementation depends on your networking system
        }
    }

    // Data Export (for detailed analysis)
    public String exportSystemReport() {
        if (monitorBlockEntity != null) {
            return monitorBlockEntity.generateSystemReport();
        }
        return "No data available";
    }

    public String[] getDetailedMetrics() {
        return monitorBlockEntity != null ? monitorBlockEntity.getDetailedMetrics() : new String[0];
    }

    // Monitoring Range and Configuration
    public int getMonitoringRange() {
        return monitorBlockEntity != null ? monitorBlockEntity.getMonitoringRange() : 0;
    }

    public void setMonitoringRange(int range) {
        if (monitorBlockEntity != null) {
            // Send packet to server to update monitoring range
            // Implementation depends on your networking system
        }
    }

    public boolean isAutoShutdownEnabled() {
        return monitorBlockEntity != null && monitorBlockEntity.isAutoShutdownEnabled();
    }

    public void setAutoShutdownEnabled(boolean enabled) {
        if (monitorBlockEntity != null) {
            // Send packet to server to toggle auto shutdown
            // Implementation depends on your networking system
        }
    }

    // Real-time monitoring status
    public boolean isMonitoring() {
        return monitorBlockEntity != null && monitorBlockEntity.isMonitoring();
    }

    public long getLastUpdateTime() {
        return monitorBlockEntity != null ? monitorBlockEntity.getLastUpdateTime() : 0L;
    }

    public int getUpdateFrequency() {
        return monitorBlockEntity != null ? monitorBlockEntity.getUpdateFrequency() : 20; // Default 1 second
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Any cleanup needed when monitor menu is closed
    }
}