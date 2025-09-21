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
 * Registration for hybrid energy system blocks
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
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
            ));

    public static final Supplier<Block> ENHANCED_ENERGY_STORAGE = BLOCKS.register("enhanced_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 5.0F)
                    .sound(SoundType.METAL)
            ));

    public static final Supplier<Block> ADVANCED_ENERGY_STORAGE = BLOCKS.register("advanced_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 6.0F)
                    .sound(SoundType.METAL)
            ));

    public static final Supplier<Block> SUPERIOR_ENERGY_STORAGE = BLOCKS.register("superior_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 7.0F)
                    .sound(SoundType.METAL)
            ));

    public static final Supplier<Block> QUANTUM_ENERGY_STORAGE = BLOCKS.register("quantum_energy_storage",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 8.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 5)
            ));

    // Cable Transmission Blocks
    public static final Supplier<Block> COPPER_CABLE = BLOCKS.register("copper_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(1.0F, 2.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
            ));

    public static final Supplier<Block> COPPER_CABLE_INSULATED = BLOCKS.register("copper_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .requiresCorrectToolForDrops()
                    .strength(1.2F, 2.5F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
            ));

    public static final Supplier<Block> GOLD_CABLE = BLOCKS.register("gold_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 3.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    public static final Supplier<Block> GOLD_CABLE_INSULATED = BLOCKS.register("gold_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .requiresCorrectToolForDrops()
                    .strength(1.7F, 3.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    public static final Supplier<Block> DIAMOND_CABLE = BLOCKS.register("diamond_cable",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIAMOND)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 4.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(blockState -> 3)
            ));

    public static final Supplier<Block> DIAMOND_CABLE_INSULATED = BLOCKS.register("diamond_cable_insulated",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIAMOND)
                    .requiresCorrectToolForDrops()
                    .strength(2.2F, 4.5F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(blockState -> 2)
            ));

    // Machine Blocks
    public static final Supplier<Block> HYBRID_COAL_GENERATOR = BLOCKS.register("hybrid_coal_generator",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.STONE)
                    .lightLevel(blockState -> blockState.getValue(net.minecraft.world.level.block.AbstractFurnaceBlock.LIT) ? 13 : 0)
            ));

    // Hybrid System Control Blocks
    public static final Supplier<Block> ENERGY_CONTROLLER = BLOCKS.register("energy_controller",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F, 8.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 8)
            ));

    public static final Supplier<Block> ENERGY_MONITOR = BLOCKS.register("energy_monitor",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 5.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 6)
            ));

    public static final Supplier<Block> ENERGY_CONVERTER = BLOCKS.register("energy_converter",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(4.5F, 7.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 4)
            ));

    // Network Infrastructure Blocks
    public static final Supplier<Block> NETWORK_RELAY = BLOCKS.register("network_relay",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(blockState -> 4)
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

    // Utility function to register block items
    private static <T extends Block> void registerBlockItem(String name, Supplier<T> block) {
        HybridItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /**
     * Registers all hybrid blocks and their corresponding items
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);

        // Register block items
        registerBlockItems();
    }

    /**
     * Registers block items for all hybrid blocks
     */
    private static void registerBlockItems() {
        // Energy Storage Block Items
        registerBlockItem("basic_energy_storage", BASIC_ENERGY_STORAGE);
        registerBlockItem("enhanced_energy_storage", ENHANCED_ENERGY_STORAGE);
        registerBlockItem("advanced_energy_storage", ADVANCED_ENERGY_STORAGE);
        registerBlockItem("superior_energy_storage", SUPERIOR_ENERGY_STORAGE);
        registerBlockItem("quantum_energy_storage", QUANTUM_ENERGY_STORAGE);

        // Cable Transmission Block Items
        registerBlockItem("copper_cable", COPPER_CABLE);
        registerBlockItem("copper_cable_insulated", COPPER_CABLE_INSULATED);
        registerBlockItem("gold_cable", GOLD_CABLE);
        registerBlockItem("gold_cable_insulated", GOLD_CABLE_INSULATED);
        registerBlockItem("diamond_cable", DIAMOND_CABLE);
        registerBlockItem("diamond_cable_insulated", DIAMOND_CABLE_INSULATED);

        // Machine Block Items
        registerBlockItem("hybrid_coal_generator", HYBRID_COAL_GENERATOR);

        // Control Block Items
        registerBlockItem("energy_controller", ENERGY_CONTROLLER);
        registerBlockItem("energy_monitor", ENERGY_MONITOR);
        registerBlockItem("energy_converter", ENERGY_CONVERTER);

        // Network Infrastructure Block Items
        registerBlockItem("network_relay", NETWORK_RELAY);
        registerBlockItem("network_amplifier", NETWORK_AMPLIFIER);
        registerBlockItem("network_bridge", NETWORK_BRIDGE);
    }

    /**
     * Helper methods for getting energy storage capacities
     */
    public static class EnergyStorageCapacities {
        public static final int BASIC_CAPACITY = 50000;      // 50k FE
        public static final int ENHANCED_CAPACITY = 200000;   // 200k FE
        public static final int ADVANCED_CAPACITY = 1000000; // 1M FE
        public static final int SUPERIOR_CAPACITY = 5000000; // 5M FE
        public static final int QUANTUM_CAPACITY = 25000000; // 25M FE

        public static final int BASIC_TRANSFER = 1000;       // 1k FE/t
        public static final int ENHANCED_TRANSFER = 5000;    // 5k FE/t
        public static final int ADVANCED_TRANSFER = 20000;   // 20k FE/t
        public static final int SUPERIOR_TRANSFER = 100000;  // 100k FE/t
        public static final int QUANTUM_TRANSFER = 500000;   // 500k FE/t
    }

    /**
     * Helper methods for getting cable transfer rates
     */
    public static class CableTransferRates {
        public static final int COPPER_RATE = 512;          // 512 FE/t
        public static final int COPPER_INSULATED_RATE = 1024; // 1024 FE/t
        public static final int GOLD_RATE = 2048;           // 2048 FE/t
        public static final int GOLD_INSULATED_RATE = 4096; // 4096 FE/t
        public static final int DIAMOND_RATE = 8192;        // 8192 FE/t
        public static final int DIAMOND_INSULATED_RATE = 16384; // 16384 FE/t

        public static final double COPPER_LOSS = 0.02;      // 2% loss per block
        public static final double COPPER_INSULATED_LOSS = 0.01; // 1% loss per block
        public static final double GOLD_LOSS = 0.015;       // 1.5% loss per block
        public static final double GOLD_INSULATED_LOSS = 0.005; // 0.5% loss per block
        public static final double DIAMOND_LOSS = 0.01;     // 1% loss per block
        public static final double DIAMOND_INSULATED_LOSS = 0.001; // 0.1% loss per block
    }

    /**
     * Helper methods for getting machine specifications
     */
    public static class MachineSpecifications {
        public static final int COAL_GENERATOR_CAPACITY = 32000;    // 32k FE
        public static final int COAL_GENERATOR_PRODUCTION = 40;     // 40 FE/t
        public static final int COAL_GENERATOR_TRANSFER = 200;      // 200 FE/t

        public static final int CONTROLLER_CAPACITY = 100000;      // 100k FE
        public static final int CONTROLLER_TRANSFER = 10000;       // 10k FE/t

        public static final int MONITOR_CAPACITY = 50000;          // 50k FE
        public static final int MONITOR_TRANSFER = 5000;           // 5k FE/t

        public static final int CONVERTER_CAPACITY = 200000;       // 200k FE
        public static final int CONVERTER_TRANSFER = 20000;        // 20k FE/t
        public static final double CONVERTER_EFFICIENCY = 0.95;    // 95% efficiency

        public static final int RELAY_TRANSFER = 8000;             // 8k FE/t
        public static final int AMPLIFIER_TRANSFER = 16000;        // 16k FE/t
        public static final int BRIDGE_TRANSFER = 32000;           // 32k FE/t
    }
}