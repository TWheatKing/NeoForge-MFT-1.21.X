package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE =
            BLOCK_ENTITIES.register("iron_furnace", () ->
                    BlockEntityType.Builder.of(IronFurnaceBlockEntity::new,
                            ModBlocks.IRON_FURNACE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}