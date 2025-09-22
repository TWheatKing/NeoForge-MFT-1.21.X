package com.thewheatking.minecraftfarmertechmod.event;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.screen.ModMenuTypes;
import com.thewheatking.minecraftfarmertechmod.screen.CoalGeneratorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.IronFurnaceScreen;
import com.thewheatking.minecraftfarmertechmod.screen.LiquifierScreen;
import com.thewheatking.minecraftfarmertechmod.screen.BioGeneratorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.SideConfigScreen;
import com.thewheatking.minecraftfarmertechmod.screen.BasicEnergyStorageScreen;
import com.thewheatking.minecraftfarmertechmod.screen.EnhancedEnergyStorageScreen;
import com.thewheatking.minecraftfarmertechmod.screen.AdvancedEnergyStorageScreen;
import com.thewheatking.minecraftfarmertechmod.screen.SuperiorEnergyStorageScreen;
import com.thewheatking.minecraftfarmertechmod.screen.QuantumEnergyStorageScreen;
import com.thewheatking.minecraftfarmertechmod.screen.HybridCoalGeneratorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyControllerScreen;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyMonitorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyConverterScreen;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkRelayScreen;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkAmplifierScreen;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkBridgeScreen;
import com.thewheatking.minecraftfarmertechmod.screen.EnergyAnalyzerScreen;
import com.thewheatking.minecraftfarmertechmod.screen.NetworkDashboardScreen;
import com.thewheatking.minecraftfarmertechmod.screen.HybridConfiguratorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * UPDATED: Client mod events using consolidated ModMenuTypes
 * Removed HybridMenuTypes import - now using single ModMenuTypes for all registrations
 * Fixed by Claude for Minecraft 1.21 + NeoForge 21.0.167
 */
@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Client setup logic can go here if needed
        event.enqueueWork(() -> {
            // Any client-specific setup can go here
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        // ========== ORIGINAL MFT SCREEN REGISTRATIONS ==========
        event.register(ModMenuTypes.IRON_FURNACE_MENU.get(), IronFurnaceScreen::new);
        event.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ModMenuTypes.BIO_GENERATOR_MENU.get(), BioGeneratorScreen::new);
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(ModMenuTypes.SIDE_CONFIG_MENU.get(), SideConfigScreen::new);

        // ========== HYBRID ENERGY STORAGE SCREEN REGISTRATIONS ==========
        event.register(ModMenuTypes.BASIC_ENERGY_STORAGE_MENU.get(), BasicEnergyStorageScreen::new);
        event.register(ModMenuTypes.ENHANCED_ENERGY_STORAGE_MENU.get(), EnhancedEnergyStorageScreen::new);
        event.register(ModMenuTypes.ADVANCED_ENERGY_STORAGE_MENU.get(), AdvancedEnergyStorageScreen::new);
        event.register(ModMenuTypes.SUPERIOR_ENERGY_STORAGE_MENU.get(), SuperiorEnergyStorageScreen::new);
        event.register(ModMenuTypes.QUANTUM_ENERGY_STORAGE_MENU.get(), QuantumEnergyStorageScreen::new);

        // ========== HYBRID MACHINE SCREEN REGISTRATIONS ==========
        event.register(ModMenuTypes.HYBRID_COAL_GENERATOR_MENU.get(), HybridCoalGeneratorScreen::new);

        // ========== HYBRID CONTROL SYSTEM SCREEN REGISTRATIONS ==========
        event.register(ModMenuTypes.ENERGY_CONTROLLER_MENU.get(), EnergyControllerScreen::new);
        event.register(ModMenuTypes.ENERGY_MONITOR_MENU.get(), EnergyMonitorScreen::new);
        event.register(ModMenuTypes.ENERGY_CONVERTER_MENU.get(), EnergyConverterScreen::new);

        // ========== HYBRID NETWORK INFRASTRUCTURE SCREEN REGISTRATIONS ==========
        event.register(ModMenuTypes.NETWORK_RELAY_MENU.get(), NetworkRelayScreen::new);
        event.register(ModMenuTypes.NETWORK_AMPLIFIER_MENU.get(), NetworkAmplifierScreen::new);
        event.register(ModMenuTypes.NETWORK_BRIDGE_MENU.get(), NetworkBridgeScreen::new);

        // ========== HYBRID SPECIALIZED INTERFACE SCREEN REGISTRATIONS ==========
        event.register(ModMenuTypes.ENERGY_ANALYZER_MENU.get(), EnergyAnalyzerScreen::new);
        event.register(ModMenuTypes.NETWORK_DASHBOARD_MENU.get(), NetworkDashboardScreen::new);
        event.register(ModMenuTypes.HYBRID_CONFIGURATOR_MENU.get(), HybridConfiguratorScreen::new);
    }
}