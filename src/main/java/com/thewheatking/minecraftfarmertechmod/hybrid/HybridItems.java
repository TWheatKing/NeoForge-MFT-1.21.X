package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registration for hybrid energy system items
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridItems.java
 * Purpose: Registers all items for the hybrid energy system including components, tools, and upgrade items
 */
public class HybridItems {

    // Deferred register for items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, MinecraftFarmerTechMod.MOD_ID);

    // Energy Components
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

    // Circuit Components
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

    public static final Supplier<Item> QUANTUM_CIRCUIT = ITEMS.register("quantum_circuit",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
            ));

    // Cable Components
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

    public static final Supplier<Item> INSULATION_MATERIAL = ITEMS.register("insulation_material",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
            ));

    // Energy Crystals
    public static final Supplier<Item> ENERGY_CRYSTAL = ITEMS.register("energy_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> CHARGED_ENERGY_CRYSTAL = ITEMS.register("charged_energy_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> QUANTUM_CRYSTAL = ITEMS.register("quantum_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.EPIC)
            ));

    // Processing Materials
    public static final Supplier<Item> ENERGY_DUST = ITEMS.register("energy_dust",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
            ));

    public static final Supplier<Item> REFINED_ENERGY_DUST = ITEMS.register("refined_energy_dust",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> PURE_ENERGY_DUST = ITEMS.register("pure_energy_dust",
            () -> new Item(new Item.Properties()
                    .stacksTo(32)
                    .rarity(Rarity.RARE)
            ));

    // Upgrade Components
    public static final Supplier<Item> EFFICIENCY_UPGRADE = ITEMS.register("efficiency_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> CAPACITY_UPGRADE = ITEMS.register("capacity_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> HYBRID_UPGRADE = ITEMS.register("hybrid_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> QUANTUM_UPGRADE = ITEMS.register("quantum_upgrade",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
            ));

    // Tools and Equipment
    public static final Supplier<Item> ENERGY_METER = ITEMS.register("energy_meter",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
                    .durability(256)
            ));

    public static final Supplier<Item> NETWORK_ANALYZER = ITEMS.register("network_analyzer",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .durability(512)
            ));

    public static final Supplier<Item> ENERGY_WRENCH = ITEMS.register("energy_wrench",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
                    .durability(384)
            ));

    public static final Supplier<Item> HYBRID_CONFIGURATOR = ITEMS.register("hybrid_configurator",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .durability(1024)
            ));

    // Storage Devices
    public static final Supplier<Item> PORTABLE_ENERGY_CELL = ITEMS.register("portable_energy_cell",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> ADVANCED_ENERGY_CELL = ITEMS.register("advanced_energy_cell",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> QUANTUM_ENERGY_CELL = ITEMS.register("quantum_energy_cell",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
            ));

    // Specialty Items
    public static final Supplier<Item> ENERGY_CONDUIT = ITEMS.register("energy_conduit",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> DIMENSIONAL_CORE = ITEMS.register("dimensional_core",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
            ));

    public static final Supplier<Item> FLUX_CAPACITOR = ITEMS.register("flux_capacitor",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> RESONANCE_CHAMBER = ITEMS.register("resonance_chamber",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    // Raw Materials
    public static final Supplier<Item> ENERGIZED_METAL_INGOT = ITEMS.register("energized_metal_ingot",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> CHARGED_ALLOY = ITEMS.register("charged_alloy",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> QUANTUM_ALLOY = ITEMS.register("quantum_alloy",
            () -> new Item(new Item.Properties()
                    .stacksTo(32)
                    .rarity(Rarity.EPIC)
            ));

    // Machine Parts
    public static final Supplier<Item> MACHINE_FRAME = ITEMS.register("machine_frame",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.COMMON)
            ));

    public static final Supplier<Item> REINFORCED_MACHINE_FRAME = ITEMS.register("reinforced_machine_frame",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> ADVANCED_MACHINE_FRAME = ITEMS.register("advanced_machine_frame",
            () -> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> HEAT_EXCHANGER = ITEMS.register("heat_exchanger",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
            ));

    public static final Supplier<Item> COOLING_UNIT = ITEMS.register("cooling_unit",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)
            ));

    /**
     * Registers all hybrid items
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    /**
     * Helper methods for getting item specifications
     */
    public static class ItemSpecifications {
        // Energy Core Capacities (in FE)
        public static final int BASIC_CORE_CAPACITY = 10000;
        public static final int ENHANCED_CORE_CAPACITY = 50000;
        public static final int ADVANCED_CORE_CAPACITY = 250000;
        public static final int SUPERIOR_CORE_CAPACITY = 1000000;
        public static final int QUANTUM_CORE_CAPACITY = 5000000;

        // Crystal Capacities (in FE)
        public static final int ENERGY_CRYSTAL_CAPACITY = 100000;
        public static final int CHARGED_CRYSTAL_CAPACITY = 500000;
        public static final int QUANTUM_CRYSTAL_CAPACITY = 2500000;

        // Portable Cell Capacities (in FE)
        public static final int PORTABLE_CELL_CAPACITY = 250000;
        public static final int ADVANCED_CELL_CAPACITY = 1000000;
        public static final int QUANTUM_CELL_CAPACITY = 10000000;

        // Tool Durabilities
        public static final int ENERGY_METER_DURABILITY = 256;
        public static final int NETWORK_ANALYZER_DURABILITY = 512;
        public static final int ENERGY_WRENCH_DURABILITY = 384;
        public static final int HYBRID_CONFIGURATOR_DURABILITY = 1024;

        // Upgrade Effects
        public static final double EFFICIENCY_UPGRADE_BONUS = 0.15; // 15% efficiency increase
        public static final double SPEED_UPGRADE_BONUS = 0.25;      // 25% speed increase
        public static final double CAPACITY_UPGRADE_BONUS = 0.50;   // 50% capacity increase
        public static final double HYBRID_UPGRADE_BONUS = 0.75;     // 75% cross-system efficiency
        public static final double QUANTUM_UPGRADE_BONUS = 1.50;    // 150% all-around bonus
    }

    /**
     * Helper methods for getting recipe components
     */
    public static class RecipeComponents {

        /**
         * Gets the items needed for basic energy storage
         */
        public static Item[] getBasicStorageComponents() {
            return new Item[]{
                    BASIC_ENERGY_CORE.get(),
                    BASIC_CIRCUIT.get(),
                    MACHINE_FRAME.get(),
                    COPPER_WIRE.get()
            };
        }

        /**
         * Gets the items needed for enhanced energy storage
         */
        public static Item[] getEnhancedStorageComponents() {
            return new Item[]{
                    ENHANCED_ENERGY_CORE.get(),
                    ENHANCED_CIRCUIT.get(),
                    REINFORCED_MACHINE_FRAME.get(),
                    GOLD_WIRE.get(),
                    ENERGY_CRYSTAL.get()
            };
        }

        /**
         * Gets the items needed for advanced energy storage
         */
        public static Item[] getAdvancedStorageComponents() {
            return new Item[]{
                    ADVANCED_ENERGY_CORE.get(),
                    ADVANCED_CIRCUIT.get(),
                    ADVANCED_MACHINE_FRAME.get(),
                    DIAMOND_WIRE.get(),
                    CHARGED_ENERGY_CRYSTAL.get(),
                    FLUX_CAPACITOR.get()
            };
        }

        /**
         * Gets the items needed for cable crafting
         */
        public static Item[] getCableComponents(String cableType) {
            return switch (cableType.toLowerCase()) {
                case "copper" -> new Item[]{COPPER_WIRE.get(), BASIC_CIRCUIT.get()};
                case "copper_insulated" -> new Item[]{COPPER_WIRE.get(), INSULATION_MATERIAL.get()};
                case "gold" -> new Item[]{GOLD_WIRE.get(), ENHANCED_CIRCUIT.get()};
                case "gold_insulated" -> new Item[]{GOLD_WIRE.get(), INSULATION_MATERIAL.get()};
                case "diamond" -> new Item[]{DIAMOND_WIRE.get(), ADVANCED_CIRCUIT.get()};
                case "diamond_insulated" -> new Item[]{DIAMOND_WIRE.get(), INSULATION_MATERIAL.get()};
                default -> new Item[]{COPPER_WIRE.get()};
            };
        }
    }
}