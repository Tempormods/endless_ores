package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightBlock extends Block implements SimpleWaterloggedBlock {
    public static final MapCodec<LightBlock> f_302767_ = m_306223_(LightBlock::new);
    public static final int MAX_LEVEL = 15;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = p_153701_ -> p_153701_.getValue(LEVEL);

    @Override
    public MapCodec<LightBlock> m_304657_() {
        return f_302767_;
    }

    public LightBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(15)).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL, WATERLOGGED);
    }

    @Override
    protected InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pPlayer.canUseGameMasterBlocks()) {
            pLevel.setBlock(pPos, pState.cycle(LEVEL), 2);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pContext.isHoldingItem(Items.LIGHT) ? Shapes.block() : Shapes.empty();
    }

    @Override
    protected boolean m_49099_(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getFluidState().isEmpty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    protected BlockState updateShape(
        BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos
    ) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader p_310734_, BlockPos pPos, BlockState pState) {
        return setLightOnStack(super.getCloneItemStack(p_310734_, pPos, pState), pState.getValue(LEVEL));
    }

    public static ItemStack setLightOnStack(ItemStack pStack, int pLight) {
        if (pLight != 15) {
            pStack.m_322496_(DataComponents.f_315479_, BlockItemStateProperties.f_314882_.m_323763_(LEVEL, pLight));
        }

        return pStack;
    }
}