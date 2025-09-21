package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * Network Bridge Block Entity - Tablet Linking Station & Multiblock Component
 *
 * WHAT IT DOES:
 * • Passive multiblock component for NetworkDashboard (enables remote control tab)
 * • Tablet linking station for NetworkTabletItem wireless network access
 * • High-capacity energy storage and transfer (100k FE, 20k FE/tick)
 * • Right-click with NetworkTabletItem to link tablet for remote access
 * • Confirmation GUI (Yes/No) for tablet linking process
 * • Future-expandable for additional networking features
 *
 * Based on TWheatKing's original MFT framework, enhanced by Claude
 * Minecraft 1.21 + NeoForge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/NetworkBridgeBlockEntity.java
 */
public class NetworkBridgeBlockEntity extends BaseMachineBlockEntity {

    // Network Bridge Specifications (High-tier)
    private static final int ENERGY_CAPACITY = 100000;       // 100,000 FE capacity
    private static final int ENERGY_TRANSFER_RATE = 20000;   // 20,000 FE/tick transfer
    private static final int INVENTORY_SIZE = 27;            // Large inventory for future features

    // Multiblock State
    private BlockPos connectedDashboardPos = null;
    private boolean isDashboardConnected = false;
    private boolean isMultiblockFormed = false;

    // Tablet Linking System
    private final Set<UUID> linkedTablets = new HashSet<>();
    private final Map<UUID, String> tabletOwners = new HashMap<>();
    private UUID pendingTabletLink = null;     // Tablet waiting for confirmation
    private Player pendingPlayer = null;        // Player who initiated linking
    private long linkingTimeout = 0L;          // When pending link expires
    private static final long LINKING_TIMEOUT_TICKS = 200L; // 10 seconds

    // Network Bridge Status
    private boolean isOperational = true;
    private boolean remoteAccessEnabled = false;
    private int maxLinkedTablets = 10;         // Maximum tablets that can be linked

    // Statistics
    private long totalRemoteAccesses = 0L;
    private long lastRemoteAccessTime = 0L;
    private int activeRemoteConnections = 0;

    // Future Expansion Placeholder
    private final Map<String, Object> futureFeatures = new HashMap<>();

    public NetworkBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.NETWORK_BRIDGE.get(), pos, state,
                ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, INVENTORY_SIZE);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        // Check for NetworkDashboard multiblock connection
        checkDashboardConnection();

        // Handle tablet linking timeout
        if (pendingTabletLink != null && level.getGameTime() > linkingTimeout) {
            cancelTabletLinking();
        }

        // Update operational state
        boolean wasOperational = isOperational;
        isOperational = energyStorage.getEnergyStored() >= 100; // Needs minimal energy to operate

        // Update working state
        boolean wasWorking = isWorking;
        isWorking = isOperational && (isDashboardConnected || !linkedTablets.isEmpty());

        // Sync to client if state changed
        if ((wasWorking != isWorking || wasOperational != isOperational) &&
                tickCounter % SYNC_INTERVAL == 0) {
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
        remoteAccessEnabled = foundDashboard && isOperational;

        // Notify if connection state changed
        if (wasConnected != isDashboardConnected) {
            if (isDashboardConnected) {
                addDebugMessage("NetworkBridge connected to Dashboard at " + dashboardPos);
            } else {
                addDebugMessage("NetworkBridge disconnected from Dashboard");
            }
            setChanged();
        }
    }

    /**
     * Handle player interaction for tablet linking
     */
    public InteractionResult handlePlayerInteraction(Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack heldItem = player.getItemInHand(hand);

        // Check if player is holding a NetworkTabletItem
        if (isNetworkTabletItem(heldItem)) {
            return handleTabletLinking(player, heldItem);
        }

        // Default interaction (open normal GUI)
        return InteractionResult.PASS;
    }

    /**
     * Handle tablet linking process
     */
    private InteractionResult handleTabletLinking(Player player, ItemStack tabletItem) {
        if (!isOperational) {
            player.sendSystemMessage(Component.literal("Network Bridge is not operational - insufficient energy"));
            return InteractionResult.FAIL;
        }

        if (!isDashboardConnected) {
            player.sendSystemMessage(Component.literal("Network Bridge must be connected to a Network Dashboard"));
            return InteractionResult.FAIL;
        }

        UUID tabletId = getTabletId(tabletItem);
        if (tabletId == null) {
            // Create new tablet ID if not present
            tabletId = UUID.randomUUID();
            setTabletId(tabletItem, tabletId);
        }

        // Check if tablet is already linked
        if (linkedTablets.contains(tabletId)) {
            player.sendSystemMessage(Component.literal("This tablet is already linked to this Network Bridge"));
            return InteractionResult.SUCCESS;
        }

        // Check if we can link more tablets
        if (linkedTablets.size() >= maxLinkedTablets) {
            player.sendSystemMessage(Component.literal("Maximum number of tablets already linked (" + maxLinkedTablets + ")"));
            return InteractionResult.FAIL;
        }

        // Start linking process with confirmation
        startTabletLinking(player, tabletId);
        return InteractionResult.SUCCESS;
    }

    /**
     * Start tablet linking process with confirmation GUI
     */
    private void startTabletLinking(Player player, UUID tabletId) {
        pendingTabletLink = tabletId;
        pendingPlayer = player;
        linkingTimeout = level.getGameTime() + LINKING_TIMEOUT_TICKS;

        // Send confirmation message
        Component confirmMessage = Component.literal("Link tablet to Network Bridge? This will allow remote network access.")
                .append(Component.literal("\n[Yes] - Confirm linking"))
                .append(Component.literal("\n[No] - Cancel linking"));

        player.sendSystemMessage(confirmMessage);

        // In a real implementation, this would open a proper GUI with Yes/No buttons
        // For now, we'll auto-confirm after a short delay
        // TODO: Implement proper confirmation GUI
    }

    /**
     * Confirm tablet linking
     */
    public void confirmTabletLinking() {
        if (pendingTabletLink != null && pendingPlayer != null) {
            // Link the tablet
            linkedTablets.add(pendingTabletLink);
            tabletOwners.put(pendingTabletLink, pendingPlayer.getName().getString());

            // Update tablet item with bridge location
            ItemStack tabletItem = findTabletInPlayerInventory(pendingPlayer, pendingTabletLink);
            if (tabletItem != null) {
                setBridgeLocation(tabletItem, worldPosition);
            }

            // Notify player
            pendingPlayer.sendSystemMessage(Component.literal("Tablet successfully linked to Network Bridge"));
            addDebugMessage("Tablet linked for player: " + pendingPlayer.getName().getString());

            // Clear pending state
            pendingTabletLink = null;
            pendingPlayer = null;
            linkingTimeout = 0L;

            setChanged();
        }
    }

    /**
     * Cancel tablet linking
     */
    public void cancelTabletLinking() {
        if (pendingPlayer != null) {
            pendingPlayer.sendSystemMessage(Component.literal("Tablet linking cancelled"));
        }

        pendingTabletLink = null;
        pendingPlayer = null;
        linkingTimeout = 0L;
    }

    /**
     * Remove tablet link
     */
    public boolean unlinkTablet(UUID tabletId) {
        if (linkedTablets.remove(tabletId)) {
            tabletOwners.remove(tabletId);
            addDebugMessage("Tablet unlinked: " + tabletId);
            setChanged();
            return true;
        }
        return false;
    }

    /**
     * Handle remote access from tablet
     */
    public void handleRemoteAccess(UUID tabletId, Player player) {
        if (!linkedTablets.contains(tabletId)) {
            player.sendSystemMessage(Component.literal("Tablet not linked to this Network Bridge"));
            return;
        }

        if (!remoteAccessEnabled) {
            player.sendSystemMessage(Component.literal("Remote access unavailable - check Network Bridge connection"));
            return;
        }

        // Track remote access
        totalRemoteAccesses++;
        lastRemoteAccessTime = level.getGameTime();

        // Open network dashboard remotely
        // TODO: Implement remote dashboard GUI
        player.sendSystemMessage(Component.literal("Opening remote network access..."));
    }

    @Override
    protected boolean canOperate() {
        return energyStorage.getEnergyStored() >= 50;
    }

    @Override
    protected void performOperation() {
        // NetworkBridge is mostly passive - main operation is multiblock detection
        // Energy is consumed for maintaining tablet connections
        if (!linkedTablets.isEmpty() && remoteAccessEnabled) {
            // Small energy cost for maintaining tablet connections
            energyStorage.extractEnergy(linkedTablets.size(), false);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.network_bridge");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HybridMenuTypes.NetworkBridgeMenu(containerId, playerInventory, this);
    }

    // ========== TABLET UTILITY METHODS ==========

    /**
     * Check if item is a NetworkTabletItem
     */
    private boolean isNetworkTabletItem(ItemStack item) {
        // TODO: Replace with actual NetworkTabletItem check
        // For now, assume any item with "tablet" in the name
        return item.getDisplayName().getString().toLowerCase().contains("tablet");
    }

    /**
     * Get tablet ID from item NBT
     */
    private UUID getTabletId(ItemStack tabletItem) {
        CompoundTag tag = tabletItem.getTag();
        if (tag != null && tag.hasUUID("TabletId")) {
            return tag.getUUID("TabletId");
        }
        return null;
    }

    /**
     * Set tablet ID in item NBT
     */
    private void setTabletId(ItemStack tabletItem, UUID tabletId) {
        tabletItem.getOrCreateTag().putUUID("TabletId", tabletId);
    }

    /**
     * Set bridge location in tablet item NBT
     */
    private void setBridgeLocation(ItemStack tabletItem, BlockPos bridgePos) {
        CompoundTag tag = tabletItem.getOrCreateTag();
        tag.putLong("LinkedBridgePos", bridgePos.asLong());
        tag.putString("LinkedBridgeDim", level.dimension().location().toString());
    }

    /**
     * Find tablet item in player inventory
     */
    private ItemStack findTabletInPlayerInventory(Player player, UUID tabletId) {
        for (ItemStack item : player.getInventory().items) {
            if (isNetworkTabletItem(item) && tabletId.equals(getTabletId(item))) {
                return item;
            }
        }
        return null;
    }

    // ========== GUI DATA METHODS ==========

    public boolean isDashboardConnected() { return isDashboardConnected; }
    public boolean isMultiblockFormed() { return isMultiblockFormed; }
    public boolean isOperational() { return isOperational; }
    public boolean isRemoteAccessEnabled() { return remoteAccessEnabled; }

    public BlockPos getConnectedDashboardPos() { return connectedDashboardPos; }
    public int getLinkedTabletsCount() { return linkedTablets.size(); }
    public int getMaxLinkedTablets() { return maxLinkedTablets; }

    public Set<UUID> getLinkedTablets() { return new HashSet<>(linkedTablets); }
    public Map<UUID, String> getTabletOwners() { return new HashMap<>(tabletOwners); }

    public boolean hasPendingTabletLink() { return pendingTabletLink != null; }
    public String getPendingPlayerName() {
        return pendingPlayer != null ? pendingPlayer.getName().getString() : "Unknown";
    }
    public long getLinkingTimeoutRemaining() {
        return Math.max(0, linkingTimeout - (level != null ? level.getGameTime() : 0));
    }

    public long getTotalRemoteAccesses() { return totalRemoteAccesses; }
    public long getLastRemoteAccessTime() { return lastRemoteAccessTime; }
    public int getActiveRemoteConnections() { return activeRemoteConnections; }

    // ========== CONTROL METHODS ==========

    public void setMaxLinkedTablets(int max) {
        this.maxLinkedTablets = Math.max(1, Math.min(50, max));
        setChanged();
    }

    public void unlinkAllTablets() {
        linkedTablets.clear();
        tabletOwners.clear();
        addDebugMessage("All tablets unlinked");
        setChanged();
    }

    public void resetStatistics() {
        totalRemoteAccesses = 0L;
        lastRemoteAccessTime = 0L;
        activeRemoteConnections = 0;
        setChanged();
    }

    // ========== DEBUG METHODS ==========

    private void addDebugMessage(String message) {
        if (level != null && !level.isClientSide()) {
            // TODO: Add to debug log or send to nearby players
            System.out.println("[NetworkBridge] " + message);
        }
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Save multiblock state
        tag.putBoolean("IsDashboardConnected", isDashboardConnected);
        tag.putBoolean("IsMultiblockFormed", isMultiblockFormed);
        tag.putBoolean("IsOperational", isOperational);
        tag.putBoolean("RemoteAccessEnabled", remoteAccessEnabled);

        if (connectedDashboardPos != null) {
            tag.putLong("ConnectedDashboardPos", connectedDashboardPos.asLong());
        }

        // Save tablet linking data
        CompoundTag tabletsTag = new CompoundTag();
        int index = 0;
        for (UUID tabletId : linkedTablets) {
            tabletsTag.putUUID("Tablet_" + index, tabletId);
            String owner = tabletOwners.get(tabletId);
            if (owner != null) {
                tabletsTag.putString("Owner_" + index, owner);
            }
            index++;
        }
        tag.put("LinkedTablets", tabletsTag);
        tag.putInt("LinkedTabletsCount", linkedTablets.size());
        tag.putInt("MaxLinkedTablets", maxLinkedTablets);

        // Save pending linking state
        if (pendingTabletLink != null) {
            tag.putUUID("PendingTabletLink", pendingTabletLink);
            tag.putLong("LinkingTimeout", linkingTimeout);
        }

        // Save statistics
        tag.putLong("TotalRemoteAccesses", totalRemoteAccesses);
        tag.putLong("LastRemoteAccessTime", lastRemoteAccessTime);
        tag.putInt("ActiveRemoteConnections", activeRemoteConnections);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        // Load multiblock state
        isDashboardConnected = tag.getBoolean("IsDashboardConnected");
        isMultiblockFormed = tag.getBoolean("IsMultiblockFormed");
        isOperational = tag.getBoolean("IsOperational");
        remoteAccessEnabled = tag.getBoolean("RemoteAccessEnabled");

        if (tag.contains("ConnectedDashboardPos")) {
            connectedDashboardPos = BlockPos.of(tag.getLong("ConnectedDashboardPos"));
        }

        // Load tablet linking data
        linkedTablets.clear();
        tabletOwners.clear();

        if (tag.contains("LinkedTablets")) {
            CompoundTag tabletsTag = tag.getCompound("LinkedTablets");
            int count = tag.getInt("LinkedTabletsCount");

            for (int i = 0; i < count; i++) {
                if (tabletsTag.hasUUID("Tablet_" + i)) {
                    UUID tabletId = tabletsTag.getUUID("Tablet_" + i);
                    linkedTablets.add(tabletId);

                    if (tabletsTag.contains("Owner_" + i)) {
                        String owner = tabletsTag.getString("Owner_" + i);
                        tabletOwners.put(tabletId, owner);
                    }
                }
            }
        }

        maxLinkedTablets = tag.getInt("MaxLinkedTablets");
        if (maxLinkedTablets == 0) maxLinkedTablets = 10; // Default value

        // Load pending linking state
        if (tag.hasUUID("PendingTabletLink")) {
            pendingTabletLink = tag.getUUID("PendingTabletLink");
            linkingTimeout = tag.getLong("LinkingTimeout");
        }

        // Load statistics
        totalRemoteAccesses = tag.getLong("TotalRemoteAccesses");
        lastRemoteAccessTime = tag.getLong("LastRemoteAccessTime");
        activeRemoteConnections = tag.getInt("ActiveRemoteConnections");
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        // Send bridge data to client for GUI updates
        tag.putBoolean("DashboardConnected", isDashboardConnected);
        tag.putBoolean("MultiblockFormed", isMultiblockFormed);
        tag.putBoolean("Operational", isOperational);
        tag.putBoolean("RemoteEnabled", remoteAccessEnabled);
        tag.putInt("LinkedTablets", linkedTablets.size());
        tag.putInt("MaxTablets", maxLinkedTablets);
        tag.putBoolean("HasPending", pendingTabletLink != null);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        // Receive bridge data from server
        isDashboardConnected = tag.getBoolean("DashboardConnected");
        isMultiblockFormed = tag.getBoolean("MultiblockFormed");
        isOperational = tag.getBoolean("Operational");
        remoteAccessEnabled = tag.getBoolean("RemoteEnabled");
        // Note: Don't sync full tablet lists to client for security
        maxLinkedTablets = tag.getInt("MaxTablets");
    }
}