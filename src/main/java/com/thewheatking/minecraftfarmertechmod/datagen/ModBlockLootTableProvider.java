package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.block.custom.CornCropBlock;
import com.thewheatking.minecraftfarmertechmod.block.custom.WildFlowerBushBlock;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.BIO_GENERATOR.get());
        dropSelf(ModBlocks.COAL_GENERATOR.get());
        dropSelf(ModBlocks.ANDESITE_CASING.get());
        dropSelf(ModBlocks.ENERGY_BATTERY.get());
        dropSelf(ModBlocks.ENERGY_CABLE.get());
        dropSelf(ModBlocks.IRON_FURNACE.get());
        dropSelf(ModBlocks.LIQUIFIER.get());
        dropSelf(ModBlocks.ZINC_CASING.get());
        dropSelf(ModBlocks.WHEAT_INGOT_BLOCK.get());
        dropSelf(ModBlocks.ZINC_BLOCK.get());

        dropSelf(ModBlocks.ZINC_STAIRS.get());
        add(ModBlocks.ZINC_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.ZINC_SLAB.get()));
        dropSelf(ModBlocks.ZINC_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.ZINC_BUTTON.get());
        dropSelf(ModBlocks.ZINC_FENCE.get());
        dropSelf(ModBlocks.ZINC_FENCE_GATE.get());
        dropSelf(ModBlocks.ZINC_WALL.get());
        dropSelf(ModBlocks.ZINC_TRAPDOOR.get());
        add(ModBlocks.ZINC_DOOR.get(),
                block -> createDoorTable(ModBlocks.ZINC_DOOR.get()));

        add(ModBlocks.ZINC_ORE.get(),
                block -> createOreDrop(ModBlocks.ZINC_ORE.get(), ModItems.RAW_ZINC.get()));
        add(ModBlocks.ZINC_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.ZINC_DEEPSLATE_ORE.get(), ModItems.RAW_ZINC.get(), 2, 5));

        add(ModBlocks.ZINC_END_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.ZINC_END_ORE.get(), ModItems.RAW_ZINC.get(), 3, 6));
        add(ModBlocks.ZINC_NETHER_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.ZINC_NETHER_ORE.get(), ModItems.RAW_ZINC.get(), 4, 8));

        dropSelf(ModBlocks.ZINC_LAMP.get());

        LootItemCondition.Builder lootItemConditionBuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CORN_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CornCropBlock.AGE, 7));

        this.add(ModBlocks.CORN_CROP.get(), this.createCropDrops(ModBlocks.CORN_CROP.get(),
                ModItems.CORN.get(), ModItems.CORN_SEEDS.get(), lootItemConditionBuilder));

        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.add(ModBlocks.STRAWBERRY_BUSH.get(), block -> this.applyExplosionDecay(
                block,LootTable.lootTable().withPool(LootPool.lootPool().when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.STRAWBERRY_BUSH.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                ).add(LootItem.lootTableItem(ModItems.STRAWBERRIES.get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                ).withPool(LootPool.lootPool().when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.STRAWBERRY_BUSH.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                ).add(LootItem.lootTableItem(ModItems.STRAWBERRIES.get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )));

        // FIXED: Proper wildflower bush loot table - only cotton swabs, no wildflower drops here
        // (wildflowers will be handled by the interaction system)
        this.add(ModBlocks.WILDFLOWER_BUSH.get(), block -> this.applyExplosionDecay(
                block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.WILDFLOWER_BUSH.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WildFlowerBushBlock.AGE, 3))
                                ).add(LootItem.lootTableItem(ModItems.COTTON_SWAB.get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                ).withPool(LootPool.lootPool().when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.WILDFLOWER_BUSH.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WildFlowerBushBlock.AGE, 2))
                                ).add(LootItem.lootTableItem(ModItems.COTTON_SWAB.get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}