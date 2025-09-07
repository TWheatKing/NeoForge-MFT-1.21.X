package com.thewheatking.minecraftfarmertechmod.util;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageSources {
    public static final ResourceKey<DamageType> BIO_FUEL_DROWNING = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "bio_fuel_drowning")
    );

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(BIO_FUEL_DROWNING, new DamageType("bio_fuel_drowning", 0.1F));
    }
}