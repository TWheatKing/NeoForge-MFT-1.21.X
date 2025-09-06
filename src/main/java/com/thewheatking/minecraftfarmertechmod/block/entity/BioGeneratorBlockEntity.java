package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.energy.MftEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import com.thewheatking.minecraftfarmertechmod.screen.BioGeneratorMenu;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class BioGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == ModItems.BIO_FUEL.get(); // Fuel input
                case 1 -> false; // Output slot (empty buckets)
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final MftEnergyStorage energyStorage = new MftEnergyStorage(10000, 0, 60) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            setChanged();
            return super.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            setChanged();
            return super.extractEnergy(maxExtract, simulate);
        }
    };

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 32000; // How much energy per bio fuel (32000 RF = 26+ minutes at 60RF/tick)
    private int fuelBurnTime = 0;
    private int maxFuelBurnTime = 0;

    public BioGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BIO_GENERATOR.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> BioGeneratorBlockEntity.this.progress;
                    case 1 -> BioGeneratorBlockEntity.this.maxProgress;
                    case 2 -> BioGeneratorBlockEntity.this.fuelBurnTime;
                    case 3 -> BioGeneratorBlockEntity.this.maxFuelBurnTime;
                    case 4 -> BioGeneratorBlockEntity.this.energyStorage.getEnergyStored();
                    case 5 -> BioGeneratorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BioGeneratorBlockEntity.this.progress = value;
                    case 1 -> BioGeneratorBlockEntity.this.maxProgress = value;
                    case 2 -> BioGeneratorBlockEntity.this.fuelBurnTime = value;
                    case 3 -> BioGeneratorBlockEntity.this.maxFuelBurnTime = value;
                    case 4 -> BioGeneratorBlockEntity.this.energyStorage.setEnergyStored(value);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.bio_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BioGeneratorMenu(containerId, playerInventory, this, this.data);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    public IEnergyStorage getEnergyStored() {
        return (IEnergyStorage) this.energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
        tag.putInt("fuel_burn_time", fuelBurnTime);
        tag.putInt("max_fuel_burn_time", maxFuelBurnTime);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        energyStorage.setEnergyStored(tag.getInt("energy"));
        progress = tag.getInt("progress");
        fuelBurnTime = tag.getInt("fuel_burn_time");
        maxFuelBurnTime = tag.getInt("max_fuel_burn_time");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        // Distribute energy to adjacent blocks
        distributeEnergy();

        // Check if we're currently burning fuel
        if (isBurning()) {
            fuelBurnTime--;
            generateEnergy();
            setChanged();

            // Update block state if needed
            boolean lit = state.getValue(com.thewheatking.minecraftfarmertechmod.block.custom.BioGeneratorBlock.LIT);
            if (!lit) {
                level.setBlock(pos, state.setValue(com.thewheatking.minecraftfarmertechmod.block.custom.BioGeneratorBlock.LIT, true), 3);
            }
        } else if (hasRecipe() && !isBurning()) {
            consumeFuel();
        } else {
            // Turn off if not burning
            boolean lit = state.getValue(com.thewheatking.minecraftfarmertechmod.block.custom.BioGeneratorBlock.LIT);
            if (lit) {
                level.setBlock(pos, state.setValue(com.thewheatking.minecraftfarmertechmod.block.custom.BioGeneratorBlock.LIT, false), 3);
            }
        }
    }

    private void distributeEnergy() {
        // Distribute energy to adjacent blocks that can accept it
        if (energyStorage.getEnergyStored() > 0) {
            for (var direction : net.minecraft.core.Direction.values()) {
                BlockPos adjacentPos = worldPosition.relative(direction);
                if (level.isLoaded(adjacentPos)) {
                    IEnergyStorage adjacentEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, adjacentPos, direction.getOpposite());
                    if (adjacentEnergy != null && adjacentEnergy.canReceive()) {
                        int energyToTransfer = Math.min(energyStorage.getEnergyStored(), 60); // Transfer up to 60 RF/tick
                        int transferred = adjacentEnergy.receiveEnergy(energyToTransfer, false);
                        if (transferred > 0) {
                            energyStorage.extractEnergy(transferred, false);
                            setChanged();
                        }
                    }
                }
            }
        }
    }

    private void generateEnergy() {
        // Generate 60 RF per tick (Tier 3)
        int energyGenerated = Math.min(60, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
        if (energyGenerated > 0) {
            energyStorage.receiveEnergy(energyGenerated, false);
            progress += energyGenerated;
        }
    }

    private boolean isBurning() {
        return fuelBurnTime > 0;
    }

    private boolean hasRecipe() {
        ItemStack fuelStack = itemHandler.getStackInSlot(0);
        return fuelStack.getItem() == ModItems.BIO_FUEL.get() && fuelStack.getCount() > 0;
    }

    private void consumeFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(0);
        if (fuelStack.getItem() == ModItems.BIO_FUEL.get()) {
            // Set fuel burn time (32000 RF / 60 RF per tick = 533 ticks = 26.65 seconds)
            fuelBurnTime = 533; // About 26+ seconds per bucket
            maxFuelBurnTime = 533;

            // Consume the bio fuel and return empty bucket
            fuelStack.shrink(1);

            // Return empty bucket to output slot
            ItemStack outputStack = itemHandler.getStackInSlot(1);
            if (outputStack.isEmpty()) {
                itemHandler.setStackInSlot(1, new ItemStack(Items.BUCKET));
            } else if (outputStack.getItem() == Items.BUCKET && outputStack.getCount() < 64) {
                outputStack.grow(1);
            }

            setChanged();
        }
    }
}