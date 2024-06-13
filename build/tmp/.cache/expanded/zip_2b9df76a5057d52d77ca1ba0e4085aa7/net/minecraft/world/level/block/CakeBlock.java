package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CakeBlock extends Block {
    public static final MapCodec<CakeBlock> f_303361_ = m_306223_(CakeBlock::new);
    public static final int MAX_BITES = 6;
    public static final IntegerProperty BITES = BlockStateProperties.BITES;
    public static final int FULL_CAKE_SIGNAL = getOutputSignal(0);
    protected static final float AABB_OFFSET = 1.0F;
    protected static final float AABB_SIZE_PER_BITE = 2.0F;
    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{
        Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
        Block.box(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
        Block.box(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
        Block.box(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
        Block.box(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
        Block.box(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
        Block.box(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
    };

    @Override
    public MapCodec<CakeBlock> m_304657_() {
        return f_303361_;
    }

    public CakeBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, Integer.valueOf(0)));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_BITE[pState.getValue(BITES)];
    }

    @Override
    protected ItemInteractionResult m_51273_(
        ItemStack p_332983_, BlockState p_333266_, Level p_328017_, BlockPos p_332811_, Player p_327926_, InteractionHand p_330281_, BlockHitResult p_332277_
    ) {
        Item item = p_332983_.getItem();
        if (p_332983_.is(ItemTags.CANDLES) && p_333266_.getValue(BITES) == 0 && Block.byItem(item) instanceof CandleBlock candleblock) {
            if (!p_327926_.isCreative()) {
                p_332983_.shrink(1);
            }

            p_328017_.playSound(null, p_332811_, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            p_328017_.setBlockAndUpdate(p_332811_, CandleCakeBlock.byCandle(candleblock));
            p_328017_.gameEvent(p_327926_, GameEvent.BLOCK_CHANGE, p_332811_);
            p_327926_.awardStat(Stats.ITEM_USED.get(item));
            return ItemInteractionResult.SUCCESS;
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected InteractionResult use(BlockState p_331745_, Level p_334119_, BlockPos p_330552_, Player p_332095_, BlockHitResult p_329702_) {
        if (p_334119_.isClientSide) {
            if (eat(p_334119_, p_330552_, p_331745_, p_332095_).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (p_332095_.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(p_334119_, p_330552_, p_331745_, p_332095_);
    }

    protected static InteractionResult eat(LevelAccessor pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pPlayer.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            pPlayer.awardStat(Stats.EAT_CAKE_SLICE);
            pPlayer.getFoodData().eat(2, 0.1F);
            int i = pState.getValue(BITES);
            pLevel.gameEvent(pPlayer, GameEvent.EAT, pPos);
            if (i < 6) {
                pLevel.setBlock(pPos, pState.setValue(BITES, Integer.valueOf(i + 1)), 3);
            } else {
                pLevel.removeBlock(pPos, false);
                pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DESTROY, pPos);
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BITES);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return getOutputSignal(pBlockState.getValue(BITES));
    }

    public static int getOutputSignal(int pEaten) {
        return (7 - pEaten) * 2;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}