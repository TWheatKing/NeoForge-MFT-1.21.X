package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Basic Energy Storage - Tier 1 (50k FE capacity)
 */
public class BasicEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public BasicEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.BASIC_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.BASIC);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.BASIC;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes.BasicEnergyStorageMenu(
                containerId, playerInventory,
                new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer())
                        .writeBlockPos(this.getBlockPos()));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.basic_energy_storage");
    }
}