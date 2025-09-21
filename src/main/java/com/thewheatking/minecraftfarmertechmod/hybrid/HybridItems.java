package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * COMPLETED: Registration for hybrid energy system items
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 * Enhanced by Claude for Minecraft 1.21 + Neoforge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridItems.java
 * Purpose: Registers all items for the hybrid energy system including components, tools, and upgrade items
 */
public class HybridItems {

    // Deferred register for items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, MinecraftFarmerTechMod.MOD_ID);

    // Energy Components (Cores for energy storage)
    public static final Supplier<Item> BASIC_ENERGY_CORE = ITEMS.register("basic_energy_core",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.COMMON)
            ));

    public static final Supplier<Item> ENHANCED_ENERGY_CORE = ITEMS.register("enhanced_energy_core",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> ADVANCED_ENERGY_CORE = ITEMS.register("advanced_energy_core",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> SUPERIOR_ENERGY_CORE = ITEMS.register("superior_energy_core",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.EPIC)
            ));

    public static final Supplier<Item> QUANTUM_ENERGY_CORE = ITEMS.register("quantum_energy_core",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
            ));

    // Circuit Components (for machine crafting)
    public static final Supplier<Item> BASIC_CIRCUIT = ITEMS.register("basic_circuit",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
            ));

    public static final Supplier<Item> ENHANCED_CIRCUIT = ITEMS.register("enhanced_circuit",
            () -> new Item(new Item.Properties()
                    .stacksTo(32)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> ADVANCED_CIRCUIT = ITEMS.register("advanced_circuit",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> SUPERIOR_CIRCUIT = ITEMS.register("superior_circuit",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
            ));

    public static final Supplier<Item> QUANTUM_CIRCUIT = ITEMS.register("quantum_circuit",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.EPIC)
            ));

    // Cable Manufacturing Components
    public static final Supplier<Item> COPPER_WIRE = ITEMS.register("copper_wire",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
            ));

    public static final Supplier<Item> GOLD_WIRE = ITEMS.register("gold_wire",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> DIAMOND_WIRE = ITEMS.register("diamond_wire",
            () -> new Item(new Item.Properties()
                    .stacksTo(32)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> CABLE_INSULATION = ITEMS.register("cable_insulation",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
            ));

    // Machine Components
    public static final Supplier<Item> ENERGY_CELL_FRAME = ITEMS.register("energy_cell_frame",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.COMMON)
            ));

    public static final Supplier<Item> MACHINE_FRAME = ITEMS.register("machine_frame",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> REINFORCED_MACHINE_FRAME = ITEMS.register("reinforced_machine_frame",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.RARE)
            ));

    // Power Transfer Components
    public static final Supplier<Item> ENERGY_CONDUIT = ITEMS.register("energy_conduit",
            () -> new Item(new Item.Properties()
                    .stacksTo(32)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> ENERGY_RELAY = ITEMS.register("energy_relay",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> ENERGY_AMPLIFIER = ITEMS.register("energy_amplifier",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
            ));

    // Network Components
    public static final Supplier<Item> NETWORK_CHIP = ITEMS.register("network_chip",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> NETWORK_PROCESSOR = ITEMS.register("network_processor",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> QUANTUM_PROCESSOR = ITEMS.register("quantum_processor",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.EPIC)
            ));

    // Tools and Utilities
    public static final Supplier<Item> ENERGY_METER = ITEMS.register("energy_meter",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> NETWORK_ANALYZER = ITEMS.register("network_analyzer",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> HYBRID_WRENCH = ITEMS.register("hybrid_wrench",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.COMMON)
                    .durability(256)
            ));

    public static final Supplier<Item> CABLE_CUTTER = ITEMS.register("cable_cutter",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.COMMON)
                    .durability(128)
            ));

    // Upgrade Components
    public static final Supplier<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> EFFICIENCY_UPGRADE = ITEMS.register("efficiency_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> CAPACITY_UPGRADE = ITEMS.register("capacity_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> TRANSFER_UPGRADE = ITEMS.register("transfer_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)
            ));

    // Advanced Materials
    public static final Supplier<Item> CRYSTALLIZED_ENERGY = ITEMS.register("crystallized_energy",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.EPIC)
            ));

    public static final Supplier<Item> QUANTUM_CRYSTAL = ITEMS.register("quantum_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
            ));

    public static final Supplier<Item> STABILIZED_QUANTUM_CRYSTAL = ITEMS.register("stabilized_quantum_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.EPIC)
            ));

    /**
     * UTILITY METHOD: Register block items for hybrid blocks
     * Enhanced by Claude for proper cross-registration with HybridBlocks
     */
    public static <T extends Block> void registerBlockItem(String name, Supplier<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /**
     * Registers all hybrid items
     * Enhanced by Claude for Minecraft 1.21 + Neoforge 21.0.167 compatibility
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        MinecraftFarmerTechMod.LOGGER.info("Hybrid Items registered successfully!");
    }

    /**
     * Helper class for item specifications and utilities
     * Based on TWheatKing's original framework patterns
     */
    public static class ItemSpecifications {

        /**
         * Gets all energy core items for creative tab
         */
        public static Item[] getEnergyCores() {
            return new Item[] {
                    BASIC_ENERGY_CORE.get(),
                    ENHANCED_ENERGY_CORE.get(),
                    ADVANCED_ENERGY_CORE.get(),
                    SUPERIOR_ENERGY_CORE.get(),
                    QUANTUM_ENERGY_CORE.get()
            };
        }

        /**
         * Gets all circuit items for creative tab
         */
        public static Item[] getCircuits() {
            return new Item[] {
                    BASIC_CIRCUIT.get(),
                    ENHANCED_CIRCUIT.get(),
                    ADVANCED_CIRCUIT.get(),
                    SUPERIOR_CIRCUIT.get(),
                    QUANTUM_CIRCUIT.get()
            };
        }

        /**
         * Gets all tool items for creative tab
         */
        public static Item[] getTools() {
            return new Item[] {
                    ENERGY_METER.get(),
                    NETWORK_ANALYZER.get(),
                    HYBRID_WRENCH.get(),
                    CABLE_CUTTER.get()
            };
        }

        /**
         * Gets all upgrade items for creative tab
         */
        public static Item[] getUpgrades() {
            return new Item[] {
                    SPEED_UPGRADE.get(),
                    EFFICIENCY_UPGRADE.get(),
                    CAPACITY_UPGRADE.get(),
                    TRANSFER_UPGRADE.get()
            };
        }

        /**
         * Gets all cable components for creative tab
         */
        public static Item[] getCableComponents() {
            return new Item[] {
                    COPPER_WIRE.get(),
                    GOLD_WIRE.get(),
                    DIAMOND_WIRE.get(),
                    CABLE_INSULATION.get()
            };
        }

        /**
         * Gets all advanced materials for creative tab
         */
        public static Item[] getAdvancedMaterials() {
            return new Item[] {
                    CRYSTALLIZED_ENERGY.get(),
                    QUANTUM_CRYSTAL.get(),
                    STABILIZED_QUANTUM_CRYSTAL.get()
            };
        }
    }
}