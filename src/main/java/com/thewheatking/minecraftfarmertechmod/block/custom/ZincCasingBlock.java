package com.thewheatking.minecraftfarmertechmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ZincCasingBlock extends Block {

    public ZincCasingBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(3.0f, 6.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (!pLevel.isClientSide()) {
            // You can add custom behavior here if needed when right-clicking the zinc_casing itself
            pLevel.playSound(null, pPos, SoundEvents.METAL_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }
}
