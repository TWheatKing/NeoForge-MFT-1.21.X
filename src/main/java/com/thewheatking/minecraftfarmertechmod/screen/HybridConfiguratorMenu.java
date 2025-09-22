package com.thewheatking.minecraftfarmertechmod.screen;

import com.thewheatking.minecraftfarmertechmod.menu.base.BaseEnergyStorageMenu;
import com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.HybridConfiguratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class HybridCoalGeneratorMenu extends BaseEnergyStorageMenu {

    public HybridCoalGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, extraData);
        if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity coalGen) {
            addSlot(new SlotItemHandler(coalGen.getInventory(), 0, 80, 35));
        }
    }

    public HybridCoalGeneratorMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(HYBRID_COAL_GENERATOR.get(), containerId, playerInventory, blockEntity);
        if (blockEntity instanceof com.thewheatking.minecraftfarmertechmod.common.blockentity.machines.CoalGeneratorBlockEntity coalGen) {
            addSlot(new SlotItemHandler(coalGen.getInventory(), 0, 80, 35));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        net.minecraft.world.inventory.Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            if (index == 0) {
                if (!moveItemStackTo(stackInSlot, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (net.neoforged.neoforge.common.CommonHooks.getBurnTime(stackInSlot, null) > 0) {
                    if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }
}