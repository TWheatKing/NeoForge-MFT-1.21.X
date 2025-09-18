package com.thewheatking.minecraftfarmertechmod.block;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.block.custom.*;
import com.thewheatking.minecraftfarmertechmod.fluid.ModFluids;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MinecraftFarmerTechMod.MOD_ID);
//register blocks
    public static final DeferredBlock<Block> ZINC_ORE = registerBlock("zinc_ore",
            () -> new DropExperienceBlock(UniformInt.of(2,4),
                    BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .strength(3.0F, 3.0F)
            )
    );
    public static final DeferredBlock<Block> ZINC_DEEPSLATE_ORE = registerBlock("zinc_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(3,6),
                    BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .strength(4.0F, 3.0F)
            )
    );
    public static final DeferredBlock<Block> WHEAT_INGOT_BLOCK = registerBlock("wheat_ingot_block",
            () -> new DropExperienceBlock(UniformInt.of(2,4),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .randomTicks()
                            .strength(3.0F, 3.0F)
            )
    );public static final DeferredBlock<Block> ZINC_BLOCK = registerBlock("zinc_block",
            () -> new DropExperienceBlock(UniformInt.of(2,4),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .randomTicks()
                            .strength(3.0F, 3.0F)
            )
    );
    public static final DeferredBlock<StairBlock> ZINC_STAIRS = registerBlock("zinc_stairs",
            () -> new StairBlock(ModBlocks.ZINC_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> ZINC_SLAB = registerBlock("zinc_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<PressurePlateBlock> ZINC_PRESSURE_PLATE = registerBlock("zinc_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<ButtonBlock> ZINC_BUTTON = registerBlock("zinc_button",
            () -> new ButtonBlock(BlockSetType.IRON, 40, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().noCollission()));

    public static final DeferredBlock<FenceBlock> ZINC_FENCE = registerBlock("zinc_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FenceGateBlock> ZINC_FENCE_GATE = registerBlock("zinc_fence_gate",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<WallBlock> ZINC_WALL = registerBlock("zinc_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<DoorBlock> ZINC_DOOR = registerBlock("zinc_door",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> ZINC_TRAPDOOR = registerBlock("zinc_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().noOcclusion()));

    //custom blocks
    public static final DeferredBlock<Block> IRON_FURNACE = registerBlock("iron_furnace",
            () -> new IronFurnaceBlock());
    public static final DeferredBlock<Block> ZINC_CASING = registerBlock("zinc_casing",
            () -> new ZincCasingBlock());
    public static final DeferredBlock<Block> ANDESITE_CASING = registerBlock("andesite_casing",
            () -> new AndesiteCasingBlock());
    public static final DeferredBlock<Block> LIQUIFIER = registerBlock("liquifier",
            () -> new LiquifierBlock(BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> BIO_GENERATOR = registerBlock("bio_generator",
            () -> new BioGeneratorBlock(BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops()));

    public static final DeferredHolder<Block, LiquidBlock> BIO_FUEL_BLOCK = BLOCKS.register("biofuel_block",
            () -> new BioFuelBlock(ModFluids.BIOFUEL,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .replaceable()
                            .noCollission()
                            .strength(100.0F)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY)));

    // Energy System Blocks
    public static final DeferredBlock<Block> ENERGY_CABLE = registerBlock("energy_cable",
            () -> new EnergyCableBlock());

    public static final DeferredBlock<Block> COAL_GENERATOR = registerBlock("coal_generator",
            () -> new CoalGeneratorBlock());

    public static final DeferredBlock<Block> ENERGY_BATTERY = registerBlock("energy_battery",
            () -> new EnergyBatteryBlock());




    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn =BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
