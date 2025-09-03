package com.thewheatking.minecraftfarmertechmod;

import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities;
import com.thewheatking.minecraftfarmertechmod.item.ModCreativeModeTabs;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import com.thewheatking.minecraftfarmertechmod.screen.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
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

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MinecraftFarmerTechMod.MOD_ID)
public class MinecraftFarmerTechMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "minecraftfarmertechmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public MinecraftFarmerTechMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        // Register the new block entities and menu types
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            //food related ingot
            event.accept(ModItems.WHEAT_INGOT);
            //machine related items
            event.accept(ModItems.ZINC_INGOT);
            event.accept(ModItems.RAW_ZINC);
            event.accept(ModItems.ZINC_ALLOY);
            event.accept(ModItems.BRASS);
            event.accept(ModItems.ANDESITE_ALLOY);
            //bits
            event.accept(ModItems.IRON_BIT);
            event.accept(ModItems.GOLD_BIT);
            event.accept(ModItems.COPPER_BIT);
            event.accept(ModItems.DIAMOND_BIT);
            event.accept(ModItems.NETHERITE_BIT);
            //cbs
            event.accept(ModItems.BASIC_CIRCUIT_BOARD);
            event.accept(ModItems.ADVANCED_CIRCUIT_BOARD);
            event.accept(ModItems.FUSION_CIRCUIT_BOARD);
            //machine parts
            event.accept(ModItems.BASIC_BLADE);
            event.accept(ModItems.ADVANCED_BLADE);
            event.accept(ModItems.GRATE);
            event.accept(ModItems.WISK);
            event.accept(ModItems.RAM);
            //plates
            event.accept(ModItems.IRON_PLATE);
            event.accept(ModItems.GOLD_PLATE);
            event.accept(ModItems.BRASS_PLATE);
            event.accept(ModItems.DIAMOND_PLATE);
            event.accept(ModItems.NETHERITE_PLATE);
        }
        if(event.getTabKey() ==CreativeModeTabs.NATURAL_BLOCKS){
            event.accept(ModBlocks.ZINC_ORE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
             //Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
