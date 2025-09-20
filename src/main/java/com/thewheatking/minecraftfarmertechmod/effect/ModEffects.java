package com.thewheatking.minecraftfarmertechmod.effect;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MinecraftFarmerTechMod.MOD_ID);

    public static final Holder<MobEffect> ATTRACTION_EFFECT = MOB_EFFECTS.register("attraction",
            () -> new AttractionEffect(MobEffectCategory.NEUTRAL, 0x90EE90)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "attraction"), -0.25f,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));


    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}