package com.thewheatking.minecraftfarmertechmod.enchantment.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record BreedingParticlesEffect(boolean isSuccess) implements EnchantmentEntityEffect {
    public static final MapCodec<BreedingParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    com.mojang.serialization.Codec.BOOL.fieldOf("success").forGetter(BreedingParticlesEffect::isSuccess)
            ).apply(instance, BreedingParticlesEffect::new)
    );

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        if (isSuccess) {
            // Show love hearts for successful extra baby
            serverLevel.sendParticles(
                    ParticleTypes.HEART,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    8, // count
                    0.5, 0.5, 0.5, // spread
                    0.1 // speed
            );
        } else {
            // Show angry particles for no extra baby
            serverLevel.sendParticles(
                    ParticleTypes.ANGRY_VILLAGER,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    4, // count
                    0.3, 0.3, 0.3, // spread
                    0.05 // speed
            );
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    // Static factory methods for easy use
    public static BreedingParticlesEffect success() {
        return new BreedingParticlesEffect(true);
    }

    public static BreedingParticlesEffect failure() {
        return new BreedingParticlesEffect(false);
    }
}