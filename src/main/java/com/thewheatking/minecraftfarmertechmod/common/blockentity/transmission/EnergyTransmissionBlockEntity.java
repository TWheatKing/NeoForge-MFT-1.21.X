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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.*;

/**
 * CORRECTED: Base energy transmission block entity for cable networks
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
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
    protected static final int NETWORK_UPDATE_INTERVAL = 20;

    // Visual state
    protected boolean isTransmitting = false;
    protected int transmissionAnimation = 0;

    // Explosion/overload system - ALL CONSTANTS DEFINED HERE
    protected int overloadTicks = 0;
    protected static final int OVERLOAD_WARNING_TICKS = 60; // 3 seconds warning
    protected static final int OVERLOAD_EXPLOSION_TICKS = 100; // 5 seconds to explosion
    protected static final double ELECTRICAL_DAMAGE_RANGE = 3.0; // 3 block radius
    protected boolean hasWarned = false;

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
        return new HybridEnergyStorage(transferTier);
    }

    @Override
    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(0); // Cables don't have inventory
    }

    @Override
    protected boolean canOperate() {
        return energyStorage.getEnergyStored() > 0 || hasConnectedDevices();
    }

    @Override
    protected void performOperation() {
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

        updateConnections();
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

        if (isTransmitting) {
            transmissionAnimation = (transmissionAnimation + 1) % 40;
            spawnTransmissionParticles();
        } else {
            transmissionAnimation = 0;
        }
    }

    protected void updateNetwork() {
        if (level != null) {
            connectedNetwork = EnhancedMftEnergyNetwork.getOrCreateNetwork(level, worldPosition);
            networkId = connectedNetwork.getNetworkId();
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
                if (CableUtils.isCable(level, adjacentPos)) {
                    isConnected = true;
                    connectedDevices.remove(direction);
                } else {
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

    /**
     * EXPLOSION SYSTEM: Enhanced energy transfer with overload protection
     */
    @Override
    protected void handleEnergyDistribution() {
        if (!canOperate()) return;

        int energyToTransfer = energyStorage.getEnergyStored();
        if (energyToTransfer <= 0) {
            if (overloadTicks > 0) {
                overloadTicks = 0;
                hasWarned = false;
            }
            return;
        }

        // Check for cable overload BEFORE attempting transfer
        if (!isEnergyLevelSafe(energyToTransfer)) {
            handleOverload(energyToTransfer);
            return; // Don't transfer energy if overloaded
        } else {
            if (overloadTicks > 0) {
                overloadTicks = 0;
                hasWarned = false;
            }
        }

        // Continue with normal energy distribution
        distributeEnergyToAllSides();
    }

    private void distributeEnergyToAllSides() {
        int totalEnergyToDistribute = energyStorage.getEnergyStored();
        if (totalEnergyToDistribute <= 0) return;

        var receivers = new ArrayList<IEnergyStorage>();

        for (Direction direction : Direction.values()) {
            if (connectedDevices.containsKey(direction)) {
                IEnergyStorage device = connectedDevices.get(direction);
                if (device.canReceive()) {
                    receivers.add(device);
                }
            }
        }

        if (receivers.isEmpty()) return;

        int energyPerReceiver = Math.min(totalEnergyToDistribute / receivers.size(), transferRate);

        for (IEnergyStorage receiver : receivers) {
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
        double lossMultiplier = isInsulated ? energyLossPerBlock * 0.5 : energyLossPerBlock;
        int actualAmount = (int) (amount * (1.0 - lossMultiplier));
        int received = target.receiveEnergy(actualAmount, false);
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
    }

    protected boolean hasConnectedDevices() {
        return !connectedDevices.isEmpty();
    }

    @Override
    protected boolean canOutputEnergy(Direction direction) {
        return connections.get(direction);
    }

    @Override
    protected boolean canInputEnergy(Direction direction) {
        return connections.get(direction);
    }

    // ========== EXPLOSION SYSTEM METHODS ==========

    protected boolean isEnergyLevelSafe(int energyAmount) {
        return !transferTier.isOverloaded(energyAmount);
    }

    protected void handleOverload(int energyAmount) {
        overloadTicks++;

        if (overloadTicks >= OVERLOAD_WARNING_TICKS && !hasWarned) {
            sendOverloadWarning();
            hasWarned = true;
        }

        if (overloadTicks % 10 == 0) {
            spawnOverloadParticles();
            playOverloadSounds();
        }

        if (overloadTicks >= OVERLOAD_EXPLOSION_TICKS) {
            explodeCable();
        }
    }

    protected void sendOverloadWarning() {
        if (level == null || level.isClientSide()) return;

        AABB searchArea = new AABB(worldPosition).inflate(10.0);
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, searchArea);

        for (Player player : nearbyPlayers) {
            player.displayClientMessage(
                    Component.literal("⚠ WARNING: Cable overload! Energy: " +
                            energyStorage.getEnergyStored() + " FE/t exceeds limit: " +
                            transferTier.getExplosionThreshold() + " FE/t"),
                    true
            );
        }
    }

    protected void spawnOverloadParticles() {
        if (level == null || !level.isClientSide()) return;

        for (int i = 0; i < 5; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetY = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;

            level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    worldPosition.getX() + 0.5 + offsetX,
                    worldPosition.getY() + 0.5 + offsetY,
                    worldPosition.getZ() + 0.5 + offsetZ,
                    0, 0.1, 0);
        }
    }

    protected void playOverloadSounds() {
        if (level == null) return;
        level.playSound(null, worldPosition, SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.BLOCKS, 0.3f, 2.0f);
    }

    protected void explodeCable() {
        if (level == null || level.isClientSide()) return;

        AABB damageArea = new AABB(worldPosition).inflate(ELECTRICAL_DAMAGE_RANGE);
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, damageArea);

        for (LivingEntity entity : nearbyEntities) {
            double distance = entity.distanceToSqr(worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);

            float damage = (float) (12.0 - (distance * 2.0));
            damage = Math.max(damage, 4.0f);

            entity.hurt(level.damageSources().lightningBolt(), damage);

            if (entity instanceof Player player) {
                player.displayClientMessage(
                        Component.literal("⚡ You were electrocuted by an overloaded cable!"),
                        false
                );
            }
        }

        spawnExplosionParticles();
        level.playSound(null, worldPosition, SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.BLOCKS, 1.0f, 1.0f);
        level.destroyBlock(worldPosition, false);
    }

    protected void spawnExplosionParticles() {
        if (level == null) return;

        for (int i = 0; i < 20; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 4.0;
            double offsetY = (level.random.nextDouble() - 0.5) * 4.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 4.0;

            level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    worldPosition.getX() + 0.5 + offsetX,
                    worldPosition.getY() + 0.5 + offsetY,
                    worldPosition.getZ() + 0.5 + offsetZ,
                    offsetX * 0.1, offsetY * 0.1, offsetZ * 0.1);
        }

        for (int i = 0; i < 10; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetY = level.random.nextDouble();
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;

            level.addParticle(ParticleTypes.LARGE_SMOKE,
                    worldPosition.getX() + 0.5 + offsetX,
                    worldPosition.getY() + 0.5 + offsetY,
                    worldPosition.getZ() + 0.5 + offsetZ,
                    0, 0.1, 0);
        }
    }

    // ========== GETTERS AND UTILITY ==========

    public int getTransferRate() { return transferRate; }
    public double getEnergyLossPerBlock() { return energyLossPerBlock; }
    public boolean isInsulated() { return isInsulated; }
    public boolean isConnected(Direction direction) { return connections.get(direction); }
    public Map<Direction, Boolean> getConnections() { return new HashMap<>(connections); }
    public boolean isTransmitting() { return isTransmitting; }
    public double getCurrentLoad() { return currentLoad; }
    public int getEnergyTransferred() { return energyTransferred; }
    public String getNetworkId() { return networkId; }
    public EnhancedMftEnergyNetwork getConnectedNetwork() { return connectedNetwork; }
    public int getTransmissionAnimation() { return transmissionAnimation; }
    public HybridEnergyStorage.TransferTier getTransferTier() { return transferTier; }

    public void onNetworkChanged() { networkDirty = true; }

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

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        pTag.putString("TransferTier", transferTier.name());
        pTag.putInt("TransferRate", transferRate);
        pTag.putDouble("EnergyLossPerBlock", energyLossPerBlock);
        pTag.putBoolean("IsInsulated", isInsulated);
        pTag.putBoolean("IsTransmitting", isTransmitting);
        pTag.putDouble("CurrentLoad", currentLoad);
        pTag.putInt("EnergyTransferred", energyTransferred);
        pTag.putInt("overloadTicks", overloadTicks);
        pTag.putBoolean("hasWarned", hasWarned);

        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.from3DDataValue(i);
            pTag.putBoolean("Connection_" + direction.name(), connections.get(direction));
        }

        if (networkId != null) {
            pTag.putString("NetworkId", networkId);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        isTransmitting = pTag.getBoolean("IsTransmitting");
        currentLoad = pTag.getDouble("CurrentLoad");
        energyTransferred = pTag.getInt("EnergyTransferred");
        overloadTicks = pTag.getInt("overloadTicks");
        hasWarned = pTag.getBoolean("hasWarned");

        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.from3DDataValue(i);
            if (pTag.contains("Connection_" + direction.name())) {
                connections.put(direction, pTag.getBoolean("Connection_" + direction.name()));
            }
        }

        if (pTag.contains("NetworkId")) {
            networkId = pTag.getString("NetworkId");
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        disconnectFromNetwork();
        CableUtils.clearCache();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null; // Cables don't have GUIs
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
}