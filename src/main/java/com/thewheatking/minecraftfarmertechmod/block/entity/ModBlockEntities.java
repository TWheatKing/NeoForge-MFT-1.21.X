package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE =
            BLOCK_ENTITIES.register("iron_furnace", () ->
                    BlockEntityType.Builder.of(IronFurnaceBlockEntity::new,
                            ModBlocks.IRON_FURNACE.get()).build(null));
    // Energy System Block Entities
    public static final Supplier<BlockEntityType<EnergyCableBlockEntity>> ENERGY_CABLE =
            BLOCK_ENTITIES.register("energy_cable", () ->
                    BlockEntityType.Builder.of(EnergyCableBlockEntity::new,
                            ModBlocks.ENERGY_CABLE.get()).build(null));

    public static final Supplier<BlockEntityType<CoalGeneratorBlockEntity>> COAL_GENERATOR =
            BLOCK_ENTITIES.register("coal_generator", () ->
                    BlockEntityType.Builder.of(CoalGeneratorBlockEntity::new,
                            ModBlocks.COAL_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<EnergyBatteryBlockEntity>> ENERGY_BATTERY =
            BLOCK_ENTITIES.register("energy_battery", () ->
                    BlockEntityType.Builder.of(EnergyBatteryBlockEntity::new,
                            ModBlocks.ENERGY_BATTERY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquifierBlockEntity>> LIQUIFIER =
            BLOCK_ENTITIES.register("liquifier", () ->
                    BlockEntityType.Builder.of(LiquifierBlockEntity::new,
                            ModBlocks.LIQUIFIER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioGeneratorBlockEntity>> BIO_GENERATOR =
            BLOCK_ENTITIES.register("bio_generator", () ->
                    BlockEntityType.Builder.of(BioGeneratorBlockEntity::new,
                            ModBlocks.BIO_GENERATOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}