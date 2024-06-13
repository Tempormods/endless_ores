package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class CoralFanBlock extends BaseCoralFanBlock {
    public static final MapCodec<CoralFanBlock> f_302270_ = RecordCodecBuilder.mapCodec(
        p_312088_ -> p_312088_.group(CoralBlock.f_303441_.forGetter(p_311032_ -> p_311032_.deadBlock), m_305607_()).apply(p_312088_, CoralFanBlock::new)
    );
    private final Block deadBlock;

    @Override
    public MapCodec<CoralFanBlock> m_304657_() {
        return f_302270_;
    }

    public CoralFanBlock(Block pDeadBlock, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.deadBlock = pDeadBlock;
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        this.tryScheduleDieTick(pState, pLevel, pPos);
    }

    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!scanForWater(pState, pLevel, pPos)) {
            pLevel.setBlock(pPos, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)), 2);
        }
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            this.tryScheduleDieTick(pState, pLevel, pCurrentPos);
            if (pState.getValue(WATERLOGGED)) {
                pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
            }

            return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }
}