package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum Pose {
    STANDING(0),
    FALL_FLYING(1),
    SLEEPING(2),
    SWIMMING(3),
    SPIN_ATTACK(4),
    CROUCHING(5),
    LONG_JUMPING(6),
    DYING(7),
    CROAKING(8),
    USING_TONGUE(9),
    SITTING(10),
    ROARING(11),
    SNIFFING(12),
    EMERGING(13),
    DIGGING(14),
    SLIDING(15),
    SHOOTING(16),
    INHALING(17);

    public static final IntFunction<Pose> f_315549_ = ByIdMap.continuous(Pose::m_320085_, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, Pose> f_315890_ = ByteBufCodecs.m_321301_(f_315549_, Pose::m_320085_);
    private final int f_314088_;

    private Pose(final int p_333317_) {
        this.f_314088_ = p_333317_;
    }

    public int m_320085_() {
        return this.f_314088_;
    }
}