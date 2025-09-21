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
 * Advanced Energy Storage - Tier 3 (1M FE capacity)
 */
public class AdvancedEnergyStorageBlockEntity extends EnergyStorageBlockEntity {

    public AdvancedEnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.ADVANCED_ENERGY_STORAGE.get(), pos, state, HybridEnergyStorage.EnergyTier.ADVANCED);
    }

    @Override
    public HybridEnergyStorage.EnergyTier getStorageTier() {
        return HybridEnergyStorage.EnergyTier.ADVANCED;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // return new AdvancedEnergyStorageMenu(containerId, playerInventory, this);
        return null; // Placeholder until menu is implemented
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.advanced_energy_storage");
    }
}