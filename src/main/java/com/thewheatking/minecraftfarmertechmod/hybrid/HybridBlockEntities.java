package com.thewheatking.minecraftfarmertechmod.hybrid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.storage.*;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registration for hybrid energy system block entities
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
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

    public static final Supplier<BlockEntityType<DiamondCableInsulatedBlockEntity>> DIAMOND_CABLE_INSULATED =
            BLOCK_ENTITIES.register("diamond_cable_insulated", () ->
                    BlockEntityType.Builder.of(DiamondCableInsulatedBlockEntity::new,
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

    /**
     * Registers all hybrid block entities
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    /**
     * Placeholder block entity classes that need to be implemented
     * These are referenced above but need to be created in the appropriate packages
     */

    // Control System Block Entities (need to be created)
    public static class EnergyControllerBlockEntity extends com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity
            implements com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyCapabilityProviders.IHybridEnergyBlockEntity {

        public EnergyControllerBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            super(ENERGY_CONTROLLER.get(), pos, state,
                    HybridBlocks.MachineSpecifications.CONTROLLER_CAPACITY,
                    HybridBlocks.MachineSpecifications.CONTROLLER_TRANSFER,
                    HybridBlocks.MachineSpecifications.CONTROLLER_TRANSFER);
        }

        @Override
        protected void serverTick() {
            super.serverTick();
            // Controller-specific logic here
            // Manages energy distribution across the network
            // Provides centralized control for hybrid energy systems
        }

        @Override
        public com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage getHybridEnergyStorage() {
            return (com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage) this.energyStorage;
        }
    }

    public static class EnergyMonitorBlockEntity extends com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity
            implements com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyCapabilityProviders.IHybridEnergyBlockEntity {

        public EnergyMonitorBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            super(ENERGY_MONITOR.get(), pos, state,
                    HybridBlocks.MachineSpecifications.MONITOR_CAPACITY,
                    HybridBlocks.MachineSpecifications.MONITOR_TRANSFER,
                    HybridBlocks.MachineSpecifications.MONITOR_TRANSFER);
        }

        @Override
        protected void serverTick() {
            super.serverTick();
            // Monitor-specific logic here
            // Tracks energy flow and network statistics
            // Provides diagnostic information for hybrid systems
        }

        @Override
        public com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage getHybridEnergyStorage() {
            return (com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage) this.energyStorage;
        }
    }

    public static class EnergyConverterBlockEntity extends com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity
            implements com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyCapabilityProviders.IHybridEnergyBlockEntity {

        public EnergyConverterBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            super(ENERGY_CONVERTER.get(), pos, state,
                    HybridBlocks.MachineSpecifications.CONVERTER_CAPACITY,
                    HybridBlocks.MachineSpecifications.CONVERTER_TRANSFER,
                    HybridBlocks.MachineSpecifications.CONVERTER_TRANSFER);
        }

        @Override
        protected void serverTick() {
            super.serverTick();
            // Converter-specific logic here
            // Converts between FE and MFT energy types
            // Handles efficiency calculations and conversion rates
        }

        @Override
        public com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage getHybridEnergyStorage() {
            return (com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage) this.energyStorage;
        }
    }

    // Network Infrastructure Block Entities (need to be created)
    public static class NetworkRelayBlockEntity extends EnergyTransmissionBlockEntity {

        public NetworkRelayBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            super(NETWORK_RELAY.get(), pos, state,
                    HybridBlocks.MachineSpecifications.RELAY_TRANSFER, 0.005);
        }

        @Override
        protected void serverTick() {
            super.serverTick();
            // Relay-specific logic here
            // Extends network range and boosts signal strength
        }
    }

    public static class NetworkAmplifierBlockEntity extends EnergyTransmissionBlockEntity {

        public NetworkAmplifierBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            super(NETWORK_AMPLIFIER.get(), pos, state,
                    HybridBlocks.MachineSpecifications.AMPLIFIER_TRANSFER, 0.003);
        }

        @Override
        protected void serverTick() {
            super.serverTick();
            // Amplifier-specific logic here
            // Amplifies energy signals and reduces loss
        }
    }

    public static class NetworkBridgeBlockEntity extends EnergyTransmissionBlockEntity {

        public NetworkBridgeBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            super(NETWORK_BRIDGE.get(), pos, state,
                    HybridBlocks.MachineSpecifications.BRIDGE_TRANSFER, 0.001);
        }

        @Override
        protected void serverTick() {
            super.serverTick();
            // Bridge-specific logic here
            // Connects separate networks and enables long-distance transmission
        }
    }

    /**
     * Helper methods for getting block entity specifications
     */
    public static class BlockEntitySpecifications {

        /**
         * Gets all energy storage block entity types
         */
        public static BlockEntityType<?>[] getEnergyStorageTypes() {
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
        public static BlockEntityType<?>[] getCableTypes() {
            return new BlockEntityType<?>[] {
                    COPPER_CABLE.get(),
                    COPPER_CABLE_INSULATED.get(),
                    GOLD_CABLE.get(),
                    GOLD_CABLE_INSULATED.get(),
                    DIAMOND_CABLE.get(),
                    DIAMOND_CABLE_INSULATED.get()
            };
        }

        /**
         * Gets all machine block entity types
         */
        public static BlockEntityType<?>[] getMachineTypes() {
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
        public static BlockEntityType<?>[] getNetworkTypes() {
            return new BlockEntityType<?>[] {
                    NETWORK_RELAY.get(),
                    NETWORK_AMPLIFIER.get(),
                    NETWORK_BRIDGE.get()
            };
        }

        /**
         * Gets all hybrid energy block entity types
         */
        public static BlockEntityType<?>[] getAllHybridTypes() {
            java.util.List<BlockEntityType<?>> allTypes = new java.util.ArrayList<>();
            allTypes.addAll(java.util.Arrays.asList(getEnergyStorageTypes()));
            allTypes.addAll(java.util.Arrays.asList(getCableTypes()));
            allTypes.addAll(java.util.Arrays.asList(getMachineTypes()));
            allTypes.addAll(java.util.Arrays.asList(getNetworkTypes()));
            return allTypes.toArray(new BlockEntityType<?>[0]);
        }
    }
}