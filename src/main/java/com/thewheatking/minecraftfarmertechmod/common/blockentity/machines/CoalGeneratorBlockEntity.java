package com.thewheatking.minecraftfarmertechmod.common.blockentity.machines;

import com.thewheatking.minecraftfarmertechmod.common.blockentity.base.BaseMachineBlockEntity;
import com.thewheatking.minecraftfarmertechmod.energy.HybridEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * CORRECTED: Enhanced Coal Generator - Basic tier power generation with hybrid energy system
 * Burns coal and other fuel items to generate energy using the new hybrid architecture
 *
 * File Location: src/main/java/com/thewheatking/minecraftfarmertechmod/common/blockentity/machines/CoalGeneratorBlockEntity.java
 * Purpose: Basic energy generation from combustible fuel items
 */
public class CoalGeneratorBlockEntity extends BaseMachineBlockEntity {

    // Generator configuration - Basic tier
    private static final int ENERGY_CAPACITY = 32000;      // 32k FE capacity
    private static final int ENERGY_TRANSFER = 200;        // 200 FE/t transfer
    private static final int ENERGY_GENERATION_RATE = 40;  // 40 FE per tick
    private static final int FUEL_SLOT = 0;
    private static final int INVENTORY_SIZE = 1;

    // Fuel burning state
    private int burnTime = 0;
    private int totalBurnTime = 0;
    private boolean isBurning = false;

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.HYBRID_COAL_GENERATOR.get(), pos, state,
                ENERGY_CAPACITY, 0, ENERGY_TRANSFER, INVENTORY_SIZE); // Can't receive energy, only generate
    }

    @Override
    protected HybridEnergyStorage createEnergyStorage() {
        // Create energy storage that can only output (generators don't receive energy)
        return new HybridEnergyStorage(ENERGY_CAPACITY, 0, ENERGY_TRANSFER,
                ENERGY_CAPACITY * HybridEnergyStorage.getFeToMftRatio(),
                0, // Can't receive MFT energy either
                ENERGY_TRANSFER * HybridEnergyStorage.getFeToMftRatio());
    }

    @Override
    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                CoalGeneratorBlockEntity.this.setChanged();
                CoalGeneratorBlockEntity.this.markUpdated();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                // Only accept fuel items
                return isFuelItem(stack);
            }
        };
    }

    @Override
    protected boolean canOperate() {
        // Can operate if we have fuel or are currently burning, and have space for energy
        return (isBurning || !inventory.getStackInSlot(FUEL_SLOT).isEmpty())
                && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }

    @Override
    protected void performOperation() {
        // Handle fuel consumption and energy generation
        if (isBurning) {
            // Currently burning fuel
            burnTime--;
            isWorking = true;

            // Generate energy using hybrid system
            int energyGenerated = (int)(ENERGY_GENERATION_RATE * efficiencyMultiplier);
            energyStorage.receiveEnergy(energyGenerated, false);

            // Check if fuel is depleted
            if (burnTime <= 0) {
                isBurning = false;
                totalBurnTime = 0;
                isWorking = false;
                markUpdated();
            }
        }

        // Try to start burning new fuel
        if (!isBurning && !inventory.getStackInSlot(FUEL_SLOT).isEmpty()) {
            ItemStack fuelStack = inventory.getStackInSlot(FUEL_SLOT);
            int fuelBurnTime = getFuelBurnTime(fuelStack);

            if (fuelBurnTime > 0) {
                // Start burning
                burnTime = fuelBurnTime;
                totalBurnTime = fuelBurnTime;
                maxWorkTime = fuelBurnTime;
                workProgress = fuelBurnTime;
                isBurning = true;
                isWorking = true;

                // Consume one item
                fuelStack.shrink(1);
                inventory.setStackInSlot(FUEL_SLOT, fuelStack);
                markUpdated();
            }
        }

        // Update work progress for GUI
        if (isBurning) {
            workProgress = burnTime;
        }
    }

    @Override
    protected boolean canOutputEnergy(Direction direction) {
        // Generators can output energy on all sides
        return true;
    }

    @Override
    protected boolean canInputEnergy(Direction direction) {
        // Generators don't accept energy input
        return false;
    }

    @Override
    protected int getEnergyPerTick() {
        // Generators don't consume energy, they produce it
        return 0;
    }

    @Override
    protected boolean hasEnoughEnergy() {
        // Generators always have "enough energy" since they produce it
        return true;
    }

    // Fuel handling methods
    private boolean isFuelItem(ItemStack stack) {
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

    // Static tick method for block entity ticker
    public static void tick(Level level, BlockPos pos, BlockState state, CoalGeneratorBlockEntity blockEntity) {
        if (level.isClientSide()) {
            blockEntity.clientTick();
        } else {
            boolean wasLit = blockEntity.isBurning();

            // Call the base machine tick
            blockEntity.serverTick();

            // Update block state if burning state changed
            if (wasLit != blockEntity.isBurning()) {
                // Update LIT property on the block if it exists
                // This would need to be implemented in the block class
                blockEntity.markUpdated();
            }
        }
    }

    // NBT save/load for fuel burning state
    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalData(tag, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("TotalBurnTime", totalBurnTime);
        tag.putBoolean("IsBurning", isBurning);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditionalData(tag, registries);
        burnTime = tag.getInt("BurnTime");
        totalBurnTime = tag.getInt("TotalBurnTime");
        isBurning = tag.getBoolean("IsBurning");

        // Update work progress after loading
        if (isBurning) {
            maxWorkTime = totalBurnTime;
            workProgress = burnTime;
            isWorking = true;
        }
    }

    // GUI creation
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // This would need to be implemented when you create the menu class
        // return new CoalGeneratorMenu(containerId, playerInventory, this);
        return null; // Placeholder
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.hybrid_coal_generator");
    }

    // Getters for GUI display
    public boolean isBurning() {
        return isBurning;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getTotalBurnTime() {
        return totalBurnTime;
    }

    public float getBurnProgress() {
        if (totalBurnTime == 0) return 0.0f;
        return (float) burnTime / totalBurnTime;
    }

    public int getBurnProgressScaled(int scale) {
        if (totalBurnTime == 0) return 0;
        return burnTime * scale / totalBurnTime;
    }

    public int getEnergyGenerationRate() {
        return (int)(ENERGY_GENERATION_RATE * efficiencyMultiplier);
    }

    public float getEnergyFillPercentage() {
        if (energyStorage.getMaxEnergyStored() == 0) return 0.0f;
        return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
    }

    public int getEnergyFillScaled(int scale) {
        if (energyStorage.getMaxEnergyStored() == 0) return 0;
        return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
    }

    @Override
    public String getDebugInfo() {
        return String.format("%s, Burning: %s (%d/%d ticks), Generation: %d FE/t (%.1f%% efficiency)",
                super.getDebugInfo(),
                isBurning ? "Yes" : "No",
                burnTime, totalBurnTime,
                getEnergyGenerationRate(),
                efficiencyMultiplier * 100);
    }

    // Particle and sound effects
    @Override
    protected void spawnParticles() {
        if (isBurning && level != null && level.isClientSide()) {
            // Spawn smoke and flame particles
            // This would need to be implemented with particle spawning code
            // Example: level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, 0, 0.1, 0);
        }
    }

    @Override
    protected void playWorkingSounds() {
        if (isBurning && level != null && level.isClientSide()) {
            // Play generator working sounds
            // This would need to be implemented with sound playing code
        }
    }
}