package com.thewheatking.minecraftfarmertechmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.thewheatking.minecraftfarmertechmod.block.entity.EnergyCableBlockEntity;
import com.thewheatking.minecraftfarmertechmod.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Energy Cable Block - Transfers energy between machines
 * Has connecting states for visual cable connections
 */
public class EnergyCableBlock extends BaseEntityBlock {

    // Connection properties for each direction
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    // Cable core shape (center part)
    private static final VoxelShape CORE_SHAPE = Block.box(6, 6, 6, 10, 10, 10);

    // Connection shapes for each direction
    private static final VoxelShape NORTH_SHAPE = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SOUTH_SHAPE = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape UP_SHAPE = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape DOWN_SHAPE = Block.box(6, 0, 6, 10, 6, 10);

    public EnergyCableBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f, 3.0f)
                .sound(SoundType.METAL)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    // Temporarily remove codec method to test compilation
    // @Override
    // protected MapCodec<? extends BaseEntityBlock> codec() {
    //     return CODEC;
    // }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getConnectedState(pContext.getLevel(), pContext.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  net.minecraft.world.level.LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return getConnectedState(pLevel, pPos);
    }

    private BlockState getConnectedState(net.minecraft.world.level.LevelAccessor pLevel, BlockPos pPos) {
        return this.defaultBlockState()
                .setValue(NORTH, canConnect(pLevel, pPos, Direction.NORTH))
                .setValue(SOUTH, canConnect(pLevel, pPos, Direction.SOUTH))
                .setValue(EAST, canConnect(pLevel, pPos, Direction.EAST))
                .setValue(WEST, canConnect(pLevel, pPos, Direction.WEST))
                .setValue(UP, canConnect(pLevel, pPos, Direction.UP))
                .setValue(DOWN, canConnect(pLevel, pPos, Direction.DOWN));
    }

    private boolean canConnect(net.minecraft.world.level.LevelAccessor pLevel, BlockPos pPos, Direction pDirection) {
        BlockPos adjacentPos = pPos.relative(pDirection);
        BlockEntity adjacentEntity = pLevel.getBlockEntity(adjacentPos);

        if (adjacentEntity == null) return false;

        // Check if the adjacent block has energy capability
        if (pLevel instanceof Level level) {
            var energyCapability = level.getCapability(
                    com.thewheatking.minecraftfarmertechmod.energy.ModEnergyCapabilities.ENERGY,
                    adjacentPos,
                    pDirection.getOpposite()
            );
            return energyCapability != null;
        }

        return false;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = CORE_SHAPE;

        if (pState.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (pState.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (pState.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        if (pState.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);
        if (pState.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
        if (pState.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);

        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EnergyCableBlockEntity(pPos, pState);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntities.ENERGY_CABLE.get(),
                EnergyCableBlockEntity::tick);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}