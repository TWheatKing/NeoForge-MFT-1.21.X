package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.*;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.*;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * COMPLETED: Registration for hybrid energy system block entities
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 * Enhanced by Claude for Minecraft 1.21 + Neoforge 21.0.167
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/hybrid/HybridBlockEntities.java
 * Purpose: Registers all block entities for the hybrid energy system including storage, transmission, and machines
 */
public class HybridBlockEntities {

    // Deferred register for block entities
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, MinecraftFarmerTechMod.MOD_ID);

    // Energy Storage Block Entities
    public static final Supplier<BlockEntityType<BasicEnergyStorageBlockEntity>> BASIC_ENERGY_STORAGE =
            BLOCK_ENTITIES.register("basic_energy_storage", () ->
                    BlockEntityType.Builder.of(BasicEnergyStorageBlockEntity::new,
                                    HybridBlocks.BASIC_ENERGY_STORAGE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<EnhancedEnergyStorageBlockEntity>> ENHANCED_ENERGY_STORAGE =
            BLOCK_ENTITIES.register("enhanced_energy_storage", () ->
                    BlockEntityType.Builder.of(EnhancedEnergyStorageBlockEntity::new,
                                    HybridBlocks.ENHANCED_ENERGY_STORAGE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<AdvancedEnergyStorageBlockEntity>> ADVANCED_ENERGY_STORAGE =
            BLOCK_ENTITIES.register("advanced_energy_storage", () ->
                    BlockEntityType.Builder.of(AdvancedEnergyStorageBlockEntity::new,
                                    HybridBlocks.ADVANCED_ENERGY_STORAGE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<SuperiorEnergyStorageBlockEntity>> SUPERIOR_ENERGY_STORAGE =
            BLOCK_ENTITIES.register("superior_energy_storage", () ->
                    BlockEntityType.Builder.of(SuperiorEnergyStorageBlockEntity::new,
                                    HybridBlocks.SUPERIOR_ENERGY_STORAGE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<QuantumEnergyStorageBlockEntity>> QUANTUM_ENERGY_STORAGE =
            BLOCK_ENTITIES.register("quantum_energy_storage", () ->
                    BlockEntityType.Builder.of(QuantumEnergyStorageBlockEntity::new,
                                    HybridBlocks.QUANTUM_ENERGY_STORAGE.get())
                            .build(null));

    // Cable Transmission Block Entities
    public static final Supplier<BlockEntityType<CopperCableBlockEntity>> COPPER_CABLE =
            BLOCK_ENTITIES.register("copper_cable", () ->
                    BlockEntityType.Builder.of(CopperCableBlockEntity::new,
                                    HybridBlocks.COPPER_CABLE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<CopperCableInsulatedBlockEntity>> COPPER_CABLE_INSULATED =
            BLOCK_ENTITIES.register("copper_cable_insulated", () ->
                    BlockEntityType.Builder.of(CopperCableInsulatedBlockEntity::new,
                                    HybridBlocks.COPPER_CABLE_INSULATED.get())
                            .build(null));

    public static final Supplier<BlockEntityType<GoldCableBlockEntity>> GOLD_CABLE =
            BLOCK_ENTITIES.register("gold_cable", () ->
                    BlockEntityType.Builder.of(GoldCableBlockEntity::new,
                                    HybridBlocks.GOLD_CABLE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<GoldCableInsulatedBlockEntity>> GOLD_CABLE_INSULATED =
            BLOCK_ENTITIES.register("gold_cable_insulated", () ->
                    BlockEntityType.Builder.of(GoldCableInsulatedBlockEntity::new,
                                    HybridBlocks.GOLD_CABLE_INSULATED.get())
                            .build(null));

    public static final Supplier<BlockEntityType<DiamondCableBlockEntity>> DIAMOND_CABLE =
            BLOCK_ENTITIES.register("diamond_cable", () ->
                    BlockEntityType.Builder.of(DiamondCableBlockEntity::new,
                                    HybridBlocks.DIAMOND_CABLE.get())
                            .build(null));

    public static final Supplier<BlockEntityType<GoldCableInsulatedBlockEntity>> DIAMOND_CABLE_INSULATED =
            BLOCK_ENTITIES.register("diamond_cable_insulated", () ->
                    BlockEntityType.Builder.of(GoldCableInsulatedBlockEntity::new,
                                    HybridBlocks.DIAMOND_CABLE_INSULATED.get())
                            .build(null));

    // Machine Block Entities
    public static final Supplier<BlockEntityType<CoalGeneratorBlockEntity>> HYBRID_COAL_GENERATOR =
            BLOCK_ENTITIES.register("hybrid_coal_generator", () ->
                    BlockEntityType.Builder.of(CoalGeneratorBlockEntity::new,
                                    HybridBlocks.HYBRID_COAL_GENERATOR.get())
                            .build(null));

    // Control System Block Entities
    public static final Supplier<BlockEntityType<EnergyControllerBlockEntity>> ENERGY_CONTROLLER =
            BLOCK_ENTITIES.register("energy_controller", () ->
                    BlockEntityType.Builder.of(EnergyControllerBlockEntity::new,
                                    HybridBlocks.ENERGY_CONTROLLER.get())
                            .build(null));

    public static final Supplier<BlockEntityType<EnergyMonitorBlockEntity>> ENERGY_MONITOR =
            BLOCK_ENTITIES.register("energy_monitor", () ->
                    BlockEntityType.Builder.of(EnergyMonitorBlockEntity::new,
                                    HybridBlocks.ENERGY_MONITOR.get())
                            .build(null));

    public static final Supplier<BlockEntityType<EnergyConverterBlockEntity>> ENERGY_CONVERTER =
            BLOCK_ENTITIES.register("energy_converter", () ->
                    BlockEntityType.Builder.of(EnergyConverterBlockEntity::new,
                                    HybridBlocks.ENERGY_CONVERTER.get())
                            .build(null));

    // Network Infrastructure Block Entities
    public static final Supplier<BlockEntityType<NetworkRelayBlockEntity>> NETWORK_RELAY =
            BLOCK_ENTITIES.register("network_relay", () ->
                    BlockEntityType.Builder.of(NetworkRelayBlockEntity::new,
                                    HybridBlocks.NETWORK_RELAY.get())
                            .build(null));

    public static final Supplier<BlockEntityType<NetworkAmplifierBlockEntity>> NETWORK_AMPLIFIER =
            BLOCK_ENTITIES.register("network_amplifier", () ->
                    BlockEntityType.Builder.of(NetworkAmplifierBlockEntity::new,
                                    HybridBlocks.NETWORK_AMPLIFIER.get())
                            .build(null));

    public static final Supplier<BlockEntityType<NetworkBridgeBlockEntity>> NETWORK_BRIDGE =
            BLOCK_ENTITIES.register("network_bridge", () ->
                    BlockEntityType.Builder.of(NetworkBridgeBlockEntity::new,
                                    HybridBlocks.NETWORK_BRIDGE.get())
                            .build(null));

    // Specialized Interface Block Entities
    public static final Supplier<BlockEntityType<EnergyAnalyzerBlockEntity>> ENERGY_ANALYZER =
            BLOCK_ENTITIES.register("energy_analyzer", () ->
                    BlockEntityType.Builder.of(EnergyAnalyzerBlockEntity::new,
                                    HybridBlocks.ENERGY_ANALYZER.get())
                            .build(null));

    public static final Supplier<BlockEntityType<NetworkDashboardBlockEntity>> NETWORK_DASHBOARD =
            BLOCK_ENTITIES.register("network_dashboard", () ->
                    BlockEntityType.Builder.of(NetworkDashboardBlockEntity::new,
                                    HybridBlocks.NETWORK_DASHBOARD.get())
                            .build(null));

    public static final Supplier<BlockEntityType<HybridConfiguratorBlockEntity>> HYBRID_CONFIGURATOR =
            BLOCK_ENTITIES.register("hybrid_configurator", () ->
                    BlockEntityType.Builder.of(HybridConfiguratorBlockEntity::new,
                                    HybridBlocks.HYBRID_CONFIGURATOR.get())
                            .build(null));

    /**
     * Registers all hybrid block entities
     * Enhanced by Claude for Minecraft 1.21 + Neoforge 21.0.167 compatibility
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        MinecraftFarmerTechMod.LOGGER.info("Hybrid Block Entities registered successfully!");
    }

    /**
     * Helper class for block entity specifications
     * Based on TWheatKing's original framework patterns
     */
    public static class BlockEntitySpecifications {

        /**
         * Gets all energy storage block entity types
         */
        public static BlockEntityType<?>[] getEnergyStorageBlockEntities() {
            return new BlockEntityType<?>[] {
                    BASIC_ENERGY_STORAGE.get(),
                    ENHANCED_ENERGY_STORAGE.get(),
                    ADVANCED_ENERGY_STORAGE.get(),
                    SUPERIOR_ENERGY_STORAGE.get(),
                    QUANTUM_ENERGY_STORAGE.get()
            };
        }

        /**
         * Gets all cable transmission block entity types
         */
        public static BlockEntityType<?>[] getCableBlockEntities() {
            return new BlockEntityType<?>[] {
                    COPPER_CABLE.get(),
                    COPPER_CABLE_INSULATED.get(),
                    GOLD_CABLE.get(),
                    GOLD_CABLE_INSULATED.get(),
                    DIAMOND_CABLE.get()
            };
        }

        /**
         * Gets all machine block entity types
         */
        public static BlockEntityType<?>[] getMachineBlockEntities() {
            return new BlockEntityType<?>[] {
                    HYBRID_COAL_GENERATOR.get(),
                    ENERGY_CONTROLLER.get(),
                    ENERGY_MONITOR.get(),
                    ENERGY_CONVERTER.get()
            };
        }

        /**
         * Gets all network infrastructure block entity types
         */
        public static BlockEntityType<?>[] getNetworkBlockEntities() {
            return new BlockEntityType<?>[] {
                    NETWORK_RELAY.get(),
                    NETWORK_AMPLIFIER.get(),
                    NETWORK_BRIDGE.get()
            };
        }

        /**
         * Gets all specialized interface block entity types
         */
        public static BlockEntityType<?>[] getSpecializedBlockEntities() {
            return new BlockEntityType<?>[] {
                    ENERGY_ANALYZER.get(),
                    NETWORK_DASHBOARD.get(),
                    HYBRID_CONFIGURATOR.get()
            };
        }

        /**
         * Gets all hybrid block entity types
         */
        public static BlockEntityType<?>[] getAllHybridBlockEntities() {
            java.util.List<BlockEntityType<?>> allBlockEntities = new java.util.ArrayList<>();
            allBlockEntities.addAll(java.util.Arrays.asList(getEnergyStorageBlockEntities()));
            allBlockEntities.addAll(java.util.Arrays.asList(getCableBlockEntities()));
            allBlockEntities.addAll(java.util.Arrays.asList(getMachineBlockEntities()));
            allBlockEntities.addAll(java.util.Arrays.asList(getNetworkBlockEntities()));
            allBlockEntities.addAll(java.util.Arrays.asList(getSpecializedBlockEntities()));
            return allBlockEntities.toArray(new BlockEntityType<?>[0]);
        }
    }

    // **PLACEHOLDER BLOCK ENTITY CLASSES**
    // These need to be implemented in their respective packages:

    /**
     * NOTE: These block entity classes need to be created.
     * Based on TWheatKing's pattern, they should extend BaseMachineBlockEntity or EnergyStorageBlockEntity
     */

    // Control System Block Entities (need to be created)

    // Network Infrastructure Block Entities (need to be created)

    // Specialized Interface Block Entities (need to be created)
}