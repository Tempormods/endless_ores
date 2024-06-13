package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CopperBulbBlock extends Block {
    public static final MapCodec<CopperBulbBlock> f_302558_ = m_306223_(CopperBulbBlock::new);
    public static final BooleanProperty f_303083_ = BlockStateProperties.POWERED;
    public static final BooleanProperty f_302965_ = BlockStateProperties.LIT;

    @Override
    protected MapCodec<? extends CopperBulbBlock> m_304657_() {
        return f_302558_;
    }

    public CopperBulbBlock(BlockBehaviour.Properties p_311115_) {
        super(p_311115_);
        this.registerDefaultState(this.defaultBlockState().setValue(f_302965_, Boolean.valueOf(false)).setValue(f_303083_, Boolean.valueOf(false)));
    }

    @Override
    protected void onPlace(BlockState p_309678_, Level p_311953_, BlockPos p_309986_, BlockState p_310956_, boolean p_311576_) {
        if (p_310956_.getBlock() != p_309678_.getBlock() && p_311953_ instanceof ServerLevel serverlevel) {
            this.m_304881_(p_309678_, serverlevel, p_309986_);
        }
    }

    @Override
    protected void neighborChanged(BlockState p_312656_, Level p_310732_, BlockPos p_312930_, Block p_310377_, BlockPos p_312667_, boolean p_310529_) {
        if (p_310732_ instanceof ServerLevel serverlevel) {
            this.m_304881_(p_312656_, serverlevel, p_312930_);
        }
    }

    public void m_304881_(BlockState p_309989_, ServerLevel p_310260_, BlockPos p_310537_) {
        boolean flag = p_310260_.hasNeighborSignal(p_310537_);
        if (flag != p_309989_.getValue(f_303083_)) {
            BlockState blockstate = p_309989_;
            if (!p_309989_.getValue(f_303083_)) {
                blockstate = p_309989_.cycle(f_302965_);
                p_310260_.playSound(null, p_310537_, blockstate.getValue(f_302965_) ? SoundEvents.f_303119_ : SoundEvents.f_302822_, SoundSource.BLOCKS);
            }

            p_310260_.setBlock(p_310537_, blockstate.setValue(f_303083_, Boolean.valueOf(flag)), 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_312159_) {
        p_312159_.add(f_302965_, f_303083_);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState p_313187_) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState p_311902_, Level p_311245_, BlockPos p_313180_) {
        return p_311245_.getBlockState(p_313180_).getValue(f_302965_) ? 15 : 0;
    }
}