package com.thewheatking.minecraftfarmertechmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.thewheatking.minecraftfarmertechmod.block.entity.EnergyBatteryBlockEntity;
import com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Energy Battery Block - Stores electrical energy
 */
public class EnergyBatteryBlock extends BaseEntityBlock {
    public static final MapCodec<EnergyBatteryBlock> CODEC = simpleCodec(properties -> new EnergyBatteryBlock());

    public EnergyBatteryBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f, 4.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos,
                                            Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof EnergyBatteryBlockEntity battery) {
                // Show energy storage info in chat
                int stored = battery.getEnergyStorage(null).getEnergyStored();
                int max = battery.getEnergyStorage(null).getMaxEnergyStored();
                pPlayer.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                                "Energy: " + stored + "/" + max + " RF (" +
                                        Math.round((float)stored / max * 100) + "%)"),
                        true);
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof EnergyBatteryBlockEntity battery) {
                // No items to drop, just energy data is lost
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EnergyBatteryBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}