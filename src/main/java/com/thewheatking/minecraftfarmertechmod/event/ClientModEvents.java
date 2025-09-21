package com.thewheatking.minecraftfarmertechmod.event;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import com.thewheatking.minecraftfarmertechmod.screen.CoalGeneratorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.IronFurnaceScreen;
import com.thewheatking.minecraftfarmertechmod.screen.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
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

@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Client setup logic can go here if needed
        event.enqueueWork(() -> {
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.IRON_FURNACE_MENU.get(), IronFurnaceScreen::new);
        event.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ModMenuTypes.BIO_GENERATOR_MENU.get(), BioGeneratorScreen::new);
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(ModMenuTypes.SIDE_CONFIG_MENU.get(), SideConfigScreen::new);
        event.register(HybridMenuTypes.BASIC_ENERGY_STORAGE.get(), BasicEnergyStorageScreen::new);
        event.register(HybridMenuTypes.ENHANCED_ENERGY_STORAGE.get(), EnhancedEnergyStorageScreen::new);
        event.register(HybridMenuTypes.ADVANCED_ENERGY_STORAGE.get(), AdvancedEnergyStorageScreen::new);
        event.register(HybridMenuTypes.SUPERIOR_ENERGY_STORAGE.get(), SuperiorEnergyStorageScreen::new);
        event.register(HybridMenuTypes.QUANTUM_ENERGY_STORAGE.get(), QuantumEnergyStorageScreen::new);
        event.register(HybridMenuTypes.HYBRID_COAL_GENERATOR.get(), HybridCoalGeneratorScreen::new);
        event.register(HybridMenuTypes.ENERGY_CONTROLLER.get(), EnergyControllerScreen::new);
        event.register(HybridMenuTypes.ENERGY_MONITOR.get(), EnergyMonitorScreen::new);
        event.register(HybridMenuTypes.ENERGY_CONVERTER.get(), EnergyConverterScreen::new);
        event.register(HybridMenuTypes.NETWORK_RELAY.get(), NetworkRelayScreen::new);
        event.register(HybridMenuTypes.NETWORK_AMPLIFIER.get(), NetworkAmplifierScreen::new);
        event.register(HybridMenuTypes.NETWORK_BRIDGE.get(), NetworkBridgeScreen::new);
        event.register(HybridMenuTypes.ENERGY_ANALYZER.get(), EnergyAnalyzerScreen::new);
        event.register(HybridMenuTypes.NETWORK_DASHBOARD.get(), NetworkDashboardScreen::new);
        event.register(HybridMenuTypes.HYBRID_CONFIGURATOR.get(), HybridConfiguratorScreen::new);
    }
}