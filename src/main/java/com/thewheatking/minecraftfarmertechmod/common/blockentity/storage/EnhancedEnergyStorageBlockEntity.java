package com.thewheatking.minecraftfarmertechmod.common.blockentity.storage;

import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Enhanced Energy Storage - Tier 2 (200k FE capacity)
 */
public class EnhancedEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public EnhancedEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.ENHANCED_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.ENHANCED);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.ENHANCED;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.thewheatking.minecraftfarmertechmod.hybrid.HybridMenuTypes.EnhancedEnergyStorageMenu(
                containerId, playerInventory,
                new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer())
                        .writeBlockPos(this.getBlockPos()));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.enhanced_energy_storage");
    }
}