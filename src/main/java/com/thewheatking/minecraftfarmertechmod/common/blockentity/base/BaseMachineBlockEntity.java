package com.thewheatking.minecraftfarmertechmod.common.blockentity.base;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyCapabilityProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * FINAL FIXED: Base machine block entity providing core functionality for all hybrid energy machines
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/base/BaseMachineBlockEntity.java
 * Purpose: Provides foundational machine functionality with hybrid energy storage, inventory management, and networking
 */
public abstract class BaseMachineBlockEntity extends BlockEntity
        implements HybridEnergyCapabilityProviders.IHybridEnergyBlockEntity, MenuProvider {

    // Energy storage system
    protected HybridEnergyStorage energyStorage;
    protected final int energyCapacity;
    protected final int energyMaxReceive;
    protected final int energyMaxExtract;

    // Inventory system
    protected ItemStackHandler inventory;
    protected final int inventorySize;

    // Machine state
    protected boolean isActive = false;
    protected boolean isPowered = false;
    protected boolean isWorking = false;
    protected int workProgress = 0;
    protected int maxWorkTime = 200; // Default 10 seconds at 20 TPS

    // Networking and synchronization
    protected boolean needsUpdate = false;
    protected int tickCounter = 0;
    protected static final int SYNC_INTERVAL = 20; // Sync every second

    // Upgrade system
    protected boolean hasEfficiencyUpgrade = false;
    protected boolean hasSpeedUpgrade = false;
    protected boolean hasCapacityUpgrade = false;
    protected double efficiencyMultiplier = 1.0;
    protected double speedMultiplier = 1.0;
    protected double capacityMultiplier = 1.0;

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        // Default values - subclasses can override these
        this.energyCapacity = 50000;
        this.energyMaxReceive = 1000;
        this.energyMaxExtract = 1000;
        this.inventorySize = 0;

        // Initialize energy storage and inventory
        initializeEnergyStorage();
        initializeInventory();
    }

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                  int energyCapacity, int energyMaxReceive, int energyMaxExtract) {
        this(type, pos, state, energyCapacity, energyMaxReceive, energyMaxExtract, 0);
    }

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                  int energyCapacity, int energyMaxReceive, int energyMaxExtract,
                                  int inventorySize) {
        super(type, pos, state);

        this.energyCapacity = energyCapacity;
        this.energyMaxReceive = energyMaxReceive;
        this.energyMaxExtract = energyMaxExtract;
        this.inventorySize = inventorySize;

        // Initialize energy storage and inventory
        initializeEnergyStorage();
        initializeInventory();
    }

    /**
     * Initialize the energy storage system
     * Subclasses can override createEnergyStorage() to customize
     */
    protected void initializeEnergyStorage() {
        this.energyStorage = createEnergyStorage();
    }

    /**
     * Initialize the inventory system
     * Subclasses can override createInventory() to customize
     */
    protected void initializeInventory() {
        this.inventory = createInventory();
    }

    /**
     * Create the energy storage for this machine
     * Override in subclasses for custom energy storage
     */
    protected HybridEnergyStorage createEnergyStorage() {
        return new HybridEnergyStorage(energyCapacity, energyMaxReceive, energyMaxExtract,
                energyCapacity * HybridEnergyStorage.getFeToMftRatio(),
                energyMaxReceive * HybridEnergyStorage.getFeToMftRatio(),
                energyMaxExtract * HybridEnergyStorage.getFeToMftRatio());
    }

    /**
     * Create the inventory for this machine
     * Override in subclasses for custom inventory
     */
    protected ItemStackHandler createInventory() {
        if (inventorySize > 0) {
            return new ItemStackHandler(inventorySize) {
                @Override
                protected void onContentsChanged(int slot) {
                    BaseMachineBlockEntity.this.setChanged();
                    BaseMachineBlockEntity.this.markUpdated();
                }
            };
        }
        return new ItemStackHandler(0);
    }

    // Core machine functionality

    public static void tick(Level level, BlockPos pos, BlockState state, BaseMachineBlockEntity blockEntity) {
        if (level.isClientSide()) {
            blockEntity.clientTick();
        } else {
            blockEntity.serverTick();
        }
    }

    protected void serverTick() {
        tickCounter++;

        // Update machine state
        updateMachineState();

        // Process work if active
        if (canOperate()) {
            performOperation();
        }

        // Handle energy distribution
        handleEnergyDistribution();

        // Apply upgrades
        applyUpgrades();

        // Sync with client periodically
        if (tickCounter % SYNC_INTERVAL == 0) {
            syncToClient();
        }

        // Mark for update if needed
        if (needsUpdate) {
            setChanged();
            markUpdated();
            needsUpdate = false;
        }
    }

    protected void clientTick() {
        // Client-side particle effects, sounds, etc.
        if (isActive) {
            spawnParticles();
            playWorkingSounds();
        }
    }

    protected void updateMachineState() {
        boolean wasActive = isActive;

        // Check if machine should be active
        isActive = canOperate() && hasEnoughEnergy();
        isPowered = energyStorage.getEnergyStored() > 0;

        if (wasActive != isActive) {
            needsUpdate = true;
            onActiveStateChanged(isActive);
        }
    }

    /**
     * Check if the machine can operate
     * Override in subclasses for specific operation conditions
     */
    protected abstract boolean canOperate();

    /**
     * Perform the machine's operation
     * Override in subclasses for specific operation logic
     */
    protected abstract void performOperation();

    protected boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= getEnergyPerTick();
    }

    protected void doWork() {
        int energyCost = getEnergyPerTick();

        if (energyStorage.extractEnergy(energyCost, false) >= energyCost) {
            workProgress++;

            if (workProgress >= getAdjustedWorkTime()) {
                completeWork();
                workProgress = 0;
            }

            needsUpdate = true;
        }
    }

    protected void completeWork() {
        // Override in subclasses for specific work completion logic
    }

    protected int getEnergyPerTick() {
        return (int) (20 / efficiencyMultiplier); // Base 20 FE/tick, reduced by efficiency
    }

    protected int getAdjustedWorkTime() {
        return (int) (maxWorkTime / speedMultiplier);
    }

    protected void handleEnergyDistribution() {
        distributeEnergy();
    }

    protected void distributeEnergy() {
        if (canOutputEnergy() && energyStorage.getEnergyStored() > 0) {
            // Distribute energy to adjacent machines
            for (Direction direction : Direction.values()) {
                if (canOutputEnergy(direction)) {
                    BlockPos adjacentPos = worldPosition.relative(direction);
                    if (level.isLoaded(adjacentPos)) {
                        IEnergyStorage adjacentStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                                adjacentPos, direction.getOpposite());

                        if (adjacentStorage != null && adjacentStorage.canReceive()) {
                            int maxTransfer = Math.min(energyMaxExtract, energyStorage.getEnergyStored());
                            int transferred = adjacentStorage.receiveEnergy(maxTransfer, true);

                            if (transferred > 0) {
                                int actualTransfer = energyStorage.extractEnergy(transferred, false);
                                adjacentStorage.receiveEnergy(actualTransfer, false);
                            }
                        }
                    }
                }
            }
        }
    }

    protected void applyUpgrades() {
        // Reset multipliers
        efficiencyMultiplier = 1.0;
        speedMultiplier = 1.0;
        capacityMultiplier = 1.0;

        // Apply upgrade bonuses
        if (hasEfficiencyUpgrade) {
            efficiencyMultiplier += 0.15; // 15% efficiency increase
        }
        if (hasSpeedUpgrade) {
            speedMultiplier += 0.25; // 25% speed increase
        }
        if (hasCapacityUpgrade) {
            capacityMultiplier += 0.50; // 50% capacity increase
        }
    }

    protected void onActiveStateChanged(boolean newActive) {
        // Override in subclasses for state change handling
    }

    protected void spawnParticles() {
        // Override in subclasses for particle effects
    }

    protected void playWorkingSounds() {
        // Override in subclasses for sound effects
    }

    // Energy system implementation

    @Override
    public HybridEnergyStorage getHybridEnergyStorage() {
        return energyStorage;
    }

    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    /**
     * Check if this machine can output energy
     */
    protected boolean canOutputEnergy() {
        return energyStorage.canExtract();
    }

    /**
     * Check if this machine can output energy on a specific side
     */
    protected boolean canOutputEnergy(Direction direction) {
        return canOutputEnergy();
    }

    /**
     * Check if this machine can input energy
     */
    protected boolean canInputEnergy() {
        return energyStorage.canReceive();
    }

    /**
     * Check if this machine can input energy on a specific side
     */
    protected boolean canInputEnergy(Direction direction) {
        return canInputEnergy();
    }

    // Inventory system

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return inventory;
    }

    public void dropContents() {
        if (inventory != null && level != null) {
            SimpleContainer container = new SimpleContainer(inventory.getSlots());
            for (int i = 0; i < inventory.getSlots(); i++) {
                container.setItem(i, inventory.getStackInSlot(i));
            }
            Containers.dropContents(level, worldPosition, container);
        }
    }

    // Getters and setters

    public boolean isActive() {
        return isActive;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public int getWorkProgress() {
        return workProgress;
    }

    public int getMaxWorkTime() {
        return getAdjustedWorkTime();
    }

    public double getWorkProgressPercent() {
        return getMaxWorkTime() > 0 ? (double) workProgress / getMaxWorkTime() : 0.0;
    }

    public void setMaxWorkTime(int maxWorkTime) {
        this.maxWorkTime = maxWorkTime;
    }

    // Upgrade system

    public void setEfficiencyUpgrade(boolean hasUpgrade) {
        this.hasEfficiencyUpgrade = hasUpgrade;
        needsUpdate = true;
    }

    public void setSpeedUpgrade(boolean hasUpgrade) {
        this.hasSpeedUpgrade = hasUpgrade;
        needsUpdate = true;
    }

    public void setCapacityUpgrade(boolean hasUpgrade) {
        this.hasCapacityUpgrade = hasUpgrade;
        needsUpdate = true;
    }

    public boolean hasEfficiencyUpgrade() {
        return hasEfficiencyUpgrade;
    }

    public boolean hasSpeedUpgrade() {
        return hasSpeedUpgrade;
    }

    public boolean hasCapacityUpgrade() {
        return hasCapacityUpgrade;
    }

    // Networking and synchronization

    public void markUpdated() {
        needsUpdate = true;
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    protected void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        handleUpdateTag(pkt.getTag(), lookupProvider);
    }

    // NBT serialization - FIXED for Minecraft 1.21

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        // Save energy storage
        tag.put("EnergyStorage", energyStorage.serializeNBT());

        // Save inventory
        if (inventory != null) {
            tag.put("Inventory", inventory.serializeNBT(registries));
        }

        // Save machine state
        tag.putBoolean("IsActive", isActive);
        tag.putBoolean("IsPowered", isPowered);
        tag.putBoolean("IsWorking", isWorking);
        tag.putInt("WorkProgress", workProgress);
        tag.putInt("MaxWorkTime", maxWorkTime);

        // Save upgrades
        tag.putBoolean("HasEfficiencyUpgrade", hasEfficiencyUpgrade);
        tag.putBoolean("HasSpeedUpgrade", hasSpeedUpgrade);
        tag.putBoolean("HasCapacityUpgrade", hasCapacityUpgrade);

        // Save multipliers
        tag.putDouble("EfficiencyMultiplier", efficiencyMultiplier);
        tag.putDouble("SpeedMultiplier", speedMultiplier);
        tag.putDouble("CapacityMultiplier", capacityMultiplier);

        // Save additional data for subclasses
        saveAdditionalData(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        // Load energy storage
        if (tag.contains("EnergyStorage")) {
            energyStorage.deserializeNBT(tag.getCompound("EnergyStorage"));
        }

        // Load inventory
        if (inventory != null && tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }

        // Load machine state
        isActive = tag.getBoolean("IsActive");
        isPowered = tag.getBoolean("IsPowered");
        isWorking = tag.getBoolean("IsWorking");
        workProgress = tag.getInt("WorkProgress");
        maxWorkTime = tag.getInt("MaxWorkTime");

        // Load upgrades
        hasEfficiencyUpgrade = tag.getBoolean("HasEfficiencyUpgrade");
        hasSpeedUpgrade = tag.getBoolean("HasSpeedUpgrade");
        hasCapacityUpgrade = tag.getBoolean("HasCapacityUpgrade");

        // Load multipliers
        efficiencyMultiplier = tag.getDouble("EfficiencyMultiplier");
        speedMultiplier = tag.getDouble("SpeedMultiplier");
        capacityMultiplier = tag.getDouble("CapacityMultiplier");

        // Load additional data for subclasses
        loadAdditionalData(tag, registries);
    }

    /**
     * Save additional data for subclasses
     * Override in subclasses to save custom data
     */
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Override in subclasses
    }

    /**
     * Load additional data for subclasses
     * Override in subclasses to load custom data
     */
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Override in subclasses
    }

    // Menu provider implementation

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod." + getClass().getSimpleName().toLowerCase());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Override in subclasses to provide custom menus
        return null;
    }

    // Utility methods

    /**
     * Gets formatted energy display string
     */
    public String getEnergyDisplayString() {
        return energyStorage.getEnergyDisplayString();
    }

    /**
     * Gets energy percentage (0.0 to 1.0)
     */
    public double getEnergyPercentage() {
        return energyStorage.getEnergyPercentage();
    }

    /**
     * Checks if machine is ready to work
     */
    public boolean isReadyToWork() {
        return isPowered && hasEnoughEnergy() && canOperate();
    }

    /**
     * Forces a work cycle completion (for testing/debugging)
     */
    public void forceCompleteWork() {
        if (canOperate()) {
            completeWork();
            workProgress = 0;
            needsUpdate = true;
        }
    }

    /**
     * Resets machine state
     */
    public void resetMachine() {
        isActive = false;
        isPowered = false;
        isWorking = false;
        workProgress = 0;
        needsUpdate = true;
    }

    /**
     * Gets debug information about this machine
     */
    public String getDebugInfo() {
        return String.format("Machine: %s, Active: %s, Powered: %s, Working: %s, Energy: %s",
                getClass().getSimpleName(),
                isActive ? "Yes" : "No",
                isPowered ? "Yes" : "No",
                isWorking ? "Yes" : "No",
                energyStorage.getEnergyDisplayString());
    }

    /**
     * Gets machine efficiency percentage
     */
    public double getEfficiencyPercentage() {
        return efficiencyMultiplier;
    }

    /**
     * Gets machine speed percentage
     */
    public double getSpeedPercentage() {
        return speedMultiplier;
    }

    /**
     * Gets machine capacity percentage
     */
    public double getCapacityPercentage() {
        return capacityMultiplier;
    }
}