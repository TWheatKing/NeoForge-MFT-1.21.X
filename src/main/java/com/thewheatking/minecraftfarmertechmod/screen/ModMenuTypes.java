package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * CONSOLIDATED: All menu type registrations for Minecraft Farmer Tech Mod
 * Based on TWheatKing's original MFT framework - consolidated for simplicity
 * Fixed and enhanced by Claude for Minecraft 1.21 + NeoForge 21.0.167
 *
 * This file replaces both ModMenuTypes.java and HybridMenuTypes.java
 * All menu classes should be separate files in their respective packages
 */
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, MinecraftFarmerTechMod.MOD_ID);

    // ========== ORIGINAL MFT MENU TYPES ==========

    public static final Supplier<MenuType<IronFurnaceMenu>> IRON_FURNACE_MENU =
            MENUS.register("iron_furnace_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new IronFurnaceMenu(containerId, inventory, data)));

    public static final Supplier<MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU =
            MENUS.register("coal_generator_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new CoalGeneratorMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<LiquifierMenu>> LIQUIFIER_MENU =
            MENUS.register("liquifier_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new LiquifierMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<BioGeneratorMenu>> BIO_GENERATOR_MENU =
            MENUS.register("bio_generator_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new BioGeneratorMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<SideConfigMenu>> SIDE_CONFIG_MENU =
            MENUS.register("side_config_menu", () -> IMenuTypeExtension.create(SideConfigMenu::new));

    // ========== HYBRID ENERGY STORAGE MENU TYPES ==========

    public static final DeferredHolder<MenuType<?>, MenuType<BasicEnergyStorageMenu>> BASIC_ENERGY_STORAGE_MENU =
            MENUS.register("basic_energy_storage_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new BasicEnergyStorageMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<EnhancedEnergyStorageMenu>> ENHANCED_ENERGY_STORAGE_MENU =
            MENUS.register("enhanced_energy_storage_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new EnhancedEnergyStorageMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<HybridMenuTypes.AdvancedEnergyStorageMenu>> ADVANCED_ENERGY_STORAGE_MENU =
            MENUS.register("advanced_energy_storage_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new HybridMenuTypes.AdvancedEnergyStorageMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<HybridMenuTypes.SuperiorEnergyStorageMenu>> SUPERIOR_ENERGY_STORAGE_MENU =
            MENUS.register("superior_energy_storage_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new HybridMenuTypes.SuperiorEnergyStorageMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<HybridMenuTypes.QuantumEnergyStorageMenu>> QUANTUM_ENERGY_STORAGE_MENU =
            MENUS.register("quantum_energy_storage_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new HybridMenuTypes.QuantumEnergyStorageMenu(containerId, inventory, data)));

    // ========== HYBRID MACHINE MENU TYPES ==========

    public static final DeferredHolder<MenuType<?>, MenuType<HybridCoalGeneratorMenu>> HYBRID_COAL_GENERATOR_MENU =
            MENUS.register("hybrid_coal_generator_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new HybridCoalGeneratorMenu(containerId, inventory, data)));

    // ========== HYBRID CONTROL SYSTEM MENU TYPES ==========

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyControllerMenu>> ENERGY_CONTROLLER_MENU =
            MENUS.register("energy_controller_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new EnergyControllerMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyMonitorMenu>> ENERGY_MONITOR_MENU =
            MENUS.register("energy_monitor_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new EnergyMonitorMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyConverterMenu>> ENERGY_CONVERTER_MENU =
            MENUS.register("energy_converter_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new EnergyConverterMenu(containerId, inventory, data)));

    // ========== HYBRID NETWORK INFRASTRUCTURE MENU TYPES ==========

    public static final DeferredHolder<MenuType<?>, MenuType<NetworkRelayMenu>> NETWORK_RELAY_MENU =
            MENUS.register("network_relay_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new NetworkRelayMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<NetworkAmplifierMenu>> NETWORK_AMPLIFIER_MENU =
            MENUS.register("network_amplifier_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new NetworkAmplifierMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<NetworkBridgeMenu>> NETWORK_BRIDGE_MENU =
            MENUS.register("network_bridge_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new NetworkBridgeMenu(containerId, inventory, data)));

    // ========== HYBRID SPECIALIZED INTERFACE MENU TYPES ==========

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyAnalyzerMenu>> ENERGY_ANALYZER_MENU =
            MENUS.register("energy_analyzer_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new EnergyAnalyzerMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<NetworkDashboardMenu>> NETWORK_DASHBOARD_MENU =
            MENUS.register("network_dashboard_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new NetworkDashboardMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<HybridMenuTypes.HybridConfiguratorMenu>> HYBRID_CONFIGURATOR_MENU =
            MENUS.register("hybrid_configurator_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new HybridMenuTypes.HybridConfiguratorMenu(containerId, inventory, data)));

    /**
     * Registers all menu types to the event bus
     * Called from your main mod class during setup
     */
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}