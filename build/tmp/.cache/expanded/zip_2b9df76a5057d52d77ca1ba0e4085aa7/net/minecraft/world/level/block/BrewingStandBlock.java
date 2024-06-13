package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrewingStandBlock extends BaseEntityBlock {
    public static final MapCodec<BrewingStandBlock> f_302408_ = m_306223_(BrewingStandBlock::new);
    public static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{
        BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2
    };
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0));

    @Override
    public MapCodec<BrewingStandBlock> m_304657_() {
        return f_302408_;
    }

    public BrewingStandBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(HAS_BOTTLE[0], Boolean.valueOf(false))
                .setValue(HAS_BOTTLE[1], Boolean.valueOf(false))
                .setValue(HAS_BOTTLE[2], Boolean.valueOf(false))
        );
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BrewingStandBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::serverTick);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof BrewingStandBlockEntity) {
                pPlayer.openMenu((BrewingStandBlockEntity)blockentity);
                pPlayer.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = (double)pPos.getX() + 0.4 + (double)pRandom.nextFloat() * 0.2;
        double d1 = (double)pPos.getY() + 0.7 + (double)pRandom.nextFloat() * 0.3;
        double d2 = (double)pPos.getZ() + 0.4 + (double)pRandom.nextFloat() * 0.2;
        pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        Containers.m_307148_(pState, pNewState, pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
    }

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}