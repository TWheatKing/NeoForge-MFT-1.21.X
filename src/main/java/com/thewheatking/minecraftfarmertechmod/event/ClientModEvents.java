package com.thewheatking.minecraftfarmertechmod.event;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import com.thewheatking.minecraftfarmertechmod.screen.CoalGeneratorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.IronFurnaceScreen;
import com.thewheatking.minecraftfarmertechmod.screen.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import com.thewheatking.minecraftfarmertechmod.screen.LiquifierScreen;
import com.thewheatking.minecraftfarmertechmod.screen.BioGeneratorScreen;
import com.thewheatking.minecraftfarmertechmod.screen.SideConfigScreen;

@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientModEvents {


    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == ModItems.ZINC_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
    }


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Client setup logic can go here if needed
        event.enqueueWork(() -> {
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.IRON_FURNACE_MENU.get(), IronFurnaceScreen::new);
        event.register(ModMenuTypes.COAL_GENERATOR_MENU.get(), CoalGeneratorScreen::new);
        event.register(ModMenuTypes.BIO_GENERATOR_MENU.get(), BioGeneratorScreen::new);
        event.register(ModMenuTypes.LIQUIFIER_MENU.get(), LiquifierScreen::new);
        event.register(ModMenuTypes.SIDE_CONFIG_MENU.get(), SideConfigScreen::new);
    }
}