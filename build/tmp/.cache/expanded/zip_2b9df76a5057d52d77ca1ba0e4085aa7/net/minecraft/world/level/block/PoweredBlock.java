package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredBlock extends Block {
    public static final MapCodec<PoweredBlock> f_302647_ = m_306223_(PoweredBlock::new);

    @Override
    public MapCodec<PoweredBlock> m_304657_() {
        return f_302647_;
    }

    public PoweredBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    protected int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return 15;
    }
}