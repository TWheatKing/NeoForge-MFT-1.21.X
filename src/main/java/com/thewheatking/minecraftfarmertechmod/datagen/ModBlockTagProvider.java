package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MinecraftFarmerTechMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ZINC_ORE.get())
                .add(ModBlocks.ZINC_DEEPSLATE_ORE.get())
                .add(ModBlocks.ZINC_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.ZINC_DEEPSLATE_ORE.get());

        tag(BlockTags.FENCES).add(ModBlocks.ZINC_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(ModBlocks.ZINC_FENCE_GATE.get());
        tag(BlockTags.WALLS).add(ModBlocks.ZINC_WALL.get());

        tag(ModTags.Blocks.NEEDS_ZINC_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL);

        tag(ModTags.Blocks.INCORRECT_FOR_ZINC_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_IRON_TOOL)
                .remove(ModTags.Blocks.NEEDS_ZINC_TOOL);

    }
}
