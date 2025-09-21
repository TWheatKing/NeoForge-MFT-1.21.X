package com.thewheatking.minecraftfarmertechmod;

import com.thewheatking.minecraftfarmertechmod.attachment.ModAttachments;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities;
import com.thewheatking.minecraftfarmertechmod.component.ModDataComponents;
import com.thewheatking.minecraftfarmertechmod.effect.ModEffects;
import com.thewheatking.minecraftfarmertechmod.enchantment.ModEnchantmentEffects;
import com.thewheatking.minecraftfarmertechmod.energy.ModEnergyCapabilities;
import com.thewheatking.minecraftfarmertechmod.fluid.ModFluidTypes;
import com.thewheatking.minecraftfarmertechmod.fluid.ModFluids;
import com.thewheatking.minecraftfarmertechmod.item.ModCreativeModeTabs;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import com.thewheatking.minecraftfarmertechmod.potion.ModPotions;
import com.thewheatking.minecraftfarmertechmod.screen.ModMenuTypes;
import com.thewheatking.minecraftfarmertechmod.sound.ModSounds;
import com.thewheatking.minecraftfarmertechmod.util.ModItemProperties;

// Hybrid System Imports
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlocks;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridItems;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyCapabilityProviders;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MinecraftFarmerTechMod.MOD_ID)
public class MinecraftFarmerTechMod {
    public static final String MOD_ID = "minecraftfarmertechmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Hybrid System Control - Set to true to use new hybrid energy system
    public static final boolean USE_HYBRID_ENERGY_SYSTEM = true;

    public MinecraftFarmerTechMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        // Register Creative Mode Tabs
        ModCreativeModeTabs.register(modEventBus);

        // Register Core Systems (Always register these)
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModEnchantmentEffects.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // Register Block Entities and Menus (Always register existing ones)
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        // Register Fluid Systems
        ModFluids.register(modEventBus);
        ModFluidTypes.register(modEventBus);

        // Conditional Energy System Registration
        if (USE_HYBRID_ENERGY_SYSTEM) {
            LOGGER.info("Initializing Hybrid Energy System...");

            // Register hybrid system components
            HybridBlocks.register(modEventBus);
            HybridItems.register(modEventBus);
            HybridBlockEntities.register(modEventBus);
            HybridMenuTypes.register(modEventBus);

            // Register hybrid energy capabilities
            HybridEnergyCapabilityProviders.register(modEventBus);

        } else {
            LOGGER.info("Using Legacy Energy System...");

            // Register legacy energy capabilities
            ModEnergyCapabilities.register(modEventBus);
        }

        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Common setup logic
        if (USE_HYBRID_ENERGY_SYSTEM) {
            LOGGER.info("Hybrid Energy System initialized successfully!");
        } else {
            LOGGER.info("Legacy Energy System initialized successfully!");
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Keep your existing creative tab additions
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            // Your existing items
            event.accept(ModItems.WHEAT_INGOT);
            event.accept(ModItems.ZINC_INGOT);
            event.accept(ModItems.RAW_ZINC);
            event.accept(ModItems.ZINC_ALLOY);
            event.accept(ModItems.BRASS);
            event.accept(ModItems.ANDESITE_ALLOY);

            // Hybrid system components (if enabled)
            if (USE_HYBRID_ENERGY_SYSTEM) {
                    event.accept(HybridItems.COPPER_WIRE);
                    event.accept(HybridItems.CABLE_INSULATION);
                    event.accept(HybridItems.ENERGY_CELL_FRAME);
            }

            // Continue with your existing items...
            event.accept(ModItems.IRON_BIT);
            event.accept(ModItems.GOLD_BIT);
            event.accept(ModItems.COPPER_BIT);
            event.accept(ModItems.DIAMOND_BIT);
            event.accept(ModItems.NETHERITE_BIT);
            event.accept(ModItems.BASIC_CIRCUIT_BOARD);
            event.accept(ModItems.ADVANCED_CIRCUIT_BOARD);
            event.accept(ModItems.FUSION_CIRCUIT_BOARD);
            event.accept(ModItems.BASIC_BLADE);
            event.accept(ModItems.ADVANCED_BLADE);
            event.accept(ModItems.GRATE);
            event.accept(ModItems.WISK);
            event.accept(ModItems.RAM);
            event.accept(ModItems.IRON_PLATE);
            event.accept(ModItems.GOLD_PLATE);
            event.accept(ModItems.BRASS_PLATE);
            event.accept(ModItems.DIAMOND_PLATE);
            event.accept(ModItems.NETHERITE_PLATE);
        }

        if(event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){
            event.accept(ModBlocks.ZINC_ORE);
        }

        // Add new creative tab for hybrid energy system
        if (USE_HYBRID_ENERGY_SYSTEM && event.getTabKey() == CreativeModeTabs.REDSTONE) {
            // Add hybrid energy blocks to redstone tab
            event.accept(HybridBlocks.COPPER_CABLE);
            event.accept(HybridBlocks.GOLD_CABLE);
            event.accept(HybridBlocks.DIAMOND_CABLE);
            event.accept(HybridBlocks.BASIC_ENERGY_STORAGE);
            event.accept(HybridBlocks.ENHANCED_ENERGY_STORAGE);
            event.accept(HybridBlocks.ADVANCED_ENERGY_STORAGE);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        if (USE_HYBRID_ENERGY_SYSTEM) {
            LOGGER.info("Server starting with Hybrid Energy System");
        } else {
            LOGGER.info("Server starting with Legacy Energy System");
        }
    }

    @EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            ModItemProperties.addCustomItemProperties();

            if (USE_HYBRID_ENERGY_SYSTEM) {
                LOGGER.info("Client: Hybrid Energy System active");
            }
        }
    }
}