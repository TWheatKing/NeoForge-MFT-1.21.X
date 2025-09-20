package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;

public class ModItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

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
        //basicItem(ModItems.CHISEL.get());
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
        basicItem(ModItems.CORN.get());
        basicItem(ModItems.CORN_SEEDS.get());
        basicItem(ModItems.STRAWBERRIES.get());
        basicItem(ModItems.COTTON_SWAB.get());
        basicItem(ModItems.WILDFLOWER.get());
        basicItem(ModItems.BAR_BRAWL_MUSIC_DISC.get());

        //tools
        handheldItem(ModItems.ZINC_SWORD);
        handheldItem(ModItems.ZINC_PICKAXE);
        handheldItem(ModItems.ZINC_SHOVEL);
        handheldItem(ModItems.ZINC_AXE);
        handheldItem(ModItems.ZINC_HOE);
        handheldItem(ModItems.ZINC_DRILL);

        //armor
        trimmedArmorItem(ModItems.ZINC_HELMET);
        trimmedArmorItem(ModItems.ZINC_CHESTPLATE);
        trimmedArmorItem(ModItems.ZINC_LEGGINGS);
        trimmedArmorItem(ModItems.ZINC_BOOTS);
        basicItem(ModItems.ZINC_HORSE_ARMOR.get());


        buttonItem(ModBlocks.ZINC_BUTTON, ModBlocks.ZINC_BLOCK);
        fenceItem(ModBlocks.ZINC_FENCE, ModBlocks.ZINC_BLOCK);
        wallItem(ModBlocks.ZINC_WALL, ModBlocks.ZINC_BLOCK);

        basicItem(ModBlocks.ZINC_DOOR.asItem());
        basicItem(ModItems.ZINC_SMITHING_TEMPLATE.get());
    }

    // Shoutout to El_Redstoniano for making this
    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        final String MOD_ID = MinecraftFarmerTechMod.MOD_ID; // Change this to your mod id

        if(itemDeferredItem.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemDeferredItem.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace()  + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemDeferredItem.getId().getPath()));
            });
        }
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
