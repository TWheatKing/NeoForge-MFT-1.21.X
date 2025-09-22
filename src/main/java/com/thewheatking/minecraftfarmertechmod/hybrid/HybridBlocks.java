package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * COMPLETED: Registration for hybrid energy system blocks
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 * Enhanced by Claude for Minecraft 1.21 + Neoforge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridBlocks.java
 * Purpose: Registers all blocks for the hybrid energy system including storage, transmission, and machines
 */
public class HybridBlocks {

    // Deferred register for blocks
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, MinecraftFarmerTechMod.MOD_ID);

    // Energy Storage Blocks
    public static final Supplier<Block> BASIC_ENERGY_STORAGE = BLOCKS.register("basic_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 4.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 3)
            ));

    public static final Supplier<Block> ENHANCED_ENERGY_STORAGE = BLOCKS.register("enhanced_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 5.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 5)
            ));

    public static final Supplier<Block> ADVANCED_ENERGY_STORAGE = BLOCKS.register("advanced_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 7)
            ));

    public static final Supplier<Block> SUPERIOR_ENERGY_STORAGE = BLOCKS.register("superior_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 8.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 9)
            ));

    public static final Supplier<Block> QUANTUM_ENERGY_STORAGE = BLOCKS.register("quantum_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(6.0F, 12.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 12)
            ));

    // Cable Transmission Blocks (Copper Tier - 512 FE/t)
    public static final Supplier<Block> COPPER_CABLE = BLOCKS.register("copper_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(1.0F, 2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    public static final Supplier<Block> COPPER_CABLE_INSULATED = BLOCKS.register("copper_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(1.2F, 2.5F)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
            ));

    // Gold Tier Cables (2048 FE/t)
    public static final Supplier<Block> GOLD_CABLE = BLOCKS.register("gold_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 3.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(blockState -> 2)
            ));

    public static final Supplier<Block> GOLD_CABLE_INSULATED = BLOCKS.register("gold_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .requiresCorrectToolForDrops()
                    .strength(1.7F, 3.5F)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
                    .lightLevel(blockState -> 1)
            ));

    // Diamond Tier Cables (8192 FE/t)
    public static final Supplier<Block> DIAMOND_CABLE = BLOCKS.register("diamond_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 4.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(blockState -> 4)
            ));

    public static final Supplier<Block> DIAMOND_CABLE_INSULATED = BLOCKS.register("diamond_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 4.0F)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
                    .lightLevel(blockState -> 3)
            ));

    // Netherite Tier Cables (Tier 4 - 32768 FE/t) - EXPLOSION PROOF
    public static final Supplier<Block> NETHERITE_CABLE = BLOCKS.register("netherite_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 4.0F)       // Stronger than other cables
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
            ));

    public static final Supplier<Block> NETHERITE_CABLE_INSULATED = BLOCKS.register("netherite_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(2.5F, 5.0F)       // Even stronger when insulated
                    .sound(SoundType.WOOL)      // Insulated sound
                    .noOcclusion()
            ));


    // Machine Blocks
    public static final Supplier<Block> HYBRID_COAL_GENERATOR = BLOCKS.register("hybrid_coal_generator",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 5.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 6)
            ));

    // Control System Blocks
    public static final Supplier<Block> ENERGY_CONTROLLER = BLOCKS.register("energy_controller",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 8)
            ));

    public static final Supplier<Block> ENERGY_MONITOR = BLOCKS.register("energy_monitor",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 4.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 6)
            ));

    public static final Supplier<Block> ENERGY_CONVERTER = BLOCKS.register("energy_converter",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 5.5F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 4)
            ));

    // Network Infrastructure Blocks
    public static final Supplier<Block> NETWORK_RELAY = BLOCKS.register("network_relay",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 6.5F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 7)
            ));

    public static final Supplier<Block> NETWORK_AMPLIFIER = BLOCKS.register("network_amplifier",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 6.5F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 7)
            ));

    public static final Supplier<Block> NETWORK_BRIDGE = BLOCKS.register("network_bridge",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 7.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 5)
            ));

    // Specialized Interface Blocks
    public static final Supplier<Block> ENERGY_ANALYZER = BLOCKS.register("energy_analyzer",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(2.5F, 4.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 6)
            ));

    public static final Supplier<Block> NETWORK_DASHBOARD = BLOCKS.register("network_dashboard",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 5.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 8)
            ));

    public static final Supplier<Block> HYBRID_CONFIGURATOR = BLOCKS.register("hybrid_configurator",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 4)
            ));

    /**
     * Registers all hybrid blocks and their corresponding items
     * Enhanced by Claude for proper Minecraft 1.21 + Neoforge 21.0.167 compatibility
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        MinecraftFarmerTechMod.LOGGER.info("Hybrid Blocks registered successfully!");
    }

    /**
     * Utility method to register block items (called by HybridItems)
     * This ensures proper cross-registration between blocks and items
     */
    public static void registerBlockItems() {
        // Energy Storage Block Items
        HybridItems.registerBlockItem("basic_energy_storage", BASIC_ENERGY_STORAGE);
        HybridItems.registerBlockItem("enhanced_energy_storage", ENHANCED_ENERGY_STORAGE);
        HybridItems.registerBlockItem("advanced_energy_storage", ADVANCED_ENERGY_STORAGE);
        HybridItems.registerBlockItem("superior_energy_storage", SUPERIOR_ENERGY_STORAGE);
        HybridItems.registerBlockItem("quantum_energy_storage", QUANTUM_ENERGY_STORAGE);

        // Cable Transmission Block Items
        HybridItems.registerBlockItem("copper_cable", COPPER_CABLE);
        HybridItems.registerBlockItem("copper_cable_insulated", COPPER_CABLE_INSULATED);
        HybridItems.registerBlockItem("gold_cable", GOLD_CABLE);
        HybridItems.registerBlockItem("gold_cable_insulated", GOLD_CABLE_INSULATED);
        HybridItems.registerBlockItem("diamond_cable", DIAMOND_CABLE);

        // Machine Block Items
        HybridItems.registerBlockItem("hybrid_coal_generator", HYBRID_COAL_GENERATOR);

        // Control System Block Items
        HybridItems.registerBlockItem("energy_controller", ENERGY_CONTROLLER);
        HybridItems.registerBlockItem("energy_monitor", ENERGY_MONITOR);
        HybridItems.registerBlockItem("energy_converter", ENERGY_CONVERTER);

        // Network Infrastructure Block Items
        HybridItems.registerBlockItem("network_relay", NETWORK_RELAY);
        HybridItems.registerBlockItem("network_amplifier", NETWORK_AMPLIFIER);
        HybridItems.registerBlockItem("network_bridge", NETWORK_BRIDGE);

        // Specialized Interface Block Items
        HybridItems.registerBlockItem("energy_analyzer", ENERGY_ANALYZER);
        HybridItems.registerBlockItem("network_dashboard", NETWORK_DASHBOARD);
        HybridItems.registerBlockItem("hybrid_configurator", HYBRID_CONFIGURATOR);

        MinecraftFarmerTechMod.LOGGER.info("Hybrid Block Items registered successfully!");
    }
}