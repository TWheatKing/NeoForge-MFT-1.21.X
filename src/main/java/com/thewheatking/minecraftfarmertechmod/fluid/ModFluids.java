package com.thewheatking.minecraftfarmertechmod.fluid;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
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

    // Use BaseFlowingFluid instead of custom BioFuel class for proper behavior
    public static final DeferredHolder<Fluid, FlowingFluid> BIOFUEL = FLUIDS.register("biofuel",
            () -> new BaseFlowingFluid.Source(ModFluids.PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_BIOFUEL = FLUIDS.register("flowing_biofuel",
            () -> new BaseFlowingFluid.Flowing(ModFluids.PROPERTIES));

    // Define properties that reference the fluids
    public static final BaseFlowingFluid.Properties PROPERTIES = new BaseFlowingFluid.Properties(
            BIO_FUEL_TYPE,
            BIOFUEL,
            FLOWING_BIOFUEL)
            .slopeFindDistance(4)  // How far it spreads
            .levelDecreasePerBlock(1)  // How much level decreases per block
            .block(ModBlocks.BIO_FUEL_BLOCK)  // The fluid block
            .bucket(ModItems.BIO_FUEL_BUCKET)  // The bucket item
            .tickRate(5);  // Update frequency

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
        FLUID_TYPES.register(eventBus);
    }
}