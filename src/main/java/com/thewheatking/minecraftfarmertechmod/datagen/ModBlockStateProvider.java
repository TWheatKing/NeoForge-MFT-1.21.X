package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MinecraftFarmerTechMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.ZINC_ORE);
        blockWithItem(ModBlocks.ZINC_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.ZINC_BLOCK);
        blockWithItem(ModBlocks.ZINC_CASING);
        blockWithItem(ModBlocks.ANDESITE_CASING);
        blockWithItem(ModBlocks.ENERGY_BATTERY);
        blockWithItem(ModBlocks.WHEAT_INGOT_BLOCK);

        stairsBlock(ModBlocks.ZINC_STAIRS.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        slabBlock(ModBlocks.ZINC_SLAB.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()), blockTexture(ModBlocks.ZINC_BLOCK.get()));

        buttonBlock(ModBlocks.ZINC_BUTTON.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        pressurePlateBlock(ModBlocks.ZINC_PRESSURE_PLATE.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        fenceGateBlock(ModBlocks.ZINC_FENCE_GATE.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        fenceBlock(ModBlocks.ZINC_FENCE.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        wallBlock(ModBlocks.ZINC_WALL.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));

        doorBlockWithRenderType(ModBlocks.ZINC_DOOR.get(), modLoc("block/zinc_door_bottom"), modLoc("block/zinc_door_top"), "cutout");
        trapdoorBlockWithRenderType(ModBlocks.ZINC_TRAPDOOR.get(), modLoc("block/zinc_trapdoor"), true, "cutout");

        blockItem(ModBlocks.ZINC_STAIRS);
        blockItem(ModBlocks.ZINC_SLAB);
        blockItem(ModBlocks.ZINC_PRESSURE_PLATE);
        blockItem(ModBlocks.ZINC_FENCE_GATE);
        blockItem(ModBlocks.ZINC_TRAPDOOR, "_bottom");


    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("tutorialmod:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("tutorialmod:block/" + deferredBlock.getId().getPath() + appendix));
    }
}
