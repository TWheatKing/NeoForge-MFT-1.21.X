package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        List<ItemLike> ZINC_SMELTABLES = List.of(ModItems.RAW_ZINC,
                ModBlocks.ZINC_ORE, ModBlocks.ZINC_DEEPSLATE_ORE);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.WHEAT_INGOT_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.WHEAT_INGOT.get())
                .unlockedBy("has_wheat_ingot", has(ModItems.WHEAT_INGOT.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WHEAT_INGOT.get(), 9)
                .requires(ModBlocks.WHEAT_INGOT_BLOCK)
                .unlockedBy("has_wheat_ingot_block", has(ModBlocks.WHEAT_INGOT_BLOCK)).save(recipeOutput);

        //ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BISMUTH.get(), 18)
               // .requires(ModBlocks.MAGIC_BLOCK)
               // .unlockedBy("has_magic_block", has(ModBlocks.MAGIC_BLOCK))
                //.save(recipeOutput, "tutorialmod:bismuth_from_magic_block");

        oreSmelting(recipeOutput, ZINC_SMELTABLES, RecipeCategory.MISC, ModItems.ZINC_INGOT.get(), 0.25f, 200, "zinc");
        oreBlasting(recipeOutput, ZINC_SMELTABLES, RecipeCategory.MISC, ModItems.ZINC_INGOT.get(), 0.25f, 100, "zinc");

        stairBuilder(ModBlocks.ZINC_STAIRS.get(), Ingredient.of(ModItems.ZINC_INGOT)).group("zinc")
                .unlockedBy("has_zinc_ingot", has(ModItems.ZINC_INGOT)).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.ZINC_SLAB.get(), ModItems.ZINC_INGOT.get());

        buttonBuilder(ModBlocks.ZINC_BUTTON.get(), Ingredient.of(ModItems.ZINC_INGOT.get())).group("zinc")
                .unlockedBy("has_zinc_ingot", has(ModItems.ZINC_INGOT.get())).save(recipeOutput);
        pressurePlate(recipeOutput, ModBlocks.ZINC_PRESSURE_PLATE.get(), ModItems.ZINC_INGOT.get());

        fenceBuilder(ModBlocks.ZINC_FENCE.get(), Ingredient.of(ModItems.ZINC_INGOT.get())).group("zinc")
                .unlockedBy("has_zinc_ingot", has(ModItems.ZINC_INGOT.get())).save(recipeOutput);
        fenceGateBuilder(ModBlocks.ZINC_FENCE_GATE.get(), Ingredient.of(ModItems.ZINC_INGOT.get())).group("zinc")
                .unlockedBy("has_zinc_ingot", has(ModItems.ZINC_INGOT.get())).save(recipeOutput);
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.ZINC_WALL.get(), ModItems.ZINC_INGOT.get());

        doorBuilder(ModBlocks.ZINC_DOOR.get(), Ingredient.of(ModItems.ZINC_INGOT.get())).group("zinc")
                .unlockedBy("has_zinc_ingot", has(ModItems.ZINC_INGOT.get())).save(recipeOutput);
        trapdoorBuilder(ModBlocks.ZINC_TRAPDOOR.get(), Ingredient.of(ModItems.ZINC_INGOT.get())).group("zinc")
                .unlockedBy("has_zinc_ingot", has(ModItems.ZINC_INGOT.get())).save(recipeOutput);

        trimSmithing(recipeOutput, ModItems.ZINC_SMITHING_TEMPLATE.get(), ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, "zinc"));
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, MinecraftFarmerTechMod.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}