package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<TrapDoorBlock> f_303469_ = RecordCodecBuilder.mapCodec(
        p_309899_ -> p_309899_.group(BlockSetType.f_303023_.fieldOf("block_set_type").forGetter(p_311609_ -> p_311609_.type), m_305607_())
                .apply(p_309899_, TrapDoorBlock::new)
    );
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final int AABB_THICKNESS = 3;
    protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    private final BlockSetType type;

    @Override
    public MapCodec<? extends TrapDoorBlock> m_304657_() {
        return f_303469_;
    }

    public TrapDoorBlock(BlockSetType pType, BlockBehaviour.Properties pProperties) {
        super(pProperties.sound(pType.soundType()));
        this.type = pType;
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.valueOf(false))
                .setValue(HALF, Half.BOTTOM)
                .setValue(POWERED, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
        );
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (!pState.getValue(OPEN)) {
            return pState.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
        } else {
            switch ((Direction)pState.getValue(FACING)) {
                case NORTH:
                default:
                    return NORTH_OPEN_AABB;
                case SOUTH:
                    return SOUTH_OPEN_AABB;
                case WEST:
                    return WEST_OPEN_AABB;
                case EAST:
                    return EAST_OPEN_AABB;
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pType) {
        switch (pType) {
            case LAND:
                return pState.getValue(OPEN);
            case WATER:
                return pState.getValue(WATERLOGGED);
            case AIR:
                return pState.getValue(OPEN);
            default:
                return false;
        }
    }

    @Override
    protected InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!this.type.canOpenByHand()) {
            return InteractionResult.PASS;
        } else {
            this.m_305890_(pState, pLevel, pPos, pPlayer);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
    }

    @Override
    protected void m_304760_(BlockState p_312876_, Level p_312713_, BlockPos p_312697_, Explosion p_312889_, BiConsumer<ItemStack, BlockPos> p_312223_) {
        if (p_312889_.m_307824_() == Explosion.BlockInteraction.TRIGGER_BLOCK
            && !p_312713_.isClientSide()
            && this.type.f_303181_()
            && !p_312876_.getValue(POWERED)) {
            this.m_305890_(p_312876_, p_312713_, p_312697_, null);
        }

        super.m_304760_(p_312876_, p_312713_, p_312697_, p_312889_, p_312223_);
    }

    private void m_305890_(BlockState p_311901_, Level p_312039_, BlockPos p_310194_, @Nullable Player p_312003_) {
        BlockState blockstate = p_311901_.cycle(OPEN);
        p_312039_.setBlock(p_310194_, blockstate, 2);
        if (blockstate.getValue(WATERLOGGED)) {
            p_312039_.scheduleTick(p_310194_, Fluids.WATER, Fluids.WATER.getTickDelay(p_312039_));
        }

        this.playSound(p_312003_, p_312039_, p_310194_, blockstate.getValue(OPEN));
    }

    protected void playSound(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, boolean pIsOpened) {
        pLevel.playSound(
            pPlayer,
            pPos,
            pIsOpened ? this.type.trapdoorOpen() : this.type.trapdoorClose(),
            SoundSource.BLOCKS,
            1.0F,
            pLevel.getRandom().nextFloat() * 0.1F + 0.9F
        );
        pLevel.gameEvent(pPlayer, pIsOpened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pPos);
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            boolean flag = pLevel.hasNeighborSignal(pPos);
            if (flag != pState.getValue(POWERED)) {
                if (pState.getValue(OPEN) != flag) {
                    pState = pState.setValue(OPEN, Boolean.valueOf(flag));
                    this.playSound(null, pLevel, pPos, flag);
                }

                pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(flag)), 2);
                if (pState.getValue(WATERLOGGED)) {
                    pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState();
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        Direction direction = pContext.getClickedFace();
        if (!pContext.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
            blockstate = blockstate.setValue(FACING, direction)
                .setValue(HALF, pContext.getClickLocation().y - (double)pContext.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
        } else {
            blockstate = blockstate.setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
        }

        if (pContext.getLevel().hasNeighborSignal(pContext.getClickedPos())) {
            blockstate = blockstate.setValue(OPEN, Boolean.valueOf(true)).setValue(POWERED, Boolean.valueOf(true));
        }

        return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    protected BlockSetType m_306287_() {
        return this.type;
    }

    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
        if (state.getValue(OPEN)) {
            BlockPos downPos = pos.below();
            BlockState down = world.getBlockState(downPos);
            return down.getBlock().makesOpenTrapdoorAboveClimbable(down, world, downPos, state);
        }
        return false;
    }
}