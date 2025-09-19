package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MinecraftFarmerTechMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.RAW_ZINC.get());
        basicItem(ModItems.ZINC_INGOT.get());
        basicItem(ModItems.WHEAT_INGOT.get());
        basicItem(ModItems.RAM.get());
        basicItem(ModItems.ADVANCED_CIRCUIT_BOARD.get());
        basicItem(ModItems.BASIC_CIRCUIT_BOARD.get());
        basicItem(ModItems.BASIC_BLADE.get());
        basicItem(ModItems.ADVANCED_BLADE.get());
        basicItem(ModItems.IRON_BIT.get());
        basicItem(ModItems.GOLD_BIT.get());
        basicItem(ModItems.COPPER_BIT.get());
        basicItem(ModItems.DIAMOND_BIT.get());
        basicItem(ModItems.NETHERITE_BIT.get());
        basicItem(ModItems.CHISEL.get());
        basicItem(ModItems.WRENCH.get());
        basicItem(ModItems.FUSION_CIRCUIT_BOARD.get());
        basicItem(ModItems.WISK.get());
        basicItem(ModItems.BIO_FUEL_BUCKET.get());
        basicItem(ModItems.BRASS.get());
        basicItem(ModItems.GRATE.get());
        basicItem(ModItems.COPPER_COIL.get());
        basicItem(ModItems.GOLD_COIL.get());
        basicItem(ModItems.EMPTY_COIL.get());
        basicItem(ModItems.COPPER_COIL.get());
        basicItem(ModItems.DIAMOND_COIL.get());
        basicItem(ModItems.IRON_PLATE.get());
        basicItem(ModItems.BRASS_PLATE.get());
        basicItem(ModItems.GOLD_PLATE.get());
        basicItem(ModItems.DIAMOND_PLATE.get());
        basicItem(ModItems.NETHERITE_PLATE.get());
        basicItem(ModItems.ANDESITE_ALLOY.get());
        basicItem(ModItems.ZINC_ALLOY.get());
        basicItem(ModItems.BURGER.get());

        //tools
        handheldItem(ModItems.ZINC_SWORD);
        handheldItem(ModItems.ZINC_PICKAXE);
        handheldItem(ModItems.ZINC_SHOVEL);
        handheldItem(ModItems.ZINC_AXE);
        handheldItem(ModItems.ZINC_HOE);



        buttonItem(ModBlocks.ZINC_BUTTON, ModBlocks.ZINC_BLOCK);
        fenceItem(ModBlocks.ZINC_FENCE, ModBlocks.ZINC_BLOCK);
        wallItem(ModBlocks.ZINC_WALL, ModBlocks.ZINC_BLOCK);

        basicItem(ModBlocks.ZINC_DOOR.asItem());
    }

    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }
    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "item/" + item.getId().getPath()));
    }
}
