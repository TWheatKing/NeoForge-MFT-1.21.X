package com.thewheatking.minecraftfarmertechmod.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EnhancedModItemModelProvider extends ItemModelProvider {
    public EnhancedModItemModelProvider(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // TODO: Add enhanced/hybrid system item models here
        // This would include models for hybrid energy items, tools, etc.
    }
}