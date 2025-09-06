package com.thewheatking.minecraftfarmertechmod.fluid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, MinecraftFarmerTechMod.MOD_ID);

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MinecraftFarmerTechMod.MOD_ID);

    // Register the fluid type first
    public static final DeferredHolder<FluidType, FluidType> BIO_FUEL_TYPE = FLUID_TYPES.register("biofuel",
            () -> new BioFuelType());

    // Register the source fluid
    public static final DeferredHolder<Fluid, FlowingFluid> BIOFUEL = FLUIDS.register("biofuel",
            () -> new BioFuel.Source());

    // Register the flowing fluid
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_BIOFUEL = FLUIDS.register("flowing_biofuel",
            () -> new BioFuel.Flowing());

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
        FLUID_TYPES.register(eventBus);
    }
}