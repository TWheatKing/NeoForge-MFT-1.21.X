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
 * Superior Energy Storage - Tier 4 (5M FE capacity)
 */
public class SuperiorEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public SuperiorEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.SUPERIOR_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.SUPERIOR);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.SUPERIOR;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // return new SuperiorEnergyStorageMenu(containerId, playerInventory, this);
        return null; // Placeholder until menu is implemented
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.superior_energy_storage");
    }
}