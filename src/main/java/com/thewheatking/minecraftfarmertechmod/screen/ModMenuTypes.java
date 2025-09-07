package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<MenuType<IronFurnaceMenu>> IRON_FURNACE_MENU =
            MENUS.register("iron_furnace_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new IronFurnaceMenu(containerId, inventory, data)));

    public static final Supplier<MenuType<CoalGeneratorMenu>> COAL_GENERATOR_MENU =
            MENUS.register("coal_generator_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new CoalGeneratorMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<LiquifierMenu>> LIQUIFIER_MENU =
            MENUS.register("liquifier_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new LiquifierMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<BioGeneratorMenu>> BIO_GENERATOR_MENU =
            MENUS.register("bio_generator_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new BioGeneratorMenu(containerId, inventory, data)));

    public static final DeferredHolder<MenuType<?>, MenuType<SideConfigMenu>> SIDE_CONFIG_MENU =
            MENUS.register("side_config_menu", () -> IMenuTypeExtension.create(SideConfigMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}