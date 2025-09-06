package com.thewheatking.minecraftfarmertechmod.block.custom;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class BioFuelBlock extends LiquidBlock {
    public BioFuelBlock(Supplier<? extends FlowingFluid> pFluid, BlockBehaviour.Properties pProperties) {
        super(pFluid.get(), pProperties);
    }
}
