package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTransparentBlock extends Block {
    public static final MapCodec<HalfTransparentBlock> f_302777_ = m_306223_(HalfTransparentBlock::new);

    @Override
    protected MapCodec<? extends HalfTransparentBlock> m_304657_() {
        return f_302777_;
    }

    public HalfTransparentBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
        return pAdjacentBlockState.is(this) ? true : super.skipRendering(pState, pAdjacentBlockState, pSide);
    }
}