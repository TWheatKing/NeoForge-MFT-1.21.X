package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    // Feature flag for hybrid system
    private static final boolean ENABLE_HYBRID_SYSTEM = false;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Server-side data generation
        if (event.includeServer()) {
            // Loot Tables (keep your existing + add hybrid)
            generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                    List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

            // Recipes - choose between original and hybrid
            if (ENABLE_HYBRID_SYSTEM) {
                generator.addProvider(true, new HybridRecipeProvider(packOutput, lookupProvider));
            } else {
                generator.addProvider(true, new ModRecipeProvider(packOutput, lookupProvider));
            }

            // Block Tags - choose between original and hybrid
            BlockTagsProvider blockTagsProvider;
            if (ENABLE_HYBRID_SYSTEM) {
                blockTagsProvider = new HybridBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
            } else {
                blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
            }
            generator.addProvider(true, blockTagsProvider);

            // Item Tags (will build on the block tags)
            generator.addProvider(true, new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));

            // Data Maps (keep existing)
            generator.addProvider(true, new ModDataMapProvider(packOutput, lookupProvider));
        }

        // Client-side data generation
        if (event.includeClient()) {
            // Item Models - choose between original and hybrid
            if (ENABLE_HYBRID_SYSTEM) {
                generator.addProvider(true, new EnhancedModItemModelProvider(packOutput, MinecraftFarmerTechMod.MOD_ID, existingFileHelper));
            } else {
                generator.addProvider(true, new ModItemModelProvider(packOutput, MinecraftFarmerTechMod.MOD_ID, existingFileHelper));
            }

            // Block States - choose between original and hybrid
            if (ENABLE_HYBRID_SYSTEM) {
                generator.addProvider(true, new EnhancedModBlockStateProvider(packOutput, MinecraftFarmerTechMod.MOD_ID, existingFileHelper));
            } else {
                generator.addProvider(true, new ModBlockStateProvider(packOutput, MinecraftFarmerTechMod.MOD_ID, existingFileHelper));
            }
        }

        // Datapack content (keep existing)
        generator.addProvider(event.includeClient(), new ModDatapackProvider(packOutput, lookupProvider));

        // Log which system is being used
        MinecraftFarmerTechMod.LOGGER.info("Data Generation using " + (ENABLE_HYBRID_SYSTEM ? "Hybrid" : "Original") + " Energy System");
    }
}