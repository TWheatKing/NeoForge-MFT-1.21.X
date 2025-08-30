package com.thewheatking.minecraftfarmertechmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Check if player is sneaking (shift-clicking) to dismantle
        if (player != null && player.isShiftKeyDown()) {
            return handleDismantleBlock(level, pos, state, player);
        } else {
            return handleRotateBlock(level, pos, state, context);
        }
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        // Never break blocks with the wrench
        return false;
    }

    private InteractionResult handleDismantleBlock(Level level, BlockPos pos, BlockState state, Player player) {
        Block block = state.getBlock();

        // Don't allow dismantling of certain blocks
        if (isBlockDismantlable(state) && isPlayerPlacedBlock(state)){
            // Get the drops for this block
            List<ItemStack> drops = Block.getDrops(state, level.getServer().overworld(), pos, level.getBlockEntity(pos));

            // Remove the block
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));

            // Play break sound
            level.playSound(null, pos, state.getSoundType(level, pos, player).getBreakSound(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);

            // Give items to player or drop them
            for (ItemStack drop : drops) {
                if (!player.getInventory().add(drop)) {
                    // Drop item if inventory is full
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                    level.addFreshEntity(itemEntity);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
    private boolean isPlayerPlacedBlock(BlockState state) {
        Block block = state.getBlock();

        // Only allow dismantling of clearly crafted/manufactured blocks
        // Exclude all natural blocks like ores, logs, stone, etc.
        return block == Blocks.FURNACE ||
                block == Blocks.BLAST_FURNACE ||
                block == Blocks.SMOKER ||
                block == Blocks.CHEST ||
                block == Blocks.BARREL ||
                block == Blocks.CRAFTING_TABLE ||
                block == Blocks.DISPENSER ||
                block == Blocks.DROPPER ||
                block == Blocks.HOPPER ||
                block == Blocks.PISTON ||
                block == Blocks.STICKY_PISTON ||
                block == Blocks.OBSERVER ||
                block == Blocks.REDSTONE_LAMP ||
                block == Blocks.REDSTONE_BLOCK ||
                block == Blocks.TARGET ||
                block == Blocks.LECTERN ||
                block == Blocks.ENCHANTING_TABLE ||
                block == Blocks.ANVIL ||
                block == Blocks.CHIPPED_ANVIL ||
                block == Blocks.DAMAGED_ANVIL ||
                // Add more crafted blocks as needed
                // Stairs, slabs, and other crafted building blocks
                block.getName().getString().contains("stairs") ||
                block.getName().getString().contains("slab") ||
                block.getName().getString().contains("wall") ||
                block.getName().getString().contains("fence");
    }
    private InteractionResult handleRotateBlock(Level level, BlockPos pos, BlockState state, UseOnContext context) {
        BlockState newState = getRotatedState(state, context.getClickedFace());

        if (newState != null && newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);

            // Play sound effect
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.2F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean isBlockDismantlable(BlockState state) {
        Block block = state.getBlock();

        // Allow dismantling most blocks, but exclude certain ones
        return block != Blocks.BEDROCK &&
                block != Blocks.END_PORTAL_FRAME &&
                block != Blocks.END_PORTAL &&
                block != Blocks.NETHER_PORTAL &&
                block != Blocks.SPAWNER &&
                block != Blocks.BARRIER &&
                block != Blocks.COMMAND_BLOCK &&
                block != Blocks.CHAIN_COMMAND_BLOCK &&
                block != Blocks.REPEATING_COMMAND_BLOCK &&
                block != Blocks.STRUCTURE_BLOCK &&
                block != Blocks.JIGSAW &&
                !state.isAir();
    }

    private BlockState getRotatedState(BlockState state, Direction clickedFace) {
        Block block = state.getBlock();

        // Handle different types of rotatable blocks

        // 1. Horizontal directional blocks (furnaces, chests, etc.)
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction newFacing = currentFacing.getClockWise();
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing);
        }

        // 2. Full directional blocks (dispensers, droppers, pistons, etc.)
        else if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction currentFacing = state.getValue(BlockStateProperties.FACING);
            Direction newFacing;

            // Rotate based on which face was clicked
            if (clickedFace.getAxis() == Direction.Axis.Y) {
                // Clicked top/bottom - rotate horizontally
                if (currentFacing.getAxis() == Direction.Axis.Y) {
                    newFacing = Direction.NORTH; // Start with north if currently vertical
                } else {
                    newFacing = currentFacing.getClockWise();
                }
            } else {
                // Clicked side - cycle through all 6 directions
                newFacing = getNextDirection(currentFacing);
            }

            return state.setValue(BlockStateProperties.FACING, newFacing);
        }

        // 3. Pillar blocks (logs, pillars, etc.)
        else if (state.hasProperty(BlockStateProperties.AXIS)) {
            Direction.Axis currentAxis = state.getValue(BlockStateProperties.AXIS);
            Direction.Axis newAxis = getNextAxis(currentAxis);
            return state.setValue(BlockStateProperties.AXIS, newAxis);
        }

        // 4. Face attached blocks (buttons, levers, etc.)
        else if (state.hasProperty(BlockStateProperties.ATTACH_FACE) &&
                state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            AttachFace attachFace = state.getValue(BlockStateProperties.ATTACH_FACE);
            Direction horizontalFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            // Rotate the horizontal facing
            Direction newHorizontalFacing = horizontalFacing.getClockWise();
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING, newHorizontalFacing);
        }

        // 5. Stairs rotation
        else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) &&
                state.hasProperty(BlockStateProperties.HALF)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction newFacing = currentFacing.getClockWise();
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing);
        }

        // Add more block types as needed

        return null; // Block cannot be rotated
    }

    private Direction getNextDirection(Direction current) {
        // Cycle through all 6 directions: DOWN -> UP -> NORTH -> SOUTH -> WEST -> EAST -> DOWN...
        return switch (current) {
            case DOWN -> Direction.UP;
            case UP -> Direction.NORTH;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.EAST;
            case EAST -> Direction.DOWN;
        };
    }

    private Direction.Axis getNextAxis(Direction.Axis current) {
        // Cycle through axes: X -> Y -> Z -> X...
        return switch (current) {
            case X -> Direction.Axis.Y;
            case Y -> Direction.Axis.Z;
            case Z -> Direction.Axis.X;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.minecraftfarmertechmod.wrench.line1"));
        tooltipComponents.add(Component.translatable("tooltip.minecraftfarmertechmod.wrench.line2"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        // Wrench doesn't need repairing since it has no durability
        return false;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        // Wrench cannot be damaged
        return false;
    }
}