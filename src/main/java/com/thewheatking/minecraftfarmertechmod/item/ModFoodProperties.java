package com.thewheatking.minecraftfarmertechmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties BURGER = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 400), 0.3f).build();
}
