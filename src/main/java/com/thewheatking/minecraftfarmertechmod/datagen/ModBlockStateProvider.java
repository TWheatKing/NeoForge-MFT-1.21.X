package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.block.custom.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

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

        // ADD THESE CUSTOM BLOCKS:
        // Bio Generator - directional with LIT state (off/on front textures)
        ModelFile bioGenOff = models().orientableWithBottom("bio_generator",
                modLoc("block/bio_generator_side"),
                modLoc("block/bio_generator_front"),
                modLoc("block/bio_generator_bottom"),
                modLoc("block/bio_generator_top"));
        ModelFile bioGenOn = models().orientableWithBottom("bio_generator_on",
                modLoc("block/bio_generator_side"),
                modLoc("block/bio_generator_front_on"),
                modLoc("block/bio_generator_bottom"),
                modLoc("block/bio_generator_top"));

        getVariantBuilder(ModBlocks.BIO_GENERATOR.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(BioGeneratorBlock.FACING);
                    boolean lit = state.getValue(BioGeneratorBlock.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(lit ? bioGenOn : bioGenOff)
                            .rotationY(((int) facing.toYRot() + 180) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.BIO_GENERATOR.get(), bioGenOff);

        // Coal Generator - directional with LIT state (off/on front textures)
        ModelFile coalGenOff = models().orientableWithBottom("coal_generator",
                modLoc("block/coal_generator_side"),
                modLoc("block/coal_generator_front"),
                modLoc("block/coal_generator_bottom"),
                modLoc("block/coal_generator_top"));
        ModelFile coalGenOn = models().orientableWithBottom("coal_generator_on",
                modLoc("block/coal_generator_side"),
                modLoc("block/coal_generator_front_on"),
                modLoc("block/coal_generator_bottom"),
                modLoc("block/coal_generator_top"));

        getVariantBuilder(ModBlocks.COAL_GENERATOR.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(CoalGeneratorBlock.FACING);
                    boolean lit = state.getValue(CoalGeneratorBlock.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(lit ? coalGenOn : coalGenOff)
                            .rotationY(((int) facing.toYRot() + 180) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.COAL_GENERATOR.get(), coalGenOff);

        // Iron Furnace - orientable with LIT state
        ModelFile ironFurnaceOff = models().orientable("iron_furnace",
                modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front"), modLoc("block/iron_furnace_top"));
        ModelFile ironFurnaceOn = models().orientable("iron_furnace_on",
                modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front_on"), modLoc("block/iron_furnace_top"));

        getVariantBuilder(ModBlocks.IRON_FURNACE.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(IronFurnaceBlock.FACING);
                    boolean lit = state.getValue(IronFurnaceBlock.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(lit ? ironFurnaceOn : ironFurnaceOff)
                            .rotationY(((int) facing.toYRot() + 180) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.IRON_FURNACE.get(), ironFurnaceOff);

        // Liquifier - directional with front/side/top/bottom textures
        ModelFile liquifierModel = models().orientable("liquifier",
                modLoc("block/liquifier_side"),
                modLoc("block/liquifier_front"),
                modLoc("block/liquifier_top"));

        getVariantBuilder(ModBlocks.LIQUIFIER.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(LiquifierBlock.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(liquifierModel)
                            .rotationY(((int) facing.toYRot() + 180) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.LIQUIFIER.get(), liquifierModel);

        // For Energy Cable (connecting block)
        energyCableBlock();

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

    // Energy Cable block generation - creates models matching your JSON files
    private void energyCableBlock() {
        ResourceLocation cableTexture = modLoc("block/energy_cable");

        // Create core model (center 4x4x4 cube)
        ModelFile coreModel = models().withExistingParent("energy_cable_core", "minecraft:block/block")
                .texture("all", cableTexture)
                .texture("particle", cableTexture)
                .element()
                .from(6, 6, 6).to(10, 10, 10)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        // Create connection models for each direction (no rotation needed)
        ModelFile northConnection = models().withExistingParent("energy_cable_north", "minecraft:block/block")
                .texture("all", cableTexture)
                .element().from(6, 6, 0).to(10, 10, 6)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        ModelFile southConnection = models().withExistingParent("energy_cable_south", "minecraft:block/block")
                .texture("all", cableTexture)
                .element().from(6, 6, 10).to(10, 10, 16)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        ModelFile eastConnection = models().withExistingParent("energy_cable_east", "minecraft:block/block")
                .texture("all", cableTexture)
                .element().from(10, 6, 6).to(16, 10, 10)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        ModelFile westConnection = models().withExistingParent("energy_cable_west", "minecraft:block/block")
                .texture("all", cableTexture)
                .element().from(0, 6, 6).to(6, 10, 10)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        ModelFile upConnection = models().withExistingParent("energy_cable_up", "minecraft:block/block")
                .texture("all", cableTexture)
                .element().from(6, 10, 6).to(10, 16, 10)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        ModelFile downConnection = models().withExistingParent("energy_cable_down", "minecraft:block/block")
                .texture("all", cableTexture)
                .element().from(6, 0, 6).to(10, 6, 10)
                .face(Direction.DOWN).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.UP).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.WEST).uvs(6, 6, 10, 10).texture("#all").end()
                .face(Direction.EAST).uvs(6, 6, 10, 10).texture("#all").end()
                .end();

        // Create multipart blockstate using direction-specific models (no rotations)
        MultiPartBlockStateBuilder builder = getMultipartBuilder(ModBlocks.ENERGY_CABLE.get());

        // Always show core
        builder.part().modelFile(coreModel).addModel();

        // Add connections for each direction using specific models (no rotation)
        builder.part().modelFile(northConnection).addModel()
                .condition(EnergyCableBlock.NORTH, true);
        builder.part().modelFile(southConnection).addModel()
                .condition(EnergyCableBlock.SOUTH, true);
        builder.part().modelFile(eastConnection).addModel()
                .condition(EnergyCableBlock.EAST, true);
        builder.part().modelFile(westConnection).addModel()
                .condition(EnergyCableBlock.WEST, true);
        builder.part().modelFile(upConnection).addModel()
                .condition(EnergyCableBlock.UP, true);
        builder.part().modelFile(downConnection).addModel()
                .condition(EnergyCableBlock.DOWN, true);

        // Create item model using just the core
        simpleBlockItem(ModBlocks.ENERGY_CABLE.get(), coreModel);

        customLamp();

        makeCrop(((CropBlock) ModBlocks.CORN_CROP.get()), "corn_crop_stage", "corn_crop_stage");
        makeBush(((SweetBerryBushBlock) ModBlocks.STRAWBERRY_BUSH.get()), "strawberry_bush_stage", "strawberry_bush_stage");
        // Add this to your registerStatesAndModels() method
        makeWildflowerBush();
    }

    private void makeWildflowerBush() {
        Function<BlockState, ConfiguredModel[]> function = state -> wildflowerBushStates(state);
        getVariantBuilder(ModBlocks.WILDFLOWER_BUSH.get()).forAllStates(function);
    }

    private ConfiguredModel[] wildflowerBushStates(BlockState state) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        int age = state.getValue(WildFlowerBushBlock.AGE);

        models[0] = new ConfiguredModel(models().cross("wildflower_bush_stage" + age,
                ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID,
                        "block/wildflower_bush_stage" + age)).renderType("cutout"));

        return models;
    }

    public void makeBush(SweetBerryBushBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().cross(modelName + state.getValue(StrawberryBushBlock.AGE),
                ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "block/" + textureName + state.getValue(StrawberryBushBlock.AGE))).renderType("cutout"));

        return models;
    }

    public void makeCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((CornCropBlock) block).getAgeProperty()),
                ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "block/" + textureName + state.getValue(((CornCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    private void customLamp() {
        getVariantBuilder(ModBlocks.ZINC_LAMP.get()).forAllStates(state -> {
            if(state.getValue(ZincLampBlock.CLICKED)) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("zinc_lamp_on",
                        ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "block/" + "zinc_lamp_on")))};
            } else {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("zinc_lamp_off",
                        ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "block/" + "zinc_lamp_off")))};
            }
        });

        simpleBlockItem(ModBlocks.ZINC_LAMP.get(), models().cubeAll("zinc_lamp_on",
                ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "block/" + "zinc_lamp_on")));
    }


    // Helper method to get block name
    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("minecraftfarmertechmod:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("minecraftfarmertechmod:block/" + deferredBlock.getId().getPath() + appendix));
    }
}