package com.thewheatking.minecraftfarmertechmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class AttractionEffect extends MobEffect {
    private static final double ATTRACTION_RADIUS = 16.0; // Configurable radius
    private static final double FOLLOW_SPEED = 0.6; // Reduced speed for more natural movement

    public AttractionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!(livingEntity instanceof Player player)) {
            return true;
        }

        Level level = player.level();
        if (level.isClientSide()) {
            return true; // Only run on server side
        }

        // Calculate attraction radius (increases with amplifier)
        double radius = ATTRACTION_RADIUS + (amplifier * 4.0);

        // Create bounding box around player
        AABB searchArea = new AABB(
                player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );

        // Find all animals in the area
        List<Animal> nearbyAnimals = level.getEntitiesOfClass(Animal.class, searchArea);

        for (Animal animal : nearbyAnimals) {
            if (animal.isAlive() && !animal.isLeashed()) {
                // Prevent fall damage while following
                animal.fallDistance = 0.0f;
                attractAnimal(animal, player, amplifier);
            }
        }

        return true;
    }

    private void attractAnimal(Animal animal, Player player, int amplifier) {
        double distance = animal.distanceTo(player);

        // Make animal look at player regardless of distance (for interest)
        animal.getLookControl().setLookAt(player, 30.0F, 30.0F);

        // Only attract if not too close (prevents crowding)
        if (distance > 2.0 && distance <= ATTRACTION_RADIUS + (amplifier * 4.0)) {
            // Calculate direction vector from animal to player
            double dx = player.getX() - animal.getX();
            double dy = player.getY() - animal.getY();
            double dz = player.getZ() - animal.getZ();

            // Normalize and apply speed
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length > 0) {
                double speed = FOLLOW_SPEED + (amplifier * 0.15); // Reduced amplifier bonus
                dx = (dx / length) * speed * 0.08; // Reduced movement scaling
                dz = (dz / length) * speed * 0.08;

                // Enhanced vertical movement for jumping over blocks
                if (player.getY() > animal.getY() + 0.5) {
                    // Player is higher, help animal jump
                    dy = Math.max(0.12, (dy / length) * speed * 0.08); // Reduced jump force
                } else {
                    // Normal vertical movement
                    dy = (dy / length) * speed * 0.04; // Reduced vertical movement
                }

                // Check if animal is stuck against a block and needs to jump
                if (animal.horizontalCollision && animal.onGround()) {
                    dy = Math.max(dy, 0.15); // Reduced forced jump when stuck
                }

                // Apply movement
                animal.setDeltaMovement(animal.getDeltaMovement().add(dx, dy, dz));
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Apply effect every tick for smooth movement
        return true;
    }
}