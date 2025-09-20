package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    protected ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(ModItems.CORN.getId(), new Compostable(0.25F),false)
                .add(ModItems.CORN_SEEDS.getId(), new Compostable(0.45F),false);
        //this.builder(NeoForgeDataMaps.FURNACE_FUELS)
        //        .add(ModItems.STARLIGHT_ASHES.getId(), new FurnaceFuel(1200), false)
        //        .add(ModItems.FROSTFIRE_ICE.getId(), new FurnaceFuel(2400), false);
    }
}