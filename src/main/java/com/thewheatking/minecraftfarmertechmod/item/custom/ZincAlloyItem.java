package com.thewheatking.minecraftfarmertechmod.item.custom;

import com.thewheatking.minecraftfarmertechmod.block.ModBlocks;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ZincAlloyItem extends Item {

    public ZincAlloyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        ItemStack itemStack = pContext.getItemInHand();

        // Check if the clicked block is a stripped log
        if (isStrippedLog(blockState.getBlock())) {
            if (!level.isClientSide()) {
                // Replace the stripped log with zinc_casing
                level.setBlock(pos, ModBlocks.ZINC_CASING.get().defaultBlockState(), 3);

                // Play sound effect
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                // Consume the zinc alloy item (unless player is in creative mode)
                // This removes 1 zinc_alloy from the player's hand
                if (player != null && !player.getAbilities().instabuild) {
                    itemStack.shrink(1); // This line consumes/removes the item
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useOn(pContext);
    }

    private boolean isStrippedLog(Block block) {
        // Check if the block is any type of stripped log
        return block == Blocks.STRIPPED_OAK_LOG ||
                block == Blocks.STRIPPED_SPRUCE_LOG ||
                block == Blocks.STRIPPED_BIRCH_LOG ||
                block == Blocks.STRIPPED_JUNGLE_LOG ||
                block == Blocks.STRIPPED_ACACIA_LOG ||
                block == Blocks.STRIPPED_DARK_OAK_LOG ||
                block == Blocks.STRIPPED_MANGROVE_LOG ||
                block == Blocks.STRIPPED_CHERRY_LOG ||
                block == Blocks.STRIPPED_CRIMSON_STEM ||
                block == Blocks.STRIPPED_WARPED_STEM;
    }
}