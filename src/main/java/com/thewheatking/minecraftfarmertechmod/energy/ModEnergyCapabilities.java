package com.thewheatking.minecraftfarmertechmod.energy;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * Defines the energy capabilities for blocks and items in the mod.
 * This follows the NeoForge capability system for 1.21.X
 */
public class ModEnergyCapabilities {

    /**
     * Block capability for energy storage and transfer
     */
    public static final BlockCapability<IEnergyStorage, net.minecraft.core.Direction> ENERGY =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "energy"),
                    IEnergyStorage.class);

    /**
     * Item capability for energy storage in items (like energy cells, tools, etc.)
     */
    public static final ItemCapability<IEnergyStorage, Void> ENERGY_ITEM =
            ItemCapability.createVoid(
                    ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "energy"),
                    IEnergyStorage.class);

    public static void register(IEventBus modEventBus) {
        // Capabilities are automatically registered when created
        // But we can use this method for any additional setup if needed
    }
}