package com.thewheatking.minecraftfarmertechmod.item;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.fluid.ModFluids;
import com.thewheatking.minecraftfarmertechmod.item.custom.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
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
    //fan blades
    public static final DeferredItem<Item> BASIC_BLADE = ITEMS.register("basic_blade",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_BLADE = ITEMS.register("advanced_blade",
            () -> new Item(new Item.Properties()));
    //machine parts
    public static final DeferredItem<Item> GRATE = ITEMS.register("grate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WISK = ITEMS.register("wisk",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAM = ITEMS.register("ram",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EMPTY_COIL = ITEMS.register("empty_coil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_COIL = ITEMS.register("copper_coil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_COIL = ITEMS.register("gold_coil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_COIL = ITEMS.register("diamond_coil",
            () -> new Item(new Item.Properties()));
    //plates
    public static final DeferredItem<Item> IRON_PLATE = ITEMS.register("iron_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_PLATE = ITEMS.register("gold_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRASS_PLATE = ITEMS.register("brass_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_PLATE = ITEMS.register("diamond_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_PLATE = ITEMS.register("netherite_plate",
            () -> new Item(new Item.Properties()));

    //custom items
    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties()));
    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32)));
    public static final DeferredItem<Item> ZINC_ALLOY = ITEMS.register("zinc_alloy",
            () -> new ZincAlloyItem(new Item.Properties()));
    public static final DeferredItem<Item> ANDESITE_ALLOY = ITEMS.register("andesite_alloy",
            () -> new AndesiteAlloyItem(new Item.Properties()));
    public static final DeferredItem<Item> BIO_FUEL_BUCKET = ITEMS.register("bio_fuel_bucket",
            () -> new BucketItem(ModFluids.BIOFUEL.get(), new Item.Properties()
                    .craftRemainder(Items.BUCKET)
                    .stacksTo(1)));
    public static final DeferredHolder<Item, Item> BIO_FUEL = ITEMS.register("bio_fuel",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BURGER = ITEMS.register("burger",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BURGER)));

    public static final DeferredItem<SwordItem> ZINC_SWORD = ITEMS.register("bismuth_sword",
            () -> new SwordItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.ZINC, 5, -2.4f))));
    public static final DeferredItem<PickaxeItem> ZINC_PICKAXE = ITEMS.register("bismuth_pickaxe",
            () -> new PickaxeItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.ZINC, 1.0F, -2.8f))));
    public static final DeferredItem<ShovelItem> ZINC_SHOVEL = ITEMS.register("bismuth_shovel",
            () -> new ShovelItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.ZINC, 1.5F, -3.0f))));
    public static final DeferredItem<AxeItem> ZINC_AXE = ITEMS.register("bismuth_axe",
            () -> new AxeItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.ZINC, 6.0F, -3.2f))));
    public static final DeferredItem<HoeItem> ZINC_HOE = ITEMS.register("bismuth_hoe",
            () -> new HoeItem(ModToolTiers.ZINC, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.ZINC, 0F, -3.0f))));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);

    }
}
