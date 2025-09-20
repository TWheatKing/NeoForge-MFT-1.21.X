package com.thewheatking.minecraftfarmertechmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.thewheatking.minecraftfarmertechmod.attachment.ModAttachments;
import com.thewheatking.minecraftfarmertechmod.capability.PlayerWildflowerLuck;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WildFlowerBushBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<WildFlowerBushBlock> CODEC = simpleCodec(WildFlowerBushBlock::new);

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public WildFlowerBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        boolean isNotFullyGrown = age < MAX_AGE;

        if (!isNotFullyGrown && stack.is(Items.BONE_MEAL)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (age > 1) {
            // Harvest cotton swabs
            int cottonSwabCount = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(ModItems.COTTON_SWAB.get(), cottonSwabCount));

            // WILDFLOWER LUCK SYSTEM - Only for fully mature bushes (age 3)
            if (age == MAX_AGE && !level.isClientSide) {
                PlayerWildflowerLuck luckData = player.getData(ModAttachments.WILDFLOWER_LUCK);
                float currentChance = luckData.getCurrentChance();

                if (level.random.nextFloat() < currentChance) {
                    // SUCCESS! Drop wildflower and reset counter
                    popResource(level, pos, new ItemStack(ModItems.WILDFLOWER.get(), 1));
                    luckData.resetFailures();

                    // Optional: Send feedback to player
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§aYou found a wildflower! Luck reset."));
                } else {
                    // FAILURE! Increment counter
                    luckData.incrementFailures();
                    int failures = luckData.getFailureCount();
                    float nextChance = luckData.getCurrentChance();

                    // Optional: Send feedback to player
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§7No wildflower this time. Failures: " + failures + " (Next chance: " + (int)(nextChance * 100) + "%)"));
                }
            }

            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState newState = state.setValue(AGE, 1);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(AGE) == 0) {
            return SAPLING_SHAPE;
        } else {
            return MID_GROWTH_SHAPE;
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && random.nextInt(5) == 0 && level.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState newState = state.setValue(AGE, age + 1);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int newAge = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        level.setBlock(pos, state.setValue(AGE, newAge), 2);
    }
}