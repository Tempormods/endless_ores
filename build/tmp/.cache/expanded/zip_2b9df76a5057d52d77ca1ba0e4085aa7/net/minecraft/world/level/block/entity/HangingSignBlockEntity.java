package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignBlockEntity extends SignBlockEntity {
    private static final int MAX_TEXT_LINE_WIDTH = 60;
    private static final int TEXT_LINE_HEIGHT = 9;

    public HangingSignBlockEntity(BlockPos p_250603_, BlockState p_251674_) {
        super(BlockEntityType.HANGING_SIGN, p_250603_, p_251674_);
    }

    @Override
    public int getTextLineHeight() {
        return 9;
    }

    @Override
    public int getMaxTextLineWidth() {
        return 60;
    }

    @Override
    public SoundEvent m_307050_() {
        return SoundEvents.f_303184_;
    }
}