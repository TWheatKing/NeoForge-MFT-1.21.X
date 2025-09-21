package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyControllerMenu;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyMonitorMenu;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyConverterMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkRelayMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkAmplifierMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkBridgeMenu;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyAnalyzerMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkDashboardMenu;
import com.thewheatking.minecraftfarmertechmod.screen.HybridConfiguratorMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ContainerData;
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
 * FIXED: Registration for hybrid energy system menu types
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 * Fixed by Claude for Minecraft 1.21 + Neoforge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridMenuTypes.java
 * Purpose: Registers all menu types for the hybrid energy system GUIs
 */
public class HybridMenuTypes {

    // Deferred register for menu types
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, MinecraftFarmerTechMod.MOD_ID);

    // Energy Storage Menu Types
    public static final Supplier<MenuType<BasicEnergyStorageMenu>> BASIC_ENERGY_STORAGE =
            MENU_TYPES.register("basic_energy_storage", () ->
                    IMenuTypeExtension.create(BasicEnergyStorageMenu::new));

    public static final Supplier<MenuType<EnhancedEnergyStorageMenu>> ENHANCED_ENERGY_STORAGE =
            MENU_TYPES.register("enhanced_energy_storage", () ->
                    IMenuTypeExtension.create(EnhancedEnergyStorageMenu::new));

    public static final Supplier<MenuType<AdvancedEnergyStorageMenu>> ADVANCED_ENERGY_STORAGE =
            MENU_TYPES.register("advanced_energy_storage", () ->
                    IMenuTypeExtension.create(AdvancedEnergyStorageMenu::new));

    public static final Supplier<MenuType<SuperiorEnergyStorageMenu>> SUPERIOR_ENERGY_STORAGE =
            MENU_TYPES.register("superior_energy_storage", () ->
                    IMenuTypeExtension.create(SuperiorEnergyStorageMenu::new));

    public static final Supplier<MenuType<QuantumEnergyStorageMenu>> QUANTUM_ENERGY_STORAGE =
            MENU_TYPES.register("quantum_energy_storage", () ->
                    IMenuTypeExtension.create(QuantumEnergyStorageMenu::new));

    // Machine Menu Types
    public static final Supplier<MenuType<HybridCoalGeneratorMenu>> HYBRID_COAL_GENERATOR =
            MENU_TYPES.register("hybrid_coal_generator", () ->
                    IMenuTypeExtension.create(HybridCoalGeneratorMenu::new));

    // Control System Menu Types
    public static final Supplier<MenuType<EnergyControllerMenu>> ENERGY_CONTROLLER =
            MENU_TYPES.register("energy_controller", () ->
                    IMenuTypeExtension.create(EnergyControllerMenu::new));

    public static final Supplier<MenuType<EnergyMonitorMenu>> ENERGY_MONITOR =
            MENU_TYPES.register("energy_monitor", () ->
                    IMenuTypeExtension.create(EnergyMonitorMenu::new));

    public static final Supplier<MenuType<EnergyConverterMenu>> ENERGY_CONVERTER =
            MENU_TYPES.register("energy_converter", () ->
                    IMenuTypeExtension.create(EnergyConverterMenu::new));

    // Network Infrastructure Menu Types
    public static final Supplier<MenuType<NetworkRelayMenu>> NETWORK_RELAY =
            MENU_TYPES.register("network_relay", () ->
                    IMenuTypeExtension.create(NetworkRelayMenu::new));

    public static final Supplier<MenuType<NetworkAmplifierMenu>> NETWORK_AMPLIFIER =
            MENU_TYPES.register("network_amplifier", () ->
                    IMenuTypeExtension.create(NetworkAmplifierMenu::new));

    public static final Supplier<MenuType<NetworkBridgeMenu>> NETWORK_BRIDGE =
            MENU_TYPES.register("network_bridge", () ->
                    IMenuTypeExtension.create(NetworkBridgeMenu::new));

    // Specialized Interface Menu Types
    public static final Supplier<MenuType<EnergyAnalyzerMenu>> ENERGY_ANALYZER =
            MENU_TYPES.register("energy_analyzer", () ->
                    IMenuTypeExtension.create(EnergyAnalyzerMenu::new));

    public static final Supplier<MenuType<NetworkDashboardMenu>> NETWORK_DASHBOARD =
            MENU_TYPES.register("network_dashboard", () ->
                    IMenuTypeExtension.create(NetworkDashboardMenu::new));

    public static final Supplier<MenuType<HybridConfiguratorMenu>> HYBRID_CONFIGURATOR =
            MENU_TYPES.register("hybrid_configurator", () ->
                    IMenuTypeExtension.create(HybridConfiguratorMenu::new));

    /**
     * Registers all hybrid menu types
     */
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }

    // ========== MENU IMPLEMENTATION CLASSES ==========

    /**
     * Basic Energy Storage Menu - simplest energy storage interface
     */
    public static class BasicEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public BasicEnergyStorageMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(BASIC_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);

            // Create basic energy data provider
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity basicStorage) {
                this.data = new BasicEnergyStorageDataProvider(basicStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        // Server-side constructor
        public BasicEnergyStorageMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(BASIC_ENERGY_STORAGE.get(), containerId, playerInventory, blockEntity);
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity basicStorage) {
                this.data = new BasicEnergyStorageDataProvider(basicStorage);
                addDataSlots(data);
            } else {
                this.data = new SimpleContainerData(4);
            }
        }

        // Energy status methods for GUI
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
     * Enhanced Energy Storage Menu - improved storage interface
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
     * Advanced Energy Storage Menu - high-capacity storage interface
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
     * Superior Energy Storage Menu - very high capacity storage interface
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
     * Quantum Energy Storage Menu - ultimate storage interface
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
     * Hybrid Coal Generator Menu - coal-powered generator interface
     */
    public static class HybridCoalGeneratorMenu extends BaseEnergyStorageMenu {

        public HybridCoalGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
            super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, extraData);

            // Add coal input slot
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity coalGen) {
                addSlot(new SlotItemHandler(
                        coalGen.getInventory(),
                        0, 80, 35)); // Coal input slot position
            }
        }

        public HybridCoalGeneratorMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
            super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, blockEntity);

            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity coalGen) {
                addSlot(new SlotItemHandler(
                        coalGen.getInventory(),
                        0, 80, 35));
            }
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            ItemStack itemStack = ItemStack.EMPTY;
            net.minecraft.world.inventory.Slot slot = slots.get(index);

            if (slot != null && slot.hasItem()) {
                ItemStack stackInSlot = slot.getItem();
                itemStack = stackInSlot.copy();

                // Handle item movement between inventories
                if (index == 0) {
                    // Moving from machine to player inventory
                    if (!moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // Moving from player inventory to machine
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