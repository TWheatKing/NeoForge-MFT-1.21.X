package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.block.custom.CoalGeneratorBlock;
import com.thewheatking.minecraftfarmertechmod.energy.MftEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.energy.IEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.screen.CoalGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block Entity for the Coal Generator
 * Burns coal to generate electricity
 */
public class CoalGeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && isFuel(stack); // Only fuel items allowed
        }
    };

    // Energy storage: 50,000 RF capacity, generates 20 RF/tick
    private final IEnergyStorage energyStorage = new MftEnergyStorage(50000, 100, 100, 0);

    protected final ContainerData data;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int energyGenerated = 0;

    // Generation settings
    private static final int ENERGY_PER_TICK = 20; // RF/tick generation rate
    private static final int TOTAL_GENERATION_TIME = 200; // How long to generate energy from one fuel

    public CoalGeneratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COAL_GENERATOR.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CoalGeneratorBlockEntity.this.burnTime;
                    case 1 -> CoalGeneratorBlockEntity.this.maxBurnTime;
                    case 2 -> CoalGeneratorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> CoalGeneratorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CoalGeneratorBlockEntity.this.burnTime = pValue;
                    case 1 -> CoalGeneratorBlockEntity.this.maxBurnTime = pValue;
                    case 2 -> ((MftEnergyStorage)CoalGeneratorBlockEntity.this.energyStorage).setEnergyStored(pValue);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CoalGeneratorBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            pBlockEntity.serverTick(pLevel, pPos, pState);
        }
    }

    private void serverTick(Level pLevel, BlockPos pPos, BlockState pState) {
        boolean wasLit = pState.getValue(CoalGeneratorBlock.LIT);
        boolean isGenerating = false;

        // Try to consume fuel if we need it
        if (burnTime <= 0 && canGenerate()) {
            consumeFuel();
        }

        // Generate energy if we have fuel and space for energy
        if (burnTime > 0 && canGenerate()) {
            burnTime--;
            generateEnergy();
            isGenerating = true;
        }

        // Update block state if lit status changed
        if (wasLit != isGenerating) {
            pLevel.setBlock(pPos, pState.setValue(CoalGeneratorBlock.LIT, isGenerating), 3);
        }

        // Mark changed for data sync
        setChanged();
    }

    private boolean canGenerate() {
        return energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }

    private void consumeFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(0);
        if (!fuelStack.isEmpty() && isFuel(fuelStack)) {
            int fuelBurnTime = getBurnTime(fuelStack.getItem());
            if (fuelBurnTime > 0) {
                burnTime = fuelBurnTime;
                maxBurnTime = fuelBurnTime;
                itemHandler.extractItem(0, 1, false);
            }
        }
    }

    private void generateEnergy() {
        int energyGenerated = Math.min(ENERGY_PER_TICK, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
        if (energyGenerated > 0) {
            energyStorage.receiveEnergy(energyGenerated, false);
            this.energyGenerated += energyGenerated;
        }
    }

    private boolean isFuel(ItemStack stack) {
        return getBurnTime(stack.getItem()) > 0;
    }

    private int getBurnTime(Item item) {
        // Burn times for different fuels (in ticks)
        if (item == Items.COAL) return 1600;
        if (item == Items.CHARCOAL) return 1600;
        if (item == Items.COAL_BLOCK) return 16000;
        if (item == Items.STICK) return 100;
        if (item == Items.WOODEN_PICKAXE || item == Items.WOODEN_AXE || item == Items.WOODEN_SHOVEL) return 200;
        return 0;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.coal_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CoalGeneratorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.put("energy", ((MftEnergyStorage)energyStorage).serializeNBT());
        pTag.putInt("burn_time", burnTime);
        pTag.putInt("max_burn_time", maxBurnTime);
        pTag.putInt("energy_generated", energyGenerated);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        if (pTag.contains("energy")) {
            ((MftEnergyStorage)energyStorage).deserializeNBT(pTag.getCompound("energy"));
        }
        burnTime = pTag.getInt("burn_time");
        maxBurnTime = pTag.getInt("max_burn_time");
        energyGenerated = pTag.getInt("energy_generated");
    }
}