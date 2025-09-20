package com.thewheatking.minecraftfarmertechmod.event;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.enchantment.ModEnchantments;
import com.thewheatking.minecraftfarmertechmod.util.BreedingEffectHelper;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;

@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID)
public class BreedingEventHandler {

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        MinecraftFarmerTechMod.LOGGER.info("BabyEntitySpawnEvent fired!");

        // Check if this is an animal breeding event caused by a player
        if (!(event.getParentA() instanceof Animal) || !(event.getParentB() instanceof Animal)) {
            MinecraftFarmerTechMod.LOGGER.info("Not an animal breeding event");
            return;
        }

        Animal parentA = (Animal) event.getParentA();
        Player breeder = null;

        // Find the player who caused the breeding
        if (parentA.getLoveCause() instanceof Player) {
            breeder = (Player) parentA.getLoveCause();
        } else if (((Animal) event.getParentB()).getLoveCause() instanceof Player) {
            breeder = (Player) ((Animal) event.getParentB()).getLoveCause();
        }

        if (breeder == null) {
            MinecraftFarmerTechMod.LOGGER.info("No player breeder found");
            return;
        }

        MinecraftFarmerTechMod.LOGGER.info("Found breeder: " + breeder.getName().getString());

        // Check for breeding enchantment on boots
        ItemStack boots = breeder.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
        MinecraftFarmerTechMod.LOGGER.info("Boots item: " + boots.getItem());

        // Use the proper enchantment registry lookup
        int breedingLevel = 0;
        var enchantmentRegistry = breeder.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        var breedingEnchantment = enchantmentRegistry.getHolder(ModEnchantments.BREEDING);

        if (breedingEnchantment.isPresent()) {
            breedingLevel = boots.getEnchantmentLevel(breedingEnchantment.get());
        }

        // Debug logging
        MinecraftFarmerTechMod.LOGGER.info("Checking for breeding enchantment on boots...");
        if (breedingLevel > 0) {
            MinecraftFarmerTechMod.LOGGER.info("Breeding enchantment detected! Level: " + breedingLevel);
        } else {
            MinecraftFarmerTechMod.LOGGER.info("No breeding enchantment found.");
            return; // Early return if no enchantment
        }

        if (breedingLevel > 0) {
            // Calculate chance for extra baby based on enchantment level
            double extraBabyChance = switch (breedingLevel) {
                case 1 -> 0.15; // 15% chance
                case 2 -> 0.25; // 25% chance
                case 3 -> 0.50; // 50% chance
                default -> 0.0;
            };

            MinecraftFarmerTechMod.LOGGER.info("Extra baby chance for level " + breedingLevel + ": " + (extraBabyChance * 100) + "%");

            // Roll for extra baby
            double roll = breeder.level().random.nextDouble();
            MinecraftFarmerTechMod.LOGGER.info("Random roll: " + roll + " (need less than " + extraBabyChance + ")");

            if (roll < extraBabyChance) {
                MinecraftFarmerTechMod.LOGGER.info("Spawning extra baby!");
                // Capture variables as final for lambda
                final BabyEntitySpawnEvent finalEvent = event;
                final Player finalBreeder = breeder;
                final int finalBreedingLevel = breedingLevel;

                // Schedule the baby spawn for next tick to ensure original baby is positioned
                breeder.level().getServer().execute(() -> {
                    spawnExtraBaby(finalEvent, finalBreeder);
                    BreedingEffectHelper.showSuccessParticles(finalEvent.getChild(), finalBreedingLevel);
                });
            } else {
                MinecraftFarmerTechMod.LOGGER.info("No extra baby this time.");
                // Show failure particles using the custom effect
                BreedingEffectHelper.showFailureParticles(event.getChild(), breedingLevel);
            }
        }
    }

    private static void spawnExtraBaby(BabyEntitySpawnEvent event, Player breeder) {
        AgeableMob baby = event.getChild();
        if (baby == null) {
            MinecraftFarmerTechMod.LOGGER.error("Failed to spawn extra baby: original baby is null");
            return;
        }

        // Use parent position instead of baby position for more reliable positioning
        Animal parentA = (Animal) event.getParentA();
        double spawnX = parentA.getX();
        double spawnY = parentA.getY();
        double spawnZ = parentA.getZ();

        MinecraftFarmerTechMod.LOGGER.info("Attempting to spawn extra baby of type: " + baby.getType());
        MinecraftFarmerTechMod.LOGGER.info("Using parent position: " + spawnX + ", " + spawnY + ", " + spawnZ);

        // Create a copy of the baby
        AgeableMob extraBaby = (AgeableMob) baby.getType().create(baby.level());
        if (extraBaby == null) {
            MinecraftFarmerTechMod.LOGGER.error("Failed to create extra baby entity");
            return;
        }

        // Set baby properties
        extraBaby.setBaby(true);

        // Spawn at parent location (where breeding happened)
        extraBaby.setPos(spawnX, spawnY, spawnZ);
        MinecraftFarmerTechMod.LOGGER.info("Extra baby positioned at parent location: " + spawnX + ", " + spawnY + ", " + spawnZ);

        // If it's an animal, set breeding properties
        if (extraBaby instanceof Animal extraAnimal) {
            // Set age to baby
            extraAnimal.setAge(-24000); // Standard baby age
            MinecraftFarmerTechMod.LOGGER.info("Set extra baby age to: " + extraAnimal.getAge());
        }

        // Spawn the extra baby
        boolean spawned = baby.level().addFreshEntity(extraBaby);
        MinecraftFarmerTechMod.LOGGER.info("Extra baby spawn result: " + spawned);

        if (spawned) {
            MinecraftFarmerTechMod.LOGGER.info("Extra baby successfully added to world with UUID: " + extraBaby.getUUID());
            MinecraftFarmerTechMod.LOGGER.info("Extra baby is alive: " + extraBaby.isAlive());
            MinecraftFarmerTechMod.LOGGER.info("Extra baby is baby: " + extraBaby.isBaby());
            MinecraftFarmerTechMod.LOGGER.info("Extra baby health: " + extraBaby.getHealth());

            // Force the baby to make a sound so you can hear where it is
            extraBaby.playSound(net.minecraft.sounds.SoundEvents.SHEEP_AMBIENT, 1.0F, 1.5F);
        } else {
            MinecraftFarmerTechMod.LOGGER.error("Failed to add extra baby to world!");
        }
    }
}