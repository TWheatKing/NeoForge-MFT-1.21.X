package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Supplier;

/**
 * CORRECTED: Registration for hybrid energy system menu types
 * Based on TWheatKing's original MFT framework - keeping the centralized design pattern
 * All menu classes are inner classes to maintain the HybridMenuTypes.MenuClass reference pattern
 * Fixed by Claude for Minecraft 1.21 + Neoforge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridMenuTypes.java
 * Purpose: Centralized registration of all hybrid menu types with inner class implementations
 */
public class HybridMenuTypes {

    // Deferred register for menu types
    public static final DeferredRegister<MenuType<?>> HYBRID_MENU_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, MinecraftFarmerTechMod.MOD_ID);

    // Energy Storage Menu Types
    public static final Supplier<MenuType<BasicEnergyStorageMenu>> BASIC_ENERGY_STORAGE =
            HYBRID_MENU_TYPES.register("basic_energy_storage", () ->
                    IMenuTypeExtension.create(BasicEnergyStorageMenu::new));

    public static final Supplier<MenuType<EnhancedEnergyStorageMenu>> ENHANCED_ENERGY_STORAGE =
            HYBRID_MENU_TYPES.register("enhanced_energy_storage", () ->
                    IMenuTypeExtension.create(EnhancedEnergyStorageMenu::new));

    public static final Supplier<MenuType<AdvancedEnergyStorageMenu>> ADVANCED_ENERGY_STORAGE =
            HYBRID_MENU_TYPES.register("advanced_energy_storage", () ->
                    IMenuTypeExtension.create(AdvancedEnergyStorageMenu::new));

    public static final Supplier<MenuType<SuperiorEnergyStorageMenu>> SUPERIOR_ENERGY_STORAGE =
            HYBRID_MENU_TYPES.register("superior_energy_storage", () ->
                    IMenuTypeExtension.create(SuperiorEnergyStorageMenu::new));

    public static final Supplier<MenuType<QuantumEnergyStorageMenu>> QUANTUM_ENERGY_STORAGE =
            HYBRID_MENU_TYPES.register("quantum_energy_storage", () ->
                    IMenuTypeExtension.create(QuantumEnergyStorageMenu::new));

    // Machine Menu Types
    public static final Supplier<MenuType<HybridCoalGeneratorMenu>> HYBRID_COAL_GENERATOR =
            HYBRID_MENU_TYPES.register("hybrid_coal_generator", () ->
                    IMenuTypeExtension.create(HybridCoalGeneratorMenu::new));

    // Control System Menu Types
    public static final Supplier<MenuType<EnergyControllerMenu>> ENERGY_CONTROLLER =
            HYBRID_MENU_TYPES.register("energy_controller", () ->
                    IMenuTypeExtension.create(EnergyControllerMenu::new));

    public static final Supplier<MenuType<EnergyMonitorMenu>> ENERGY_MONITOR =
            HYBRID_MENU_TYPES.register("energy_monitor", () ->
                    IMenuTypeExtension.create(EnergyMonitorMenu::new));

    public static final Supplier<MenuType<EnergyConverterMenu>> ENERGY_CONVERTER =
            HYBRID_MENU_TYPES.register("energy_converter", () ->
                    IMenuTypeExtension.create(EnergyConverterMenu::new));

    // Network Infrastructure Menu Types
    public static final Supplier<MenuType<NetworkRelayMenu>> NETWORK_RELAY =
            HYBRID_MENU_TYPES.register("network_relay", () ->
                    IMenuTypeExtension.create(NetworkRelayMenu::new));

    public static final Supplier<MenuType<NetworkAmplifierMenu>> NETWORK_AMPLIFIER =
            HYBRID_MENU_TYPES.register("network_amplifier", () ->
                    IMenuTypeExtension.create(NetworkAmplifierMenu::new));

    public static final Supplier<MenuType<NetworkBridgeMenu>> NETWORK_BRIDGE =
            HYBRID_MENU_TYPES.register("network_bridge", () ->
                    IMenuTypeExtension.create(NetworkBridgeMenu::new));

    // Specialized Interface Menu Types
    public static final Supplier<MenuType<EnergyAnalyzerMenu>> ENERGY_ANALYZER =
            HYBRID_MENU_TYPES.register("energy_analyzer", () ->
                    IMenuTypeExtension.create(EnergyAnalyzerMenu::new));

    public static final Supplier<MenuType<NetworkDashboardMenu>> NETWORK_DASHBOARD =
            HYBRID_MENU_TYPES.register("network_dashboard", () ->
                    IMenuTypeExtension.create(NetworkDashboardMenu::new));

    public static final Supplier<MenuType<HybridConfiguratorMenu>> HYBRID_CONFIGURATOR =
            HYBRID_MENU_TYPES.register("hybrid_configurator", () ->
                    IMenuTypeExtension.create(HybridConfiguratorMenu::new));

    /**
     * Registers all hybrid menu types
     */
    public static void register(IEventBus eventBus) {
        HYBRID_MENU_TYPES.register(eventBus);
    }

    // ========== INNER MENU CLASS IMPLEMENTATIONS ==========
    // All menu classes are inner classes to maintain HybridMenuTypes.MenuClass pattern

    /**
     * Basic Energy Storage Menu
     */
    public static class BasicEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public BasicEnergyStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(BASIC_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity basicStorage) {
                this.data = new BasicEnergyStorageDataProvider(basicStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public BasicEnergyStorageMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(BASIC_ENERGY_STORAGE.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity basicStorage) {
                this.data = new BasicEnergyStorageDataProvider(basicStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Enhanced Energy Storage Menu
     */
    public static class EnhancedEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public EnhancedEnergyStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(ENHANCED_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity enhancedStorage) {
                this.data = new EnhancedEnergyStorageDataProvider(enhancedStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public EnhancedEnergyStorageMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(ENHANCED_ENERGY_STORAGE.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity enhancedStorage) {
                this.data = new EnhancedEnergyStorageDataProvider(enhancedStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Advanced Energy Storage Menu
     */
    public static class AdvancedEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public AdvancedEnergyStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(ADVANCED_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity advancedStorage) {
                this.data = new AdvancedEnergyStorageDataProvider(advancedStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public AdvancedEnergyStorageMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(ADVANCED_ENERGY_STORAGE.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity advancedStorage) {
                this.data = new AdvancedEnergyStorageDataProvider(advancedStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Superior Energy Storage Menu
     */
    public static class SuperiorEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public SuperiorEnergyStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(SUPERIOR_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity superiorStorage) {
                this.data = new SuperiorEnergyStorageDataProvider(superiorStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public SuperiorEnergyStorageMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(SUPERIOR_ENERGY_STORAGE.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity superiorStorage) {
                this.data = new SuperiorEnergyStorageDataProvider(superiorStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Quantum Energy Storage Menu
     */
    public static class QuantumEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public QuantumEnergyStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(QUANTUM_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity quantumStorage) {
                this.data = new QuantumEnergyStorageDataProvider(quantumStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public QuantumEnergyStorageMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(QUANTUM_ENERGY_STORAGE.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity quantumStorage) {
                this.data = new QuantumEnergyStorageDataProvider(quantumStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Hybrid Coal Generator Menu
     */
    public static class HybridCoalGeneratorMenu extends BaseEnergyStorageMenu {

        public HybridCoalGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, extraData);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity coalGen) {
                addSlot(new SlotItemHandler(coalGen.getInventory(), 0, 80, 35));
            }
        }

        public HybridCoalGeneratorMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity coalGen) {
                addSlot(new SlotItemHandler(coalGen.getInventory(), 0, 80, 35));
            }
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            ItemStack itemStack = ItemStack.EMPTY;
            net.minecraft.world.inventory.Slot slot = slots.get(index);

            if (slot != null && slot.hasItem()) {
                ItemStack stackInSlot = slot.getItem();
                itemStack = stackInSlot.copy();

                if (index == 0) {
                    if (!moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (net.neoforged.neoforge.common.CommonHooks.getBurnTime(stackInSlot, null) > 0) {
                        if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                }

                if (stackInSlot.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }

            return itemStack;
        }
    }

    /**
     * Energy Controller Menu
     */
    public static class EnergyControllerMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyControllerBlockEntity controllerBlockEntity;

        public EnergyControllerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(ENERGY_CONTROLLER.get(), containerId, playerInventory, extraData);
            this.controllerBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyControllerBlockEntity) blockEntity;
        }

        public EnergyControllerMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(ENERGY_CONTROLLER.get(), containerId, playerInventory, blockEntity);
            this.controllerBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyControllerBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyControllerBlockEntity getControllerBlockEntity() {
            return controllerBlockEntity;
        }
    }

    /**
     * Energy Monitor Menu
     */
    public static class EnergyMonitorMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyMonitorBlockEntity monitorBlockEntity;

        public EnergyMonitorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(ENERGY_MONITOR.get(), containerId, playerInventory, extraData);
            this.monitorBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyMonitorBlockEntity) blockEntity;
        }

        public EnergyMonitorMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(ENERGY_MONITOR.get(), containerId, playerInventory, blockEntity);
            this.monitorBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyMonitorBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyMonitorBlockEntity getMonitorBlockEntity() {
            return monitorBlockEntity;
        }
    }

    /**
     * Energy Converter Menu
     */
    public static class EnergyConverterMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyConverterBlockEntity converterBlockEntity;

        public EnergyConverterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(ENERGY_CONVERTER.get(), containerId, playerInventory, extraData);
            this.converterBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyConverterBlockEntity) blockEntity;
        }

        public EnergyConverterMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(ENERGY_CONVERTER.get(), containerId, playerInventory, blockEntity);
            this.converterBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyConverterBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyConverterBlockEntity getConverterBlockEntity() {
            return converterBlockEntity;
        }
    }

    /**
     * Network Relay Menu
     */
    public static class NetworkRelayMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkRelayBlockEntity relayBlockEntity;

        public NetworkRelayMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(NETWORK_RELAY.get(), containerId, playerInventory, extraData);
            this.relayBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkRelayBlockEntity) blockEntity;
        }

        public NetworkRelayMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(NETWORK_RELAY.get(), containerId, playerInventory, blockEntity);
            this.relayBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkRelayBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkRelayBlockEntity getRelayBlockEntity() {
            return relayBlockEntity;
        }
    }

    /**
     * Network Amplifier Menu
     */
    public static class NetworkAmplifierMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkAmplifierBlockEntity amplifierBlockEntity;

        public NetworkAmplifierMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(NETWORK_AMPLIFIER.get(), containerId, playerInventory, extraData);
            this.amplifierBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkAmplifierBlockEntity) blockEntity;
        }

        public NetworkAmplifierMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(NETWORK_AMPLIFIER.get(), containerId, playerInventory, blockEntity);
            this.amplifierBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkAmplifierBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkAmplifierBlockEntity getAmplifierBlockEntity() {
            return amplifierBlockEntity;
        }
    }

    /**
     * Network Bridge Menu
     */
    public static class NetworkBridgeMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkBridgeBlockEntity bridgeBlockEntity;

        public NetworkBridgeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(NETWORK_BRIDGE.get(), containerId, playerInventory, extraData);
            this.bridgeBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkBridgeBlockEntity) blockEntity;
        }

        public NetworkBridgeMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(NETWORK_BRIDGE.get(), containerId, playerInventory, blockEntity);
            this.bridgeBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkBridgeBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkBridgeBlockEntity getBridgeBlockEntity() {
            return bridgeBlockEntity;
        }
    }

    /**
     * Energy Analyzer Menu
     */
    public static class EnergyAnalyzerMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyAnalyzerBlockEntity analyzerBlockEntity;

        public EnergyAnalyzerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(ENERGY_ANALYZER.get(), containerId, playerInventory, extraData);
            this.analyzerBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyAnalyzerBlockEntity) blockEntity;
        }

        public EnergyAnalyzerMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(ENERGY_ANALYZER.get(), containerId, playerInventory, blockEntity);
            this.analyzerBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyAnalyzerBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.EnergyAnalyzerBlockEntity getAnalyzerBlockEntity() {
            return analyzerBlockEntity;
        }
    }

    /**
     * Network Dashboard Menu
     */
    public static class NetworkDashboardMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkDashboardBlockEntity dashboardBlockEntity;

        public NetworkDashboardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(NETWORK_DASHBOARD.get(), containerId, playerInventory, extraData);
            this.dashboardBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkDashboardBlockEntity) blockEntity;
        }

        public NetworkDashboardMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(NETWORK_DASHBOARD.get(), containerId, playerInventory, blockEntity);
            this.dashboardBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkDashboardBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.NetworkDashboardBlockEntity getDashboardBlockEntity() {
            return dashboardBlockEntity;
        }
    }

    /**
     * Hybrid Configurator Menu
     */
    public static class HybridConfiguratorMenu extends BaseEnergyStorageMenu {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.HybridConfiguratorBlockEntity configuratorBlockEntity;

        public HybridConfiguratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(HYBRID_CONFIGURATOR.get(), containerId, playerInventory, extraData);
            this.configuratorBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.HybridConfiguratorBlockEntity) blockEntity;
        }

        public HybridConfiguratorMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(HYBRID_CONFIGURATOR.get(), containerId, playerInventory, blockEntity);
            this.configuratorBlockEntity = (com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.HybridConfiguratorBlockEntity) blockEntity;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        public com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.HybridConfiguratorBlockEntity getConfiguratorBlockEntity() {
            return configuratorBlockEntity;
        }
    }

    // ========== DATA PROVIDER HELPER CLASSES ==========

    private static class BasicEnergyStorageDataProvider implements ContainerData {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity storage;

        public BasicEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity storage) {
            this.storage = storage;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStorage().getEnergyStored();
                case 1 -> storage.getEnergyStorage().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Read-only data
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private static class EnhancedEnergyStorageDataProvider implements ContainerData {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity storage;

        public EnhancedEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity storage) {
            this.storage = storage;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStorage().getEnergyStored();
                case 1 -> storage.getEnergyStorage().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Read-only data
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private static class AdvancedEnergyStorageDataProvider implements ContainerData {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity storage;

        public AdvancedEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity storage) {
            this.storage = storage;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStorage().getEnergyStored();
                case 1 -> storage.getEnergyStorage().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Read-only data
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private static class SuperiorEnergyStorageDataProvider implements ContainerData {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity storage;

        public SuperiorEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity storage) {
            this.storage = storage;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStorage().getEnergyStored();
                case 1 -> storage.getEnergyStorage().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Read-only data
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private static class QuantumEnergyStorageDataProvider implements ContainerData {
        private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity storage;

        public QuantumEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity storage) {
            this.storage = storage;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStorage().getEnergyStored();
                case 1 -> storage.getEnergyStorage().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Read-only data
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}