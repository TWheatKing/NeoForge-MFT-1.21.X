package com.thewheatking.minecraftfarmertechmod.item;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<CreativeModeTab> MINECRAFT_FARMER_TECH_TAB = CREATIVE_MODE_TAB.register("minecraft_farmer_tech_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHEAT_INGOT.get()))
                    .title(Component.translatable("creativetab.minecraftfarmertechmod.minecraft_farmer_tech"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //food related ingot
                        output.accept(ModItems.WHEAT_INGOT);
                        //machine related items
                        output.accept(ModItems.ZINC_INGOT);
                        output.accept(ModItems.RAW_ZINC);
                        output.accept(ModItems.ZINC_ALLOY);
                        output.accept(ModItems.BRASS);
                        output.accept(ModItems.ANDESITE_ALLOY);
                        //bits
                        output.accept(ModItems.IRON_BIT);
                        output.accept(ModItems.GOLD_BIT);
                        output.accept(ModItems.COPPER_BIT);
                        output.accept(ModItems.DIAMOND_BIT);
                        output.accept(ModItems.NETHERITE_BIT);
                        //cbs
                        output.accept(ModItems.BASIC_CIRCUIT_BOARD);
                        output.accept(ModItems.ADVANCED_CIRCUIT_BOARD);
                        output.accept(ModItems.FUSION_CIRCUIT_BOARD);
                        //machine parts
                        output.accept(ModItems.BASIC_BLADE);
                        output.accept(ModItems.ADVANCED_BLADE);
                        output.accept(ModItems.GRATE);
                        output.accept(ModItems.WISK);
                        output.accept(ModItems.RAM);
                        //plates
                        output.accept(ModItems.IRON_PLATE);
                        output.accept(ModItems.GOLD_PLATE);
                        output.accept(ModItems.BRASS_PLATE);
                        output.accept(ModItems.DIAMOND_PLATE);
                        output.accept(ModItems.NETHERITE_PLATE);
                        //blocks
                        output.accept(ModBlocks.ZINC_ORE);
                        output.accept(ModBlocks.ZINC_DEEPSLATE_ORE);
                        //custom items
                        output.accept(ModItems.WRENCH);
                        output.accept(ModItems.CHISEL);

                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
