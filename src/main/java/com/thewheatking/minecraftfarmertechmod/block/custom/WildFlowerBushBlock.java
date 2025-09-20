// Updated WildFlowerBushBlock.java with better bonemeal support

package com.thewheatking.minecraftfarmertechmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WildFlowerBushBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<WildFlowerBushBlock> CODEC = simpleCodec(WildFlowerBushBlock::new);

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    // TEMPORARY: Simple in-memory storage for testing
    private static final Map<UUID, Integer> PLAYER_FAILURES = new HashMap<>();
    private static final float BASE_CHANCE = 0.1f; // 10%
    private static final float BONUS_PER_FAILURE = 0.05f; // 5% per failure

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

        // Debug message
        if (!level.isClientSide) {
            player.sendSystemMessage(Component.literal("§e[DEBUG] Bush age: " + age + ", clicking with: " + stack.getDisplayName().getString()));
        }

        if (!isNotFullyGrown && stack.is(Items.BONE_MEAL)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (age > 1) {
            // Harvest cotton swabs
            int cottonSwabCount = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(ModItems.COTTON_SWAB.get(), cottonSwabCount));

            if (!level.isClientSide) {
                player.sendSystemMessage(Component.literal("§a[DEBUG] Harvested " + cottonSwabCount + " cotton swabs"));
            }

            // WILDFLOWER LUCK SYSTEM - Only for fully mature bushes (age 3)
            if (age == MAX_AGE && !level.isClientSide) {
                tryWildflowerDrop(level, pos, player, "manual harvest");
            }

            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState newState = state.setValue(AGE, 1);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.literal("§c[DEBUG] Bush not mature enough (age " + age + ")"));
            }
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
    }

    // Helper method for wildflower luck system
    private void tryWildflowerDrop(Level level, BlockPos pos, Player player, String trigger) {
        UUID playerId = player.getUUID();
        int failures = PLAYER_FAILURES.getOrDefault(playerId, 0);
        float currentChance = Math.min(1.0f, BASE_CHANCE + (failures * BONUS_PER_FAILURE));

        player.sendSystemMessage(Component.literal("§e[DEBUG] Trying wildflower drop via " + trigger + ". Current chance: " + (int)(currentChance * 100) + "%"));

        if (level.random.nextFloat() < currentChance) {
            // SUCCESS! Drop wildflower and reset counter
            popResource(level, pos, new ItemStack(ModItems.WILDFLOWER.get(), 1));
            PLAYER_FAILURES.put(playerId, 0);

            player.sendSystemMessage(Component.literal("§a★ SUCCESS! You found a wildflower! Luck reset. ★"));
        } else {
            // FAILURE! Increment counter
            int newFailures = failures + 1;
            PLAYER_FAILURES.put(playerId, newFailures);
            float nextChance = Math.min(1.0f, BASE_CHANCE + (newFailures * BONUS_PER_FAILURE));

            player.sendSystemMessage(Component.literal("§7No wildflower this time. Failures: " + newFailures + " (Next chance: " + (int)(nextChance * 100) + "%)"));
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
        int currentAge = state.getValue(AGE);
        int newAge = Math.min(MAX_AGE, currentAge + 1);
        BlockState newState = state.setValue(AGE, newAge);
        level.setBlock(pos, newState, 2);

        // Debug message
        System.out.println("[DEBUG] Bonemealed bush from age " + currentAge + " to " + newAge);

        // IMPROVED: Check for wildflower drop whenever reaching full maturity via bonemeal
        if (newAge == MAX_AGE) {
            // Find the player who used bonemeal
            Player nearestPlayer = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false);
            if (nearestPlayer != null) {
                nearestPlayer.sendSystemMessage(Component.literal("§e[DEBUG] Bonemeal reached maturity, checking for wildflower..."));

                // FIXED: Always trigger wildflower luck when reaching maturity, regardless of previous age
                // This allows for automated farming with dispensers
                if (random.nextFloat() < 0.5f) { // 50% chance to trigger luck system
                    tryWildflowerDrop(level, pos, nearestPlayer, "bonemeal to maturity");
                } else {
                    nearestPlayer.sendSystemMessage(Component.literal("§7[DEBUG] Bonemeal wildflower check failed (50% chance)"));
                }
            } else {
                System.out.println("[DEBUG] No player found near bonemealed bush - wildflower system needs a player");
            }
        }
    }
}

// ALTERNATIVE APPROACH FOR AUTOMATED FARMING:
// If you want to make it work with dispensers/automation without requiring a nearby player,
// you could modify the system to:
// 1. Store the luck data per block position instead of per player
// 2. Have a global luck progression that works for any harvesting method
//
// Here's a snippet for that approach:

/*
// Per-block luck storage (instead of per-player)
private static final Map<BlockPos, Integer> BLOCK_FAILURES = new HashMap<>();

private void tryAutomatedWildflowerDrop(Level level, BlockPos pos, String trigger) {
    int failures = BLOCK_FAILURES.getOrDefault(pos, 0);
    float currentChance = Math.min(1.0f, BASE_CHANCE + (failures * BONUS_PER_FAILURE));

    System.out.println("[DEBUG] Automated wildflower drop via " + trigger + ". Current chance: " + (int)(currentChance * 100) + "%");

    if (level.random.nextFloat() < currentChance) {
        // SUCCESS! Drop wildflower and reset counter
        popResource(level, pos, new ItemStack(ModItems.WILDFLOWER.get(), 1));
        BLOCK_FAILURES.put(pos, 0);
        System.out.println("[DEBUG] Automated wildflower success! Luck reset.");
    } else {
        // FAILURE! Increment counter
        int newFailures = failures + 1;
        BLOCK_FAILURES.put(pos, newFailures);
        System.out.println("[DEBUG] No automated wildflower. Failures: " + newFailures);
    }
}
*/