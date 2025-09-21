package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.menu.EnergyControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

import static com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities.ENERGY_CONTROLLER;

public class EnergyControllerBlockEntity extends BaseMachineBlockEntity {

    // Power control settings
    private boolean powerEnabled = true;
    private int energyFlowLimit = 1000; // FE/t
    private int maxEnergyFlowLimit = 10000; // Maximum configurable limit

    // Network management
    private final Set<BlockPos> connectedDevices = new HashSet<>();
    private int networkEnergyUsage = 0;
    private int controlRange = 16; // Blocks

    // Emergency and safety
    private boolean emergencyShutdown = false;
    private long lastScanTime = 0;
    private static final long SCAN_INTERVAL = 20; // Ticks between network scans

    // Network statistics
    private int totalEnergyControlled = 0;
    private int activeConnections = 0;
    private boolean networkStable = true;

    public EnergyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ENERGY_CONTROLLER.get(), pos, state, 100000, 2000, 2000, 0); // High capacity, no inventory
    }

    @Override
    protected boolean canOperate() {
        return powerEnabled && !emergencyShutdown && energyStorage.getEnergyStored() > 50;
    }

    @Override
    protected void performOperation() {
        if (level.getGameTime() - lastScanTime >= SCAN_INTERVAL) {
            scanAndManageNetwork();
            lastScanTime = level.getGameTime();
        }

        if (powerEnabled && !emergencyShutdown) {
            controlEnergyFlow();
        }
    }

    /**
     * Scans the network for connected energy devices
     */
    private void scanAndManageNetwork() {
        Set<BlockPos> newConnectedDevices = new HashSet<>();
        int totalUsage = 0;
        int activeCount = 0;

        // Scan in a cube around the controller
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -controlRange; x <= controlRange; x++) {
            for (int y = -controlRange; y <= controlRange; y++) {
                for (int z = -controlRange; z <= controlRange; z++) {
                    mutablePos.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);

                    BlockEntity blockEntity = level.getBlockEntity(mutablePos);
                    if (blockEntity != null) {
                        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, mutablePos, null);
                        if (energyStorage != null) {
                            BlockPos immutablePos = mutablePos.immutable();
                            newConnectedDevices.add(immutablePos);

                            // Estimate energy usage
                            if (energyStorage.canReceive() || energyStorage.canExtract()) {
                                activeCount++;
                                totalUsage += Math.min(energyStorage.getEnergyStored(), 100); // Rough estimate
                            }
                        }
                    }
                }
            }
        }

        connectedDevices.clear();
        connectedDevices.addAll(newConnectedDevices);
        networkEnergyUsage = totalUsage;
        activeConnections = activeCount;

        // Check network stability
        networkStable = networkEnergyUsage < energyFlowLimit * 0.9; // 90% threshold

        setChanged();
    }

    /**
     * Controls energy flow through the network
     */
    private void controlEnergyFlow() {
        if (connectedDevices.isEmpty()) return;

        int availableEnergy = Math.min(energyStorage.getEnergyStored(), energyFlowLimit);
        int energyPerDevice = connectedDevices.size() > 0 ? availableEnergy / connectedDevices.size() : 0;

        for (BlockPos devicePos : connectedDevices) {
            BlockEntity device = level.getBlockEntity(devicePos);
            if (device != null) {
                IEnergyStorage deviceStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, devicePos, null);
                if (deviceStorage != null && deviceStorage.canReceive()) {
                    // Transfer limited energy to prevent overload
                    int transferred = deviceStorage.receiveEnergy(energyPerDevice, false);
                    energyStorage.extractEnergy(transferred, false);
                    totalEnergyControlled += transferred;
                }
            }
        }
    }

    // Public getter methods for the menu
    public boolean isPowerEnabled() {
        return powerEnabled && !emergencyShutdown;
    }

    public int getEnergyFlowLimit() {
        return energyFlowLimit;
    }

    public int getMaxEnergyFlowLimit() {
        return maxEnergyFlowLimit;
    }

    public int getConnectedDevicesCount() {
        return connectedDevices.size();
    }

    public int getNetworkEnergyUsage() {
        return networkEnergyUsage;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public boolean isNetworkStable() {
        return networkStable;
    }

    public boolean isEmergencyShutdown() {
        return emergencyShutdown;
    }

    public int getControlRange() {
        return controlRange;
    }

    public int getTotalEnergyControlled() {
        return totalEnergyControlled;
    }

    // Control methods
    public void setPowerEnabled(boolean enabled) {
        this.powerEnabled = enabled;
        if (!enabled) {
            // Disconnect all devices when power is disabled
            disconnectAllDevices();
        }
        setChanged();
    }

    public void setEnergyFlowLimit(int limit) {
        this.energyFlowLimit = Math.max(0, Math.min(limit, maxEnergyFlowLimit));
        setChanged();
    }

    public void setControlRange(int range) {
        this.controlRange = Math.max(1, Math.min(range, 32)); // Max 32 block range
        setChanged();
    }

    public void triggerEmergencyShutdown() {
        this.emergencyShutdown = true;
        this.powerEnabled = false;
        disconnectAllDevices();
        setChanged();
    }

    public void resetEmergencyShutdown() {
        this.emergencyShutdown = false;
        this.powerEnabled = true;
        setChanged();
    }

    public void resetStatistics() {
        this.totalEnergyControlled = 0;
        setChanged();
    }

    /**
     * Safely disconnects all devices by stopping energy transfer
     */
    private void disconnectAllDevices() {
        // In a real implementation, you might want to send shutdown signals
        // or gradually reduce power to prevent damage to connected devices
        networkEnergyUsage = 0;
        activeConnections = 0;
    }

    /**
     * Gets detailed network information
     */
    public String getNetworkStatus() {
        if (emergencyShutdown) {
            return "EMERGENCY SHUTDOWN ACTIVE";
        } else if (!powerEnabled) {
            return "POWER DISABLED";
        } else if (connectedDevices.isEmpty()) {
            return "NO DEVICES CONNECTED";
        } else if (!networkStable) {
            return "NETWORK OVERLOADED";
        } else {
            return "NETWORK OPERATIONAL";
        }
    }

    /**
     * Gets current load percentage
     */
    public float getLoadPercentage() {
        if (energyFlowLimit <= 0) return 0.0f;
        return Math.min(100.0f, (float) networkEnergyUsage / energyFlowLimit * 100.0f);
    }

    /**
     * Checks if the controller is overloaded
     */
    public boolean isOverloaded() {
        return networkEnergyUsage > energyFlowLimit * 0.95; // 95% threshold
    }

    /**
     * Gets connected device positions (for diagnostic purposes)
     */
    public Set<BlockPos> getConnectedDevicePositions() {
        return new HashSet<>(connectedDevices);
    }

    /**
     * Forces a network rescan
     */
    public void forceNetworkScan() {
        scanAndManageNetwork();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.energy_controller");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EnergyControllerMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("powerEnabled", powerEnabled);
        tag.putInt("energyFlowLimit", energyFlowLimit);
        tag.putInt("maxEnergyFlowLimit", maxEnergyFlowLimit);
        tag.putInt("networkEnergyUsage", networkEnergyUsage);
        tag.putInt("controlRange", controlRange);
        tag.putBoolean("emergencyShutdown", emergencyShutdown);
        tag.putLong("lastScanTime", lastScanTime);
        tag.putInt("totalEnergyControlled", totalEnergyControlled);
        tag.putInt("activeConnections", activeConnections);
        tag.putBoolean("networkStable", networkStable);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        powerEnabled = tag.getBoolean("powerEnabled");
        energyFlowLimit = tag.getInt("energyFlowLimit");
        maxEnergyFlowLimit = tag.getInt("maxEnergyFlowLimit");
        networkEnergyUsage = tag.getInt("networkEnergyUsage");
        controlRange = tag.getInt("controlRange");
        emergencyShutdown = tag.getBoolean("emergencyShutdown");
        lastScanTime = tag.getLong("lastScanTime");
        totalEnergyControlled = tag.getInt("totalEnergyControlled");
        activeConnections = tag.getInt("activeConnections");
        networkStable = tag.getBoolean("networkStable");
    }
}