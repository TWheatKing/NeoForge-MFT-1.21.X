package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import com.thewheatking.minecraftfarmertechmod.screen.LiquifierMenu;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiquifierBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> isPlantMatter(stack); // Plant matter input
                case 1 -> stack.getItem() == Items.WATER_BUCKET; // Water input
                case 2 -> stack.getItem() == Items.BUCKET; // Empty bucket input
                case 3 -> false; // Bio fuel output - no manual input
                default -> false;
            };
        }
    };

    private static final int PLANT_SLOT = 0;
    private static final int WATER_INPUT_SLOT = 1;
    private static final int EMPTY_BUCKET_SLOT = 2;
    private static final int BIO_FUEL_OUTPUT_SLOT = 3;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100; // 5 seconds
    private int waterLevel = 0; // In mB (millibuckets)
    private int bioFuelLevel = 0; // In mB
    private float bioFuelProgress = 0.0f; // Fractional progress toward next bucket

    public LiquifierBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LIQUIFIER.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> LiquifierBlockEntity.this.progress;
                    case 1 -> LiquifierBlockEntity.this.maxProgress;
                    case 2 -> LiquifierBlockEntity.this.waterLevel;
                    case 3 -> LiquifierBlockEntity.this.bioFuelLevel;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> LiquifierBlockEntity.this.progress = pValue;
                    case 1 -> LiquifierBlockEntity.this.maxProgress = pValue;
                    case 2 -> LiquifierBlockEntity.this.waterLevel = pValue;
                    case 3 -> LiquifierBlockEntity.this.bioFuelLevel = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftfarmertechmod.liquifier");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new LiquifierMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("progress", progress);
        pTag.putInt("waterLevel", waterLevel);
        pTag.putInt("bioFuelLevel", bioFuelLevel);
        pTag.putFloat("bioFuelProgress", bioFuelProgress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("progress");
        waterLevel = pTag.getInt("waterLevel");
        bioFuelLevel = pTag.getInt("bioFuelLevel");
        bioFuelProgress = pTag.getFloat("bioFuelProgress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        // Handle water bucket input
        handleWaterInput();

        // Handle bio fuel bucket output
        handleBioFuelOutput();

        // Process plant matter if we have resources
        if(hasRecipe() && hasWater()) {
            increaseCraftingProcess();
            setChanged();

            if(hasProgressFinished()) {
                processPlantMatter();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void handleWaterInput() {
        ItemStack waterBucket = this.itemHandler.getStackInSlot(WATER_INPUT_SLOT);
        if(!waterBucket.isEmpty() && waterBucket.getItem() == Items.WATER_BUCKET && waterLevel < 10000) {
            // Add 1000 mB (1 bucket) of water
            waterLevel = Math.min(waterLevel + 1000, 10000);

            // Replace water bucket with empty bucket
            waterBucket.shrink(1);
            this.itemHandler.setStackInSlot(WATER_INPUT_SLOT, waterBucket);

            ItemStack emptyBucket = new ItemStack(Items.BUCKET);
            if(waterBucket.isEmpty()) {
                this.itemHandler.setStackInSlot(WATER_INPUT_SLOT, emptyBucket);
            }
        }
    }

    private void handleBioFuelOutput() {
        ItemStack emptyBucket = this.itemHandler.getStackInSlot(EMPTY_BUCKET_SLOT);
        ItemStack currentOutput = this.itemHandler.getStackInSlot(BIO_FUEL_OUTPUT_SLOT);

        if(!emptyBucket.isEmpty() && emptyBucket.getItem() == Items.BUCKET &&
                bioFuelLevel >= 1000 && currentOutput.getCount() < 64) {

            // Convert 1000 mB bio fuel to 1 bio fuel bucket
            bioFuelLevel -= 1000;
            emptyBucket.shrink(1);
            this.itemHandler.setStackInSlot(EMPTY_BUCKET_SLOT, emptyBucket);

            ItemStack bioFuelBucket = new ItemStack(ModItems.BIO_FUEL.get());
            if(currentOutput.isEmpty()) {
                this.itemHandler.setStackInSlot(BIO_FUEL_OUTPUT_SLOT, bioFuelBucket);
            } else {
                currentOutput.grow(1);
            }
        }
    }

    private boolean hasWater() {
        return waterLevel >= 10; // Need at least 10 mB for processing
    }

    private void resetProgress() {
        progress = 0;
    }

    private void processPlantMatter() {
        ItemStack plantStack = this.itemHandler.getStackInSlot(PLANT_SLOT);
        if(!plantStack.isEmpty() && waterLevel >= 10) {
            float bioFuelYield = getBioFuelYield(plantStack);
            bioFuelProgress += bioFuelYield;

            // Use 10 mB of water per processing
            waterLevel -= 10;

            // Consume one plant item
            plantStack.shrink(1);
            this.itemHandler.setStackInSlot(PLANT_SLOT, plantStack);

            // Check if we've accumulated enough for a full bucket (1000 mB)
            if(bioFuelProgress >= 1.0f) {
                int bucketsToAdd = (int) bioFuelProgress;
                bioFuelLevel = Math.min(bioFuelLevel + (bucketsToAdd * 1000), 10000);
                bioFuelProgress -= bucketsToAdd;
            }
        }
    }

    private float getBioFuelYield(ItemStack stack) {
        // Basic crops: 0.1 bio fuel each
        if(stack.getItem() == Items.WHEAT || stack.getItem() == Items.CARROT ||
                stack.getItem() == Items.POTATO || stack.getItem() == Items.POISONOUS_POTATO || stack.getItem() == Items.BEETROOT) {
            return 0.1f;
        }

        // Saplings and leaves: 0.5 bio fuel each
        if(stack.getItem().toString().contains("sapling") ||
                stack.getItem().toString().contains("leaves")) {
            return 0.5f;
        }

        // Flowers and tall grass: 0.2 bio fuel each
        if(stack.getItem().toString().contains("flower") ||
                stack.getItem() == Items.GRASS_BLOCK || stack.getItem() == Items.TALL_GRASS || stack.getItem() == Items.SHORT_GRASS ||
                stack.getItem() == Items.FERN || stack.getItem() == Items.LARGE_FERN) {
            return 0.2f;
        }

        return 0.1f; // Default for other plant items
    }

    private boolean isPlantMatter(ItemStack stack) {
        // Check for basic crops
        if(stack.getItem() == Items.WHEAT || stack.getItem() == Items.CARROT ||
                stack.getItem() == Items.POTATO || stack.getItem() == Items.BEETROOT) {
            return true;
        }

        // Check for plant-based items (this is a simple check - you might want to expand this)
        String itemName = stack.getItem().toString().toLowerCase();
        return itemName.contains("sapling") || itemName.contains("leaves") ||
                itemName.contains("flower") || itemName.contains("grass") ||
                itemName.contains("fern") || itemName.contains("seed");
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProcess() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack plantStack = this.itemHandler.getStackInSlot(PLANT_SLOT);
        return !plantStack.isEmpty() && isPlantMatter(plantStack);
    }
}