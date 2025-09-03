package com.thewheatking.minecraftfarmertechmod.block;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.thewheatking.minecraftfarmertechmod.block.custom.ZincCasingBlock;

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
    //custom blocks
    // Register your Iron Furnace block
    //public static final DeferredBlock<IronFurnaceBlock> IRON_FURNACE_BLOCK = BLOCKS.register("iron_furnace",
            //() -> new IronFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    //.requiresCorrectToolForDrops()
                    //.strength(5.0F, 6.0F)
                    //.lightLevel(state -> state.getValue(IronFurnaceBlock.LIT) ? 13 : 0)));
    public static final DeferredBlock<Block> ZINC_CASING = registerBlock("zinc_casing",
            ZincCasingBlock::new);
    public static final DeferredBlock<Block> ANDESITE_CASING = registerBlock("andesite_casing",
            ZincCasingBlock::new);





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
