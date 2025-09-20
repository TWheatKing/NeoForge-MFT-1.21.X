package com.thewheatking.minecraftfarmertechmod.util;

import com.thewheatking.minecraftfarmertechmod.enchantment.custom.BreedingParticlesEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public class BreedingEffectHelper {

    /**
     * Shows success particles (love hearts) around the given entity
     */
    public static void showSuccessParticles(Entity entity, int enchantmentLevel) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BreedingParticlesEffect.success().apply(
                serverLevel,
                enchantmentLevel,
                createDummyItemInUse(),
                entity,
                entity.position()
        );
    }

    /**
     * Shows failure particles (angry villager) around the given entity
     */
    public static void showFailureParticles(Entity entity, int enchantmentLevel) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BreedingParticlesEffect.failure().apply(
                serverLevel,
                enchantmentLevel,
                createDummyItemInUse(),
                entity,
                entity.position()
        );
    }

    private static EnchantedItemInUse createDummyItemInUse() {
        // Create a minimal dummy item for the effect
        return new EnchantedItemInUse(ItemStack.EMPTY, null, null);
    }
}