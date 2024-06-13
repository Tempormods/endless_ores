package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TintedGlassBlock extends TransparentBlock {
    public static final MapCodec<TintedGlassBlock> f_303583_ = m_306223_(TintedGlassBlock::new);

    @Override
    public MapCodec<TintedGlassBlock> m_304657_() {
        return f_303583_;
    }

    public TintedGlassBlock(BlockBehaviour.Properties p_154822_) {
        super(p_154822_);
    }

    @Override
    protected boolean m_49099_(BlockState p_154824_, BlockGetter p_154825_, BlockPos p_154826_) {
        return false;
    }

    @Override
    protected int getLightBlock(BlockState p_154828_, BlockGetter p_154829_, BlockPos p_154830_) {
        return p_154829_.getMaxLightLevel();
    }
}