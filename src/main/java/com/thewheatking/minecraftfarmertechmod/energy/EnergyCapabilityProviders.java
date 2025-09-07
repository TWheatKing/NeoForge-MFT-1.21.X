package com.thewheatking.minecraftfarmertechmod.energy;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.entity.CoalGeneratorBlockEntity;
import com.thewheatking.minecraftfarmertechmod.block.entity.EnergyCableBlockEntity;
import com.thewheatking.minecraftfarmertechmod.block.entity.EnergyBatteryBlockEntity;
import com.thewheatking.minecraftfarmertechmod.block.entity.LiquifierBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Registers energy capabilities for all blocks that can handle energy
 */
@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EnergyCapabilityProviders {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        // Register energy capability for Coal Generator (energy producer)
        event.registerBlockEntity(
                ModEnergyCapabilities.ENERGY,
                com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities.COAL_GENERATOR.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof CoalGeneratorBlockEntity generator) {
                        return generator.getEnergyStorage(direction);
                    }
                    return null;
                }
        );

        // Register energy capability for Energy Battery (energy storage)
        event.registerBlockEntity(
                ModEnergyCapabilities.ENERGY,
                com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities.ENERGY_BATTERY.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof EnergyBatteryBlockEntity battery) {
                        return battery.getEnergyStorage(direction);
                    }
                    return null;
                }
        );

        // Register energy capability for Energy Cable (energy transfer)
        event.registerBlockEntity(
                ModEnergyCapabilities.ENERGY,
                com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities.ENERGY_CABLE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof EnergyCableBlockEntity cable) {
                        return cable.getEnergyStorage(direction);
                    }
                    return null;
                }
        );

        // Register energy capability for Liquifier (energy consumer)
        event.registerBlockEntity(
                ModEnergyCapabilities.ENERGY,
                com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities.LIQUIFIER.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof LiquifierBlockEntity liquifier) {
                        return liquifier.getEnergyStorage(direction);
                    }
                    return null;
                }
        );

        // Register item capability for Liquifier (side-aware item handling)
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities.LIQUIFIER.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof LiquifierBlockEntity liquifier) {
                        return liquifier.getItemHandler(direction);
                    }
                    return null;
                }
        );
    }
}