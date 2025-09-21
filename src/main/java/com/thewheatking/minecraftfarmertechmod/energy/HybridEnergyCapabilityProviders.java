package com.thewheatking.minecraftfarmertechmod.energy;

import com.thewheatking.minecraftfarmertechmod.common.capabilities.energy.AdaptiveEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FIXED: Capability providers for the hybrid energy system
 * Based on TWheatKing's original MFT framework, enhanced for hybrid system support
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/energy/HybridEnergyCapabilityProviders.java
 * Purpose: Manages energy capability registration and provides seamless integration between FE and MFT systems
 */
public class HybridEnergyCapabilityProviders {

    // Cache for capability providers to improve performance
    private static final Map<Class<? extends BlockEntity>, ICapabilityProvider<BlockEntity, Direction, IEnergyStorage>>
            PROVIDER_CACHE = new ConcurrentHashMap<>();

    // Custom MFT energy capability (if MFT system defines one)
    public static final BlockCapability<IEnergyStorage, Direction> MFT_ENERGY_CAPABILITY =
            BlockCapability.createSided(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraftfarmertechmod", "mft_energy"),
                    IEnergyStorage.class
            );

    /**
     * Interface for hybrid energy capable block entities
     */
    public interface IHybridEnergyBlockEntity {
        /**
         * Gets the hybrid energy storage for this block entity
         */
        HybridEnergyStorage getHybridEnergyStorage();

        /**
         * Gets the adaptive energy storage for this block entity
         */
        default AdaptiveEnergyStorage getAdaptiveEnergyStorage() {
            return getHybridEnergyStorage();
        }

        /**
         * Checks if this block entity supports MFT energy
         */
        default boolean supportsMftEnergy() {
            return getHybridEnergyStorage() != null;
        }

        /**
         * Checks if this block entity supports Forge Energy
         */
        default boolean supportsForgeEnergy() {
            return getHybridEnergyStorage() != null;
        }

        /**
         * Gets the energy storage for a specific side
         */
        default IEnergyStorage getEnergyStorage(@Nullable Direction side) {
            return getHybridEnergyStorage();
        }
    }

    /**
     * Wrapper class for providing different energy interfaces
     */
    public static class HybridEnergyWrapper implements IEnergyStorage {
        private final HybridEnergyStorage hybridStorage;
        private final AdaptiveEnergyStorage.EnergyMode preferredMode;

        public HybridEnergyWrapper(HybridEnergyStorage hybridStorage, AdaptiveEnergyStorage.EnergyMode preferredMode) {
            this.hybridStorage = hybridStorage;
            this.preferredMode = preferredMode;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            // Temporarily switch mode if needed
            AdaptiveEnergyStorage.EnergyMode originalMode = hybridStorage.getMode();
            if (preferredMode != originalMode) {
                hybridStorage.setMode(preferredMode);
            }

            try {
                return hybridStorage.receiveEnergy(maxReceive, simulate);
            } finally {
                // Restore original mode
                if (preferredMode != originalMode) {
                    hybridStorage.setMode(originalMode);
                }
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            // Temporarily switch mode if needed
            AdaptiveEnergyStorage.EnergyMode originalMode = hybridStorage.getMode();
            if (preferredMode != originalMode) {
                hybridStorage.setMode(preferredMode);
            }

            try {
                return hybridStorage.extractEnergy(maxExtract, simulate);
            } finally {
                // Restore original mode
                if (preferredMode != originalMode) {
                    hybridStorage.setMode(originalMode);
                }
            }
        }

        @Override
        public int getEnergyStored() {
            return hybridStorage.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return hybridStorage.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return hybridStorage.canExtract();
        }

        @Override
        public boolean canReceive() {
            return hybridStorage.canReceive();
        }
    }

    /**
     * Creates a capability provider for hybrid energy block entities
     */
    public static <T extends BlockEntity & IHybridEnergyBlockEntity>
    ICapabilityProvider<BlockEntity, Direction, IEnergyStorage> createHybridProvider(Class<T> blockEntityClass) {

        return PROVIDER_CACHE.computeIfAbsent(blockEntityClass, clazz ->
                new ICapabilityProvider<BlockEntity, Direction, IEnergyStorage>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public @Nullable IEnergyStorage getCapability(BlockEntity blockEntity, @Nullable Direction context) {
                        if (clazz.isInstance(blockEntity)) {
                            T hybridEntity = (T) blockEntity;
                            HybridEnergyStorage storage = hybridEntity.getHybridEnergyStorage();
                            if (storage != null) {
                                return new HybridEnergyWrapper(storage, AdaptiveEnergyStorage.EnergyMode.HYBRID);
                            }
                        }
                        return null;
                    }
                });
    }

    /**
     * Creates a Forge Energy specific capability provider
     */
    public static <T extends BlockEntity & IHybridEnergyBlockEntity>
    ICapabilityProvider<BlockEntity, Direction, IEnergyStorage> createForgeEnergyProvider(Class<T> blockEntityClass) {

        return new ICapabilityProvider<BlockEntity, Direction, IEnergyStorage>() {
            @Override
            @SuppressWarnings("unchecked")
            public @Nullable IEnergyStorage getCapability(BlockEntity blockEntity, @Nullable Direction context) {
                if (blockEntityClass.isInstance(blockEntity)) {
                    T hybridEntity = (T) blockEntity;
                    if (hybridEntity.supportsForgeEnergy()) {
                        HybridEnergyStorage storage = hybridEntity.getHybridEnergyStorage();
                        if (storage != null) {
                            return new HybridEnergyWrapper(storage, AdaptiveEnergyStorage.EnergyMode.FORGE_ENERGY);
                        }
                    }
                }
                return null;
            }
        };
    }

    /**
     * Creates an MFT Energy specific capability provider
     */
    public static <T extends BlockEntity & IHybridEnergyBlockEntity>
    ICapabilityProvider<BlockEntity, Direction, IEnergyStorage> createMftEnergyProvider(Class<T> blockEntityClass) {

        return new ICapabilityProvider<BlockEntity, Direction, IEnergyStorage>() {
            @Override
            @SuppressWarnings("unchecked")
            public @Nullable IEnergyStorage getCapability(BlockEntity blockEntity, @Nullable Direction context) {
                if (blockEntityClass.isInstance(blockEntity)) {
                    T hybridEntity = (T) blockEntity;
                    if (hybridEntity.supportsMftEnergy()) {
                        HybridEnergyStorage storage = hybridEntity.getHybridEnergyStorage();
                        if (storage != null) {
                            return new HybridEnergyWrapper(storage, AdaptiveEnergyStorage.EnergyMode.MFT_ENERGY);
                        }
                    }
                }
                return null;
            }
        };
    }

    /**
     * Creates a sided capability provider that can handle different energy types per side
     */
    public static <T extends BlockEntity & IHybridEnergyBlockEntity>
    ICapabilityProvider<BlockEntity, Direction, IEnergyStorage> createSidedProvider(
            Class<T> blockEntityClass,
            Map<Direction, AdaptiveEnergyStorage.EnergyMode> sideModes) {

        return new ICapabilityProvider<BlockEntity, Direction, IEnergyStorage>() {
            @Override
            @SuppressWarnings("unchecked")
            public @Nullable IEnergyStorage getCapability(BlockEntity blockEntity, @Nullable Direction context) {
                if (blockEntityClass.isInstance(blockEntity)) {
                    T hybridEntity = (T) blockEntity;
                    HybridEnergyStorage storage = hybridEntity.getHybridEnergyStorage();

                    if (storage != null) {
                        AdaptiveEnergyStorage.EnergyMode mode = sideModes.getOrDefault(
                                context, AdaptiveEnergyStorage.EnergyMode.HYBRID);
                        return new HybridEnergyWrapper(storage, mode);
                    }
                }
                return null;
            }
        };
    }

    /**
     * Creates a smart capability provider that automatically chooses the best energy type
     */
    public static <T extends BlockEntity & IHybridEnergyBlockEntity>
    ICapabilityProvider<BlockEntity, Direction, IEnergyStorage> createSmartProvider(Class<T> blockEntityClass) {

        return new ICapabilityProvider<BlockEntity, Direction, IEnergyStorage>() {
            @Override
            @SuppressWarnings("unchecked")
            public @Nullable IEnergyStorage getCapability(BlockEntity blockEntity, @Nullable Direction context) {
                if (blockEntityClass.isInstance(blockEntity)) {
                    T hybridEntity = (T) blockEntity;
                    HybridEnergyStorage storage = hybridEntity.getHybridEnergyStorage();

                    if (storage != null) {
                        // Automatically choose the best mode based on current state
                        AdaptiveEnergyStorage.EnergyMode smartMode = determineOptimalMode(storage, context);
                        return new HybridEnergyWrapper(storage, smartMode);
                    }
                }
                return null;
            }
        };
    }

    /**
     * Determines the optimal energy mode based on storage state and context
     */
    private static AdaptiveEnergyStorage.EnergyMode determineOptimalMode(HybridEnergyStorage storage, @Nullable Direction context) {
        // Get current energy levels
        double fePercentage = (double) storage.getEnergyStored() / storage.getMaxEnergyStored();
        double mftPercentage = storage.getMftEnergyStored() / storage.getMaxMftEnergyStored();

        // Choose mode based on which system has more available capacity or energy
        if (storage.canExtract()) {
            // For extraction, use the system with more energy
            return fePercentage > mftPercentage ?
                    AdaptiveEnergyStorage.EnergyMode.FORGE_ENERGY :
                    AdaptiveEnergyStorage.EnergyMode.MFT_ENERGY;
        } else if (storage.canReceive()) {
            // For receiving, use the system with more capacity
            double feCapacity = 1.0 - fePercentage;
            double mftCapacity = 1.0 - mftPercentage;
            return feCapacity > mftCapacity ?
                    AdaptiveEnergyStorage.EnergyMode.FORGE_ENERGY :
                    AdaptiveEnergyStorage.EnergyMode.MFT_ENERGY;
        }

        // Default to hybrid mode
        return AdaptiveEnergyStorage.EnergyMode.HYBRID;
    }

    /**
     * FIXED: Register capabilities using the new Neoforge 1.21 API
     */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(HybridEnergyCapabilityProviders::registerCapabilities);
    }

    /**
     * FIXED: Event handler for capability registration
     */
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register capabilities for all hybrid block entities
        // This method will be called during mod initialization

        // Example registration - you'll need to add all your block entity types here
        // event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
        //     YourBlockEntities.BASIC_ENERGY_STORAGE.get(),
        //     createForgeEnergyProvider(BasicEnergyStorageBlockEntity.class));

        // Add more registrations here as needed
    }

    /**
     * Utility class for managing energy capability registration
     */
    public static class RegistrationHelper {

        /**
         * FIXED: Registers standard Forge Energy capability for a hybrid block entity
         */
        public static <T extends BlockEntity & IHybridEnergyBlockEntity>
        void registerForgeEnergyCapability(RegisterCapabilitiesEvent event,
                                           net.neoforged.neoforge.registries.DeferredHolder<?, ? extends T> blockEntityType,
                                           Class<T> blockEntityClass) {

            // Register for standard Forge Energy capability
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    blockEntityType.get(),
                    createForgeEnergyProvider(blockEntityClass));
        }

        /**
         * FIXED: Registers MFT Energy capability for a hybrid block entity
         */
        public static <T extends BlockEntity & IHybridEnergyBlockEntity>
        void registerMftEnergyCapability(RegisterCapabilitiesEvent event,
                                         net.neoforged.neoforge.registries.DeferredHolder<?, ? extends T> blockEntityType,
                                         Class<T> blockEntityClass) {

            // Register for MFT Energy capability
            event.registerBlockEntity(MFT_ENERGY_CAPABILITY,
                    blockEntityType.get(),
                    createMftEnergyProvider(blockEntityClass));
        }

        /**
         * FIXED: Registers both energy capabilities for a hybrid block entity
         */
        public static <T extends BlockEntity & IHybridEnergyBlockEntity>
        void registerHybridCapabilities(RegisterCapabilitiesEvent event,
                                        net.neoforged.neoforge.registries.DeferredHolder<?, ? extends T> blockEntityType,
                                        Class<T> blockEntityClass) {

            registerForgeEnergyCapability(event, blockEntityType, blockEntityClass);
            registerMftEnergyCapability(event, blockEntityType, blockEntityClass);
        }

        /**
         * FIXED: Registers smart capability that automatically chooses the best energy type
         */
        public static <T extends BlockEntity & IHybridEnergyBlockEntity>
        void registerSmartCapability(RegisterCapabilitiesEvent event,
                                     net.neoforged.neoforge.registries.DeferredHolder<?, ? extends T> blockEntityType,
                                     Class<T> blockEntityClass) {

            // Register smart provider for Forge Energy
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                    blockEntityType.get(),
                    createSmartProvider(blockEntityClass));

            // Register smart provider for MFT Energy
            event.registerBlockEntity(MFT_ENERGY_CAPABILITY,
                    blockEntityType.get(),
                    createSmartProvider(blockEntityClass));
        }

        /**
         * FIXED: Registers sided capability with different modes per side
         */
        public static <T extends BlockEntity & IHybridEnergyBlockEntity>
        void registerSidedCapability(RegisterCapabilitiesEvent event,
                                     net.neoforged.neoforge.registries.DeferredHolder<?, ? extends T> blockEntityType,
                                     Class<T> blockEntityClass,
                                     Map<Direction, AdaptiveEnergyStorage.EnergyMode> sideModes) {

            ICapabilityProvider<BlockEntity, Direction, IEnergyStorage> provider =
                    createSidedProvider(blockEntityClass, sideModes);

            // Register for both capability types
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, blockEntityType.get(), provider);
            event.registerBlockEntity(MFT_ENERGY_CAPABILITY, blockEntityType.get(), provider);
        }
    }

    /**
     * Utility methods for working with hybrid energy capabilities
     */
    public static class Utils {

        /**
         * Checks if a block entity supports hybrid energy
         */
        public static boolean isHybridEnergyBlockEntity(BlockEntity blockEntity) {
            return blockEntity instanceof IHybridEnergyBlockEntity;
        }

        /**
         * Gets hybrid energy storage from a block entity if available
         */
        public static @Nullable HybridEnergyStorage getHybridEnergyStorage(BlockEntity blockEntity) {
            if (blockEntity instanceof IHybridEnergyBlockEntity hybrid) {
                return hybrid.getHybridEnergyStorage();
            }
            return null;
        }

        /**
         * Gets energy storage with preferred mode from a block entity
         */
        public static @Nullable IEnergyStorage getEnergyStorage(BlockEntity blockEntity,
                                                                @Nullable Direction side,
                                                                AdaptiveEnergyStorage.EnergyMode preferredMode) {
            if (blockEntity instanceof IHybridEnergyBlockEntity hybrid) {
                HybridEnergyStorage storage = hybrid.getHybridEnergyStorage();
                if (storage != null) {
                    return new HybridEnergyWrapper(storage, preferredMode);
                }
            }
            return null;
        }

        /**
         * Transfers energy between two hybrid storages with optimal conversion
         */
        public static int transferEnergyOptimal(HybridEnergyStorage from, HybridEnergyStorage to, int maxTransfer) {
            // Determine the most efficient transfer method
            boolean useMft = shouldUseMftTransfer(from, to);

            if (useMft) {
                double mftTransfer = maxTransfer * HybridEnergyStorage.getFeToMftRatio();
                double extracted = from.extractMftEnergy(mftTransfer, false);
                double received = to.receiveMftEnergy(extracted, false);
                return (int) (received * HybridEnergyStorage.getMftToFeRatio());
            } else {
                int extracted = from.extractEnergy(maxTransfer, false);
                return to.receiveEnergy(extracted, false);
            }
        }

        private static boolean shouldUseMftTransfer(HybridEnergyStorage from, HybridEnergyStorage to) {
            // Use MFT if both storages prefer MFT or if it's more efficient
            HybridEnergyStorage.EnergyPriority fromPriority = from.getPriority();
            HybridEnergyStorage.EnergyPriority toPriority = to.getPriority();

            return fromPriority == HybridEnergyStorage.EnergyPriority.MFT_ENERGY_FIRST ||
                    toPriority == HybridEnergyStorage.EnergyPriority.MFT_ENERGY_FIRST ||
                    (fromPriority == HybridEnergyStorage.EnergyPriority.EFFICIENCY_OPTIMIZED &&
                            toPriority == HybridEnergyStorage.EnergyPriority.EFFICIENCY_OPTIMIZED);
        }
    }

    /**
     * Clears the capability provider cache
     */
    public static void clearCache() {
        PROVIDER_CACHE.clear();
    }

    /**
     * Gets the MFT energy capability
     */
    public static BlockCapability<IEnergyStorage, Direction> getMftEnergyCapability() {
        return MFT_ENERGY_CAPABILITY;
    }
}