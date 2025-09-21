package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyControllerMenu;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyMonitorMenu;
import com.thewheatking.minecraftfarmertechmod.menu.EnergyConverterMenu;
import com.thewheatking.minecraftfarmertechmod.menu.NetworkRelayMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkAmplifierMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkBridgeMenu;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyAnalyzerMenu;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkDashboardMenu;
import com.thewheatking.minecraftfarmertechmod.screen.HybridConfiguratorMenu;

import java.util.function.Supplier;

/**
 * Registration for hybrid energy system menu types
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridMenuTypes.java
 * Purpose: Registers all menu types for the hybrid energy system GUIs including storage, machines, and control interfaces
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
    public static final DeferredHolder<MenuType<?>, MenuType<EnergyControllerMenu>> ENERGY_CONTROLLER =
            MENU_TYPES.register("energy_controller", () ->
                    IMenuTypeExtension.create(EnergyControllerMenu::new));

    public static final Supplier<MenuType<HybridCoalGeneratorMenu.EnergyMonitorMenu>> ENERGY_MONITOR =
            MENU_TYPES.register("energy_monitor", () ->
                    IMenuTypeExtension.create(EnergyMonitorMenu::new));

    public static final Supplier<MenuType<HybridCoalGeneratorMenu.EnergyConverterMenu>> ENERGY_CONVERTER =
            MENU_TYPES.register("energy_converter", () ->
                    IMenuTypeExtension.create(EnergyConverterMenu::new));

    // Network Infrastructure Menu Types
    public static final Supplier<MenuType<HybridCoalGeneratorMenu.NetworkRelayMenu>> NETWORK_RELAY =
            MENU_TYPES.register("network_relay", () ->
                    IMenuTypeExtension.create(NetworkRelayMenu::new));

    public static final Supplier<MenuType<HybridCoalGeneratorMenu.NetworkAmplifierMenu>> NETWORK_AMPLIFIER =
            MENU_TYPES.register("network_amplifier", () ->
                    IMenuTypeExtension.create(NetworkAmplifierMenu::new));

    public static final Supplier<MenuType<HybridCoalGeneratorMenu.NetworkBridgeMenu>> NETWORK_BRIDGE =
            MENU_TYPES.register("network_bridge", () ->
                    IMenuTypeExtension.create(NetworkBridgeMenu::new));

    // Specialized Interface Menu Types
    public static final Supplier<MenuType<HybridCoalGeneratorMenu.EnergyAnalyzerMenu>> ENERGY_ANALYZER =
            MENU_TYPES.register("energy_analyzer", () ->
                    IMenuTypeExtension.create(EnergyAnalyzerMenu::new));

    public static final Supplier<MenuType<HybridCoalGeneratorMenu.NetworkDashboardMenu>> NETWORK_DASHBOARD =
            MENU_TYPES.register("network_dashboard", () ->
                    IMenuTypeExtension.create(NetworkDashboardMenu::new));

    public static final Supplier<MenuType<HybridCoalGeneratorMenu.HybridConfiguratorMenu>> HYBRID_CONFIGURATOR =
            MENU_TYPES.register("hybrid_configurator", () ->
                    IMenuTypeExtension.create(HybridConfiguratorMenu::new));

    /**
     * Registers all hybrid menu types
     */
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }

    /**
     * Placeholder menu classes that need to be implemented
     * These are referenced above but need to be created in the appropriate packages
     */

    // Base Energy Storage Menu
    public static class BasicEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public BasicEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                      net.minecraft.network.FriendlyByteBuf extraData) {
            super(BASIC_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);

            // Get the block entity and create data provider
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity basicStorage) {
                this.data = new BasicEnergyStorageDataProvider(basicStorage);
                addDataSlots(data);
            } else {
                this.data = new net.minecraft.world.inventory.SimpleContainerData(4);
            }
        }

        // Energy status methods for GUI
        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public int getPreviousEnergy() { return data.get(2); }

        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        public int getScaledEnergy(int scale) {
            int energy = getCurrentEnergy();
            int maxEnergy = getMaxEnergy();
            if (maxEnergy == 0) return 0;
            long scaledEnergy = (long)energy * scale / maxEnergy;
            return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
        }

        public EnergyStatus getEnergyStatus() {
            int current = getCurrentEnergy();
            int previous = getPreviousEnergy();
            int max = getMaxEnergy();

            if (current >= max) return EnergyStatus.FULL;
            else if (current > previous) return EnergyStatus.CHARGING;
            else if (current < previous) return EnergyStatus.DISCHARGING;
            else return EnergyStatus.IDLE;
        }

        public int getEnergyBarColor() {
            float percentage = getEnergyPercentage();
            if (percentage < 0.33f) return 0xFFFF0000; // Red
            else if (percentage < 0.66f) return 0xFFFFFF00; // Yellow
            else return 0xFF00FF00; // Green
        }

        public enum EnergyStatus { CHARGING, DISCHARGING, FULL, IDLE }

        // Data provider for syncing energy data
        private static class BasicEnergyStorageDataProvider implements ContainerData {
            private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity blockEntity;
            private int previousEnergy = 0;

            public BasicEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.BasicEnergyStorageBlockEntity blockEntity) {
                this.blockEntity = blockEntity;
            }

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getEnergyStorage(null).getEnergyStored();
                    case 1 -> blockEntity.getEnergyStorage(null).getMaxEnergyStored();
                    case 2 -> previousEnergy;
                    case 3 -> 0; // Tick counter (unused for now)
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 2) previousEnergy = value;
            }

            @Override
            public int getCount() { return 4; }
        }
    }

    public static class EnhancedEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public EnhancedEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                         net.minecraft.network.FriendlyByteBuf extraData) {
            super(ENHANCED_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);

            // Get the block entity and create data provider
            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity enhancedStorage) {
                this.data = new EnhancedEnergyStorageDataProvider(enhancedStorage);
                addDataSlots(data);
            } else {
                this.data = new net.minecraft.world.inventory.SimpleContainerData(4);
            }
        }

        // Energy status methods for GUI (same pattern as Basic, different capacity)
        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public int getPreviousEnergy() { return data.get(2); }

        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        public int getScaledEnergy(int scale) {
            int energy = getCurrentEnergy();
            int maxEnergy = getMaxEnergy();
            if (maxEnergy == 0) return 0;
            long scaledEnergy = (long)energy * scale / maxEnergy;
            return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
        }

        public EnergyStatus getEnergyStatus() {
            int current = getCurrentEnergy();
            int previous = getPreviousEnergy();
            int max = getMaxEnergy();

            if (current >= max) return EnergyStatus.FULL;
            else if (current > previous) return EnergyStatus.CHARGING;
            else if (current < previous) return EnergyStatus.DISCHARGING;
            else return EnergyStatus.IDLE;
        }

        // Enhanced tier uses blue color scheme
        public int getEnergyBarColor() {
            float percentage = getEnergyPercentage();
            if (percentage < 0.33f) return 0xFF0066CC; // Dark blue for low
            else if (percentage < 0.66f) return 0xFF0099FF; // Medium blue for medium
            else return 0xFF00CCFF; // Light blue for high
        }

        public enum EnergyStatus { CHARGING, DISCHARGING, FULL, IDLE }

        // Data provider for syncing energy data
        private static class EnhancedEnergyStorageDataProvider implements ContainerData {
            private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity blockEntity;
            private int previousEnergy = 0;

            public EnhancedEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.EnhancedEnergyStorageBlockEntity blockEntity) {
                this.blockEntity = blockEntity;
            }

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getEnergyStorage(null).getEnergyStored();
                    case 1 -> blockEntity.getEnergyStorage(null).getMaxEnergyStored();
                    case 2 -> previousEnergy;
                    case 3 -> 0; // Tick counter (unused for now)
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 2) previousEnergy = value;
            }

            @Override
            public int getCount() { return 4; }
        }
    }

    public static class AdvancedEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public AdvancedEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                         net.minecraft.network.FriendlyByteBuf extraData) {
            super(ADVANCED_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);

            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity advancedStorage) {
                this.data = new AdvancedEnergyStorageDataProvider(advancedStorage);
                addDataSlots(data);
            } else {
                this.data = new net.minecraft.world.inventory.SimpleContainerData(4);
            }
        }

        // Same energy methods as Enhanced
        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public int getPreviousEnergy() { return data.get(2); }

        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        public int getScaledEnergy(int scale) {
            int energy = getCurrentEnergy();
            int maxEnergy = getMaxEnergy();
            if (maxEnergy == 0) return 0;
            long scaledEnergy = (long)energy * scale / maxEnergy;
            return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
        }

        public EnergyStatus getEnergyStatus() {
            int current = getCurrentEnergy();
            int previous = getPreviousEnergy();
            int max = getMaxEnergy();

            if (current >= max) return EnergyStatus.FULL;
            else if (current > previous) return EnergyStatus.CHARGING;
            else if (current < previous) return EnergyStatus.DISCHARGING;
            else return EnergyStatus.IDLE;
        }

        // Advanced tier uses purple color scheme
        public int getEnergyBarColor() {
            float percentage = getEnergyPercentage();
            if (percentage < 0.33f) return 0xFF6600CC; // Dark purple for low
            else if (percentage < 0.66f) return 0xFF9900FF; // Medium purple for medium
            else return 0xFFCC00FF; // Light purple for high
        }

        public enum EnergyStatus { CHARGING, DISCHARGING, FULL, IDLE }

        private static class AdvancedEnergyStorageDataProvider implements ContainerData {
            private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity blockEntity;
            private int previousEnergy = 0;

            public AdvancedEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.AdvancedEnergyStorageBlockEntity blockEntity) {
                this.blockEntity = blockEntity;
            }

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getEnergyStorage(null).getEnergyStored();
                    case 1 -> blockEntity.getEnergyStorage(null).getMaxEnergyStored();
                    case 2 -> previousEnergy;
                    case 3 -> 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 2) previousEnergy = value;
            }

            @Override
            public int getCount() { return 4; }
        }
    }

    public static class SuperiorEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public SuperiorEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                         net.minecraft.network.FriendlyByteBuf extraData) {
            super(SUPERIOR_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);

            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity superiorStorage) {
                this.data = new SuperiorEnergyStorageDataProvider(superiorStorage);
                addDataSlots(data);
            } else {
                this.data = new net.minecraft.world.inventory.SimpleContainerData(4);
            }
        }

        // Same energy methods as others
        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public int getPreviousEnergy() { return data.get(2); }

        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        public int getScaledEnergy(int scale) {
            int energy = getCurrentEnergy();
            int maxEnergy = getMaxEnergy();
            if (maxEnergy == 0) return 0;
            long scaledEnergy = (long)energy * scale / maxEnergy;
            return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
        }

        public EnergyStatus getEnergyStatus() {
            int current = getCurrentEnergy();
            int previous = getPreviousEnergy();
            int max = getMaxEnergy();

            if (current >= max) return EnergyStatus.FULL;
            else if (current > previous) return EnergyStatus.CHARGING;
            else if (current < previous) return EnergyStatus.DISCHARGING;
            else return EnergyStatus.IDLE;
        }

        // Superior tier uses gold color scheme
        public int getEnergyBarColor() {
            float percentage = getEnergyPercentage();
            if (percentage < 0.33f) return 0xFFCC6600; // Dark gold for low
            else if (percentage < 0.66f) return 0xFFFFAA00; // Medium gold for medium
            else return 0xFFFFDD00; // Light gold for high
        }

        public enum EnergyStatus { CHARGING, DISCHARGING, FULL, IDLE }

        private static class SuperiorEnergyStorageDataProvider implements ContainerData {
            private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity blockEntity;
            private int previousEnergy = 0;

            public SuperiorEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.SuperiorEnergyStorageBlockEntity blockEntity) {
                this.blockEntity = blockEntity;
            }

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getEnergyStorage(null).getEnergyStored();
                    case 1 -> blockEntity.getEnergyStorage(null).getMaxEnergyStored();
                    case 2 -> previousEnergy;
                    case 3 -> 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 2) previousEnergy = value;
            }

            @Override
            public int getCount() { return 4; }
        }
    }

    public static class QuantumEnergyStorageMenu extends BaseEnergyStorageMenu {
        private final ContainerData data;

        public QuantumEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                        net.minecraft.network.FriendlyByteBuf extraData) {
            super(QUANTUM_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);

            if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity quantumStorage) {
                this.data = new QuantumEnergyStorageDataProvider(quantumStorage);
                addDataSlots(data);
            } else {
                this.data = new net.minecraft.world.inventory.SimpleContainerData(4);
            }
        }

        // Same energy methods as others
        public int getCurrentEnergy() { return data.get(0); }
        public int getMaxEnergy() { return data.get(1); }
        public int getPreviousEnergy() { return data.get(2); }

        public float getEnergyPercentage() {
            int max = getMaxEnergy();
            return max == 0 ? 0.0f : (float) getCurrentEnergy() / max;
        }

        public int getScaledEnergy(int scale) {
            int energy = getCurrentEnergy();
            int maxEnergy = getMaxEnergy();
            if (maxEnergy == 0) return 0;
            long scaledEnergy = (long)energy * scale / maxEnergy;
            return energy > 0 ? Math.max(1, (int)scaledEnergy) : 0;
        }

        public EnergyStatus getEnergyStatus() {
            int current = getCurrentEnergy();
            int previous = getPreviousEnergy();
            int max = getMaxEnergy();

            if (current >= max) return EnergyStatus.FULL;
            else if (current > previous) return EnergyStatus.CHARGING;
            else if (current < previous) return EnergyStatus.DISCHARGING;
            else return EnergyStatus.IDLE;
        }

        // Quantum tier uses cyan color scheme
        public int getEnergyBarColor() {
            float percentage = getEnergyPercentage();
            if (percentage < 0.33f) return 0xFF0099CC; // Dark cyan for low
            else if (percentage < 0.66f) return 0xFF00CCFF; // Medium cyan for medium
            else return 0xFF00FFFF; // Light cyan for high
        }

        public enum EnergyStatus { CHARGING, DISCHARGING, FULL, IDLE }

        private static class QuantumEnergyStorageDataProvider implements ContainerData {
            private final com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity blockEntity;
            private int previousEnergy = 0;

            public QuantumEnergyStorageDataProvider(com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.QuantumEnergyStorageBlockEntity blockEntity) {
                this.blockEntity = blockEntity;
            }

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getEnergyStorage(null).getEnergyStored();
                    case 1 -> blockEntity.getEnergyStorage(null).getMaxEnergyStored();
                    case 2 -> previousEnergy;
                    case 3 -> 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 2) previousEnergy = value;
            }

            @Override
            public int getCount() { return 4; }
        }
    }

    // Energy Storage Menu Implementations
    public static class BasicEnergyStorageMenu extends BaseEnergyStorageMenu {
        public BasicEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                      net.minecraft.network.FriendlyByteBuf extraData) {
            super(BASIC_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
        }
    }

    public static class EnhancedEnergyStorageMenu extends BaseEnergyStorageMenu {
        public EnhancedEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                         net.minecraft.network.FriendlyByteBuf extraData) {
            super(ENHANCED_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
        }
    }

    public static class AdvancedEnergyStorageMenu extends BaseEnergyStorageMenu {
        public AdvancedEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                         net.minecraft.network.FriendlyByteBuf extraData) {
            super(ADVANCED_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
        }
    }

    public static class SuperiorEnergyStorageMenu extends BaseEnergyStorageMenu {
        public SuperiorEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                         net.minecraft.network.FriendlyByteBuf extraData) {
            super(SUPERIOR_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
        }
    }

    public static class QuantumEnergyStorageMenu extends BaseEnergyStorageMenu {
        public QuantumEnergyStorageMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                        net.minecraft.network.FriendlyByteBuf extraData) {
            super(QUANTUM_ENERGY_STORAGE.get(), containerId, playerInventory, extraData);
        }
    }

    // Machine Menu Implementations
    public static class HybridCoalGeneratorMenu extends BaseEnergyStorageMenu {

        public HybridCoalGeneratorMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                       net.minecraft.network.FriendlyByteBuf extraData) {
            super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, extraData);

            // Change from Slot to SlotItemHandler
            addSlot(new net.neoforged.neoforge.items.SlotItemHandler(
                    ((com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity) blockEntity).getInventory(),
                    0, 80, 36));
        }

        @Override
        public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
            net.minecraft.world.item.ItemStack itemStack = net.minecraft.world.item.ItemStack.EMPTY;
            net.minecraft.world.inventory.Slot slot = slots.get(index);

            if (slot.hasItem()) {
                net.minecraft.world.item.ItemStack stackInSlot = slot.getItem();
                itemStack = stackInSlot.copy();

                if (index < 1) {
                    // Moving from machine to player inventory
                    if (!moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                        return net.minecraft.world.item.ItemStack.EMPTY;
                    }
                } else {
                    // Moving from player inventory to machine
                    // Fix: Use the correct getBurnTime method signature
                    if (stackInSlot.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0) {
                        if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                            return net.minecraft.world.item.ItemStack.EMPTY;
                        }
                    } else {
                        return net.minecraft.world.item.ItemStack.EMPTY;
                    }
                }

                if (stackInSlot.isEmpty()) {
                    slot.set(net.minecraft.world.item.ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }

            return itemStack;
        }

        // Control System Menu Implementations
        public static class EnergyControllerMenu extends BaseEnergyStorageMenu {
            public EnergyControllerMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                        net.minecraft.network.FriendlyByteBuf extraData) {
                super(ENERGY_CONTROLLER.get(), containerId, playerInventory, extraData);
            }
        }

        public static class EnergyMonitorMenu extends BaseEnergyStorageMenu {
            public EnergyMonitorMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                     net.minecraft.network.FriendlyByteBuf extraData) {
                super(ENERGY_MONITOR.get(), containerId, playerInventory, extraData);
            }
        }

        public static class EnergyConverterMenu extends BaseEnergyStorageMenu {
            public EnergyConverterMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                       net.minecraft.network.FriendlyByteBuf extraData) {
                super(ENERGY_CONVERTER.get(), containerId, playerInventory, extraData);
            }
        }

        // Network Infrastructure Menu Implementations
        public static class NetworkRelayMenu extends BaseEnergyStorageMenu {
            public NetworkRelayMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                    net.minecraft.network.FriendlyByteBuf extraData) {
                super(NETWORK_RELAY.get(), containerId, playerInventory, extraData);
            }
        }

        public static class NetworkAmplifierMenu extends BaseEnergyStorageMenu {
            public NetworkAmplifierMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                        net.minecraft.network.FriendlyByteBuf extraData) {
                super(NETWORK_AMPLIFIER.get(), containerId, playerInventory, extraData);
            }
        }

        public static class NetworkBridgeMenu extends BaseEnergyStorageMenu {
            public NetworkBridgeMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                     net.minecraft.network.FriendlyByteBuf extraData) {
                super(NETWORK_BRIDGE.get(), containerId, playerInventory, extraData);
            }
        }

        // Specialized Interface Menu Implementations
        public static class EnergyAnalyzerMenu extends BaseEnergyStorageMenu {
            public EnergyAnalyzerMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                      net.minecraft.network.FriendlyByteBuf extraData) {
                super(ENERGY_ANALYZER.get(), containerId, playerInventory, extraData);
            }
        }

        public static class NetworkDashboardMenu extends BaseEnergyStorageMenu {
            public NetworkDashboardMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                        net.minecraft.network.FriendlyByteBuf extraData) {
                super(NETWORK_DASHBOARD.get(), containerId, playerInventory, extraData);
            }
        }

        public static class HybridConfiguratorMenu extends BaseEnergyStorageMenu {
            public HybridConfiguratorMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                          net.minecraft.network.FriendlyByteBuf extraData) {
                super(HYBRID_CONFIGURATOR.get(), containerId, playerInventory, extraData);
            }
        }

        /**
         * Helper methods for menu specifications
         */
        public static class MenuSpecifications {

            /**
             * Gets all energy storage menu types
             */
            public static MenuType<?>[] getEnergyStorageMenus() {
                return new MenuType<?>[]{
                        BASIC_ENERGY_STORAGE.get(),
                        ENHANCED_ENERGY_STORAGE.get(),
                        ADVANCED_ENERGY_STORAGE.get(),
                        SUPERIOR_ENERGY_STORAGE.get(),
                        QUANTUM_ENERGY_STORAGE.get()
                };
            }

            /**
             * Gets all machine menu types
             */
            public static MenuType<?>[] getMachineMenus() {
                return new MenuType<?>[]{
                        HYBRID_COAL_GENERATOR.get(),
                        ENERGY_CONTROLLER.get(),
                        ENERGY_MONITOR.get(),
                        ENERGY_CONVERTER.get()
                };
            }

            /**
             * Gets all network infrastructure menu types
             */
            public static MenuType<?>[] getNetworkMenus() {
                return new MenuType<?>[]{
                        NETWORK_RELAY.get(),
                        NETWORK_AMPLIFIER.get(),
                        NETWORK_BRIDGE.get()
                };
            }

            /**
             * Gets all specialized interface menu types
             */
            public static MenuType<?>[] getSpecializedMenus() {
                return new MenuType<?>[]{
                        ENERGY_ANALYZER.get(),
                        NETWORK_DASHBOARD.get(),
                        HYBRID_CONFIGURATOR.get()
                };
            }

            /**
             * Gets all hybrid menu types
             */
            public static MenuType<?>[] getAllHybridMenus() {
                java.util.List<MenuType<?>> allMenus = new java.util.ArrayList<>();
                allMenus.addAll(java.util.Arrays.asList(getEnergyStorageMenus()));
                allMenus.addAll(java.util.Arrays.asList(getMachineMenus()));
                allMenus.addAll(java.util.Arrays.asList(getNetworkMenus()));
                allMenus.addAll(java.util.Arrays.asList(getSpecializedMenus()));
                return allMenus.toArray(new MenuType<?>[0]);
            }
        }
    }
}