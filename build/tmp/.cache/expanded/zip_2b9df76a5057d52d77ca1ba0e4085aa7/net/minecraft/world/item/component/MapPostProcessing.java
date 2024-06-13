package net.minecraft.world.item.component;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum MapPostProcessing {
    LOCK(0),
    SCALE(1);

    public static final IntFunction<MapPostProcessing> f_314180_ = ByIdMap.continuous(MapPostProcessing::m_319925_, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, MapPostProcessing> f_315810_ = ByteBufCodecs.m_321301_(f_314180_, MapPostProcessing::m_319925_);
    private final int f_314424_;

    private MapPostProcessing(final int p_331501_) {
        this.f_314424_ = p_331501_;
    }

    public int m_319925_() {
        return this.f_314424_;
    }
}