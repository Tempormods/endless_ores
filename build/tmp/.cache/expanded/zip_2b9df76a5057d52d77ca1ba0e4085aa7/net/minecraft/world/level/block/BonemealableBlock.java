package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BonemealableBlock {
    boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState);

    boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState);

    void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState);

    default BlockPos m_319154_(BlockPos p_335812_) {
        return switch (this.m_319049_()) {
            case NEIGHBOR_SPREADER -> p_335812_.above();
            case GROWER -> p_335812_;
        };
    }

    default BonemealableBlock.Type m_319049_() {
        return BonemealableBlock.Type.GROWER;
    }

    public static enum Type {
        NEIGHBOR_SPREADER,
        GROWER;
    }
}