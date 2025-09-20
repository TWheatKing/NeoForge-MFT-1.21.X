package com.thewheatking.minecraftfarmertechmod.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record BreedingEnchantmentEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<BreedingEnchantmentEffect> CODEC = MapCodec.unit(BreedingEnchantmentEffect::new);

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        // The actual breeding logic is handled in BreedingEventHandler
        // This effect just needs to exist so the enchantment can be detected
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}