package com.thewheatking.minecraftfarmertechmod.item.custom;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = MinecraftFarmerTechMod.MOD_ID)
public class WrenchEventHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // Check if player is holding our wrench and is sneaking
        if (heldItem.getItem() instanceof WrenchItem && player.isShiftKeyDown()) {
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);

            if (!level.isClientSide) {
                BlockState newState = getRotatedState(state, event.getFace());
                if (newState != null && newState != state) {
                    level.setBlock(pos, newState, Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.2F);
                }
            }

            // Cancel the event to prevent block breaking
            event.setCanceled(true);
        }
    }

    private static BlockState getRotatedState(BlockState state, Direction clickedFace) {
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

        // 4. Stairs rotation
        else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) &&
                state.hasProperty(BlockStateProperties.HALF)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction newFacing = currentFacing.getClockWise();
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing);
        }

        return null; // Block cannot be rotated
    }

    private static Direction getNextDirection(Direction current) {
        // Cycle through all 6 directions
        return switch (current) {
            case DOWN -> Direction.UP;
            case UP -> Direction.NORTH;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.EAST;
            case EAST -> Direction.DOWN;
        };
    }

    private static Direction.Axis getNextAxis(Direction.Axis current) {
        // Cycle through axes
        return switch (current) {
            case X -> Direction.Axis.Y;
            case Y -> Direction.Axis.Z;
            case Z -> Direction.Axis.X;
        };
    }
}