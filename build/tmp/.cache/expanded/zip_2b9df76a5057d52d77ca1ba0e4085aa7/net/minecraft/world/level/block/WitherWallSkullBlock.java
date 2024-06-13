package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WitherWallSkullBlock extends WallSkullBlock {
    public static final MapCodec<WitherWallSkullBlock> f_302940_ = m_306223_(WitherWallSkullBlock::new);

    @Override
    public MapCodec<WitherWallSkullBlock> m_304657_() {
        return f_302940_;
    }

    public WitherWallSkullBlock(BlockBehaviour.Properties p_58276_) {
        super(SkullBlock.Types.WITHER_SKELETON, p_58276_);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        WitherSkullBlock.m_322429_(pLevel, pPos);
    }
}