package com.thewheatking.minecraftfarmertechmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ModFoodProperties {
    public static final FoodProperties BURGER = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 400), 0.3f).build();

    public static final FoodProperties STRAWBERRY = new FoodProperties.Builder().nutrition(2)
            .saturationModifier(0.15f).fast().build();

    public static final FoodProperties CORN = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.3f)
            //removed to test if error goes bye bye.usingConvertsTo(new ItemStack(ModItems.CORN_SEEDS.get()).getItem()) // Use ItemStack instead
            .build();
}
