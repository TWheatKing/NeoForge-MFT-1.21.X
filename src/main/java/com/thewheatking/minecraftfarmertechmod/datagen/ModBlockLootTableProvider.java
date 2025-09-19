package com.thewheatking.minecraftfarmertechmod.datagen;

import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
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

        dropSelf(ModBlocks.ZINC_LAMP.get());

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
