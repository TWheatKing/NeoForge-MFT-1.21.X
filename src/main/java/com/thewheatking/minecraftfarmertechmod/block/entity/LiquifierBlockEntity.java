package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.config.SideConfig;
import com.thewheatking.minecraftfarmertechmod.config.SideConfigurable;
import com.thewheatking.minecraftfarmertechmod.energy.IEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.energy.MftEnergyStorage;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import com.thewheatking.minecraftfarmertechmod.screen.LiquifierMenu;
import com.thewheatking.minecraftfarmertechmod.screen.SideConfigMenu;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiquifierBlockEntity extends BlockEntity implements MenuProvider, SideConfigurable {
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

    // Energy storage - can only receive energy, not provide it
    private final MftEnergyStorage energyStorage = new MftEnergyStorage(10000, 100, 0) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }
    };

    // Side configuration
    private SideConfig sideConfig = new SideConfig();

    public static final int PLANT_SLOT = 0;
    public static final int WATER_INPUT_SLOT = 1;
    public static final int EMPTY_BUCKET_SLOT = 2;
    public static final int BIO_FUEL_OUTPUT_SLOT = 3;

    // Energy consumption per processing tick
    private static final int ENERGY_PER_TICK = 20; // 20 RF per tick when processing

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
                    case 4 -> LiquifierBlockEntity.this.energyStorage.getEnergyStored();
                    case 5 -> LiquifierBlockEntity.this.energyStorage.getMaxEnergyStored();
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
                    case 4 -> LiquifierBlockEntity.this.energyStorage.setEnergyStored(pValue);
                    case 5 -> { /* Max energy is read-only */ }
                }
            }

            @Override
            public int getCount() {
                return 6; // Added energy fields
            }
        };
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * Gets the item handler for the given direction, respecting side configuration
     */
    public IItemHandler getItemHandler(@Nullable Direction direction) {
        if (direction == null) {
            return itemHandler; // Internal access gets full handler
        }

        return new SideAwareItemHandler(itemHandler, direction, sideConfig);
    }
    private static class SideAwareItemHandler implements IItemHandler {
        private final IItemHandler wrapped;
        private final Direction side;
        private final SideConfig sideConfig;

        public SideAwareItemHandler(IItemHandler wrapped, Direction side, SideConfig sideConfig) {
            this.wrapped = wrapped;
            this.side = side;
            this.sideConfig = sideConfig;
        }

        @Override
        public int getSlots() {
            return wrapped.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return wrapped.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Only allow input if this side can input
            if (!sideConfig.canInput(side)) {
                return stack; // Cannot insert
            }

            // Don't allow external insertion into output slot
            if (slot == BIO_FUEL_OUTPUT_SLOT) {
                return stack;
            }

            // Auto-route items to correct slots based on item type
            if (stack.getItem() == Items.WATER_BUCKET) {
                // Water buckets always go to water input slot
                return wrapped.insertItem(WATER_INPUT_SLOT, stack, simulate);
            } else if (stack.getItem() == Items.BUCKET) {
                // Empty buckets always go to empty bucket slot
                return wrapped.insertItem(EMPTY_BUCKET_SLOT, stack, simulate);
            } else if (isPlantMatterItem(stack)) {
                // Plant matter always goes to plant slot
                return wrapped.insertItem(PLANT_SLOT, stack, simulate);
            }

            // If the item doesn't match any category, don't allow insertion
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Only allow output if this side can output
            if (!sideConfig.canOutput(side)) {
                return ItemStack.EMPTY; // Cannot extract
            }

            // Only allow extraction from output slot
            if (slot != BIO_FUEL_OUTPUT_SLOT) {
                return ItemStack.EMPTY; // Can only extract from output
            }

            return wrapped.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return wrapped.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Check side config first
            if (!sideConfig.canInput(side)) {
                return false;
            }

            // Don't allow external insertion into output slot
            if (slot == BIO_FUEL_OUTPUT_SLOT) {
                return false;
            }

            return wrapped.isItemValid(slot, stack);
        }

        private boolean isPlantMatterItem(ItemStack stack) {
            if(stack.getItem() == Items.WHEAT || stack.getItem() == Items.CARROT ||
                    stack.getItem() == Items.POTATO || stack.getItem() == Items.BEETROOT) {
                return true;
            }

            String itemName = stack.getItem().toString().toLowerCase();
            return itemName.contains("sapling") || itemName.contains("leaves") ||
                    itemName.contains("flower") || itemName.contains("grass") ||
                    itemName.contains("fern") || itemName.contains("seed");
        }
    }
    /**
     * Gets the energy storage for the given direction, respecting side configuration
     */
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        if (direction != null && !sideConfig.canInputEnergy(direction)) {
            return null; // Block energy access from non-configured sides
        }
        return energyStorage;
    }

    // SideConfigurable implementation
    @Override
    public SideConfig getSideConfig() {
        return sideConfig;
    }

    @Override
    public void setSideConfig(SideConfig config) {
        this.sideConfig = config;
        setChanged();

        // Add debug logging
        System.out.println("Setting side config: " + config.serializeNBT());

        // Also ensure NBT gets updated
        if (level != null && !level.isClientSide) {
            level.blockEntityChanged(getBlockPos());
            System.out.println("Marked block entity as changed on server");
        }
    }
    @Override
    public void openConfigGui(Player player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Configure " + LiquifierBlockEntity.this.getDisplayName().getString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                return new SideConfigMenu(containerId, playerInventory, LiquifierBlockEntity.this);
            }
        }, getBlockPos());
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
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.put("sideConfig", sideConfig.serializeNBT());
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
        energyStorage.deserializeNBT(pTag.getCompound("energy"));
        if (pTag.contains("sideConfig")) {
            sideConfig.deserializeNBT(pTag.getCompound("sideConfig"));
            System.out.println("Loaded side config: " + pTag.getCompound("sideConfig"));
        } else {
            System.out.println("No side config found in NBT");
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        // Handle water bucket input
        handleWaterInput();

        // Handle bio fuel bucket output
        handleBioFuelOutput();

        // Process plant matter if we have resources AND energy
        if(hasRecipe() && hasWater() && hasEnergy()) {
            increaseCraftingProcess();

            // Consume energy while processing
            energyStorage.extractEnergy(ENERGY_PER_TICK, false);
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

            // Remove the water bucket
            waterBucket.shrink(1);
            this.itemHandler.setStackInSlot(WATER_INPUT_SLOT, waterBucket);

            // Put the empty bucket in the EMPTY_BUCKET_SLOT (slot 2), not back in slot 1
            ItemStack emptyBucketSlot = this.itemHandler.getStackInSlot(EMPTY_BUCKET_SLOT);
            if (emptyBucketSlot.isEmpty()) {
                this.itemHandler.setStackInSlot(EMPTY_BUCKET_SLOT, new ItemStack(Items.BUCKET));
            } else if (emptyBucketSlot.getItem() == Items.BUCKET && emptyBucketSlot.getCount() < 64) {
                emptyBucketSlot.grow(1);
            }
            // If empty bucket slot is full, the empty bucket is lost (or you could add overflow logic)
        }
    }

    private void handleBioFuelOutput() {
        ItemStack emptyBucket = this.itemHandler.getStackInSlot(EMPTY_BUCKET_SLOT);
        ItemStack currentOutput = this.itemHandler.getStackInSlot(BIO_FUEL_OUTPUT_SLOT);

        // Check if we have an empty bucket, enough bio fuel, and space in output
        if(!emptyBucket.isEmpty() &&
                emptyBucket.getItem() == Items.BUCKET &&
                bioFuelLevel >= 1000 &&
                (currentOutput.isEmpty() || (currentOutput.getItem() == ModItems.BIO_FUEL_BUCKET.get() && currentOutput.getCount() < 64))) {

            // Convert 1000 mB bio fuel to 1 bio fuel bucket
            bioFuelLevel -= 1000;

            // Remove one empty bucket
            emptyBucket.shrink(1);
            this.itemHandler.setStackInSlot(EMPTY_BUCKET_SLOT, emptyBucket);

            // Add bio fuel bucket to output
            ItemStack bioFuelBucket = new ItemStack(ModItems.BIO_FUEL_BUCKET.get());
            if(currentOutput.isEmpty()) {
                this.itemHandler.setStackInSlot(BIO_FUEL_OUTPUT_SLOT, bioFuelBucket);
            } else {
                currentOutput.grow(1);
            }

            setChanged(); // Mark as changed so the GUI updates
        }
    }

    private boolean hasWater() {
        return waterLevel >= 10; // Need at least 10 mB for processing
    }

    private boolean hasEnergy() {
        return energyStorage.getEnergyStored() >= ENERGY_PER_TICK; // Need at least one tick's worth of energy
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