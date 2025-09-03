package com.thewheatking.minecraftfarmertechmod.block.entity;

import com.thewheatking.minecraftfarmertechmod.block.custom.IronFurnaceBlock;
import com.thewheatking.minecraftfarmertechmod.screen.IronFurnaceMenu;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IronFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> isValidInput(stack); // Input slot
                case 1 -> isFuel(stack); // Fuel slot
                case 2, 3 -> false; // Output slots - no manual insertion
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 50; // Four Times as fast as regular furnace (200 -> 50)
    private int fuelTime = 0;
    private int maxFuelTime = 0;

    public IronFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.IRON_FURNACE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> IronFurnaceBlockEntity.this.progress;
                    case 1 -> IronFurnaceBlockEntity.this.maxProgress;
                    case 2 -> IronFurnaceBlockEntity.this.fuelTime;
                    case 3 -> IronFurnaceBlockEntity.this.maxFuelTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> IronFurnaceBlockEntity.this.progress = pValue;
                    case 1 -> IronFurnaceBlockEntity.this.maxProgress = pValue;
                    case 2 -> IronFurnaceBlockEntity.this.fuelTime = pValue;
                    case 3 -> IronFurnaceBlockEntity.this.maxFuelTime = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
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
        return Component.translatable("block.minecraftfarmertechmod.iron_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new IronFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("iron_furnace.progress", progress);
        pTag.putInt("iron_furnace.fuel_time", fuelTime);
        pTag.putInt("iron_furnace.max_fuel_time", maxFuelTime);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("iron_furnace.progress");
        fuelTime = pTag.getInt("iron_furnace.fuel_time");
        maxFuelTime = pTag.getInt("iron_furnace.max_fuel_time");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe() && hasFuel()) {
            increaseCraftingProcess();
            setChanged(pLevel, pPos, pState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }

        if (hasFuel()) {
            decreaseFuelTime();
        }

        BlockState blockState = pState.setValue(IronFurnaceBlock.LIT, this.fuelTime > 0);
        if (!pState.equals(blockState)) {
            pLevel.setBlock(pPos, blockState, 3);
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<SmeltingRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);

        // Try to add to first output slot
        if (itemHandler.getStackInSlot(2).isEmpty()) {
            itemHandler.setStackInSlot(2, new ItemStack(result.getItem(), result.getCount()));
        } else if (itemHandler.getStackInSlot(2).getItem() == result.getItem() &&
                itemHandler.getStackInSlot(2).getCount() + result.getCount() <= itemHandler.getStackInSlot(2).getMaxStackSize()) {
            itemHandler.getStackInSlot(2).grow(result.getCount());
        }
        // Try second output slot if first is full
        else if (itemHandler.getStackInSlot(3).isEmpty()) {
            itemHandler.setStackInSlot(3, new ItemStack(result.getItem(), result.getCount()));
        } else if (itemHandler.getStackInSlot(3).getItem() == result.getItem() &&
                itemHandler.getStackInSlot(3).getCount() + result.getCount() <= itemHandler.getStackInSlot(3).getMaxStackSize()) {
            itemHandler.getStackInSlot(3).grow(result.getCount());
        }

        itemHandler.extractItem(0, 1, false);
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProcess() {
        progress++;
    }

    private void decreaseFuelTime() {
        fuelTime--;
        if (fuelTime <= 0 && hasRecipe()) {
            consumeFuel();
        }
    }

    private void consumeFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(1);
        if (!fuelStack.isEmpty()) {
            int burnTime = getBurnTime(fuelStack.getItem());
            if (burnTime > 0) {
                fuelTime = burnTime;
                maxFuelTime = burnTime;
                itemHandler.extractItem(1, 1, false);
            }
        }
    }

    private boolean hasFuel() {
        return fuelTime > 0 || (!itemHandler.getStackInSlot(1).isEmpty() && getBurnTime(itemHandler.getStackInSlot(1).getItem()) > 0);
    }

    private boolean hasRecipe() {
        Optional<SmeltingRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(null);
        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<SmeltingRecipe> getCurrentRecipe() {
        SingleRecipeInput recipeInput = new SingleRecipeInput(itemHandler.getStackInSlot(0));
        return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInput, level)
                .map(holder -> holder.value());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return (itemHandler.getStackInSlot(2).isEmpty() || itemHandler.getStackInSlot(2).getItem() == item) ||
                (itemHandler.getStackInSlot(3).isEmpty() || itemHandler.getStackInSlot(3).getItem() == item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int slot2Available = itemHandler.getStackInSlot(2).isEmpty() ? 64 : (64 - itemHandler.getStackInSlot(2).getCount());
        int slot3Available = itemHandler.getStackInSlot(3).isEmpty() ? 64 : (64 - itemHandler.getStackInSlot(3).getCount());
        return (slot2Available + slot3Available) >= count;
    }

    private boolean isValidInput(ItemStack stack) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);
        return this.level != null && this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInput, level).isPresent();
    }

    private boolean isFuel(ItemStack stack) {
        return getBurnTime(stack.getItem()) > 0;
    }

    private int getBurnTime(Item item) {
        if (item == Items.COAL || item == Items.CHARCOAL) return 1600;
        if (item == Items.COAL_BLOCK) return 16000;
        if (item == Items.LAVA_BUCKET) return 20000;
        if (item == Items.STICK) return 100;
        if (item == Items.WOODEN_PICKAXE || item == Items.WOODEN_AXE || item == Items.WOODEN_SHOVEL || item == Items.WOODEN_HOE || item == Items.WOODEN_SWORD) return 200;
        return 0;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}