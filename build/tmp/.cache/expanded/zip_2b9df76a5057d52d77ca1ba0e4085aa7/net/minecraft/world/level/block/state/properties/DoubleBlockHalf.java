package net.minecraft.world.level.block.state.properties;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum DoubleBlockHalf implements StringRepresentable {
    UPPER(Direction.DOWN),
    LOWER(Direction.UP);

    private final Direction f_303220_;

    private DoubleBlockHalf(final Direction p_312507_) {
        this.f_303220_ = p_312507_;
    }

    public Direction m_305637_() {
        return this.f_303220_;
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this == UPPER ? "upper" : "lower";
    }

    public DoubleBlockHalf m_305105_() {
        return this == UPPER ? LOWER : UPPER;
    }
}