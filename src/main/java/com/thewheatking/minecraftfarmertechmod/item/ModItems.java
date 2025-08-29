package com.thewheatking.minecraftfarmertechmod.item;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MinecraftFarmerTechMod.MOD_ID);

    public static final DeferredItem<Item> WHEAT_INGOT = ITEMS.register("wheat_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ZINC_INGOT = ITEMS.register("zinc_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_ZINC = ITEMS.register("raw_zinc",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRASS = ITEMS.register("brass",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ZINC_ALLOY = ITEMS.register("zinc_alloy",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ANDESITE_ALLOY = ITEMS.register("andesite_alloy",
            () -> new Item(new Item.Properties()));
    //Bits
    public static final DeferredItem<Item> IRON_BIT = ITEMS.register("iron_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_BIT = ITEMS.register("gold_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_BIT = ITEMS.register("copper_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_BIT = ITEMS.register("diamond_bit",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_BIT = ITEMS.register("netherite_bit",
            () -> new Item(new Item.Properties()));
    //CBS
    public static final DeferredItem<Item> BASIC_CIRCUIT_BOARD = ITEMS.register("basic_circuit_board",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_CIRCUIT_BOARD = ITEMS.register("advanced_circuit_board",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FUSION_CIRCUIT_BOARD = ITEMS.register("fusion_circuit_board",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);

    }
}
