package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<MenuType<IronFurnaceMenu>> IRON_FURNACE_MENU =
            MENUS.register("iron_furnace_menu", () ->
                    IMenuTypeExtension.create((containerId, inventory, data) ->
                            new IronFurnaceMenu(containerId, inventory, data)));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}