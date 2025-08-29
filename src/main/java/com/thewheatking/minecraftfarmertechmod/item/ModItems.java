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

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);

    }
}
