package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MapItemColor(int f_315574_) {
    public static final Codec<MapItemColor> f_316789_ = Codec.INT.xmap(MapItemColor::new, MapItemColor::f_315574_);
    public static final StreamCodec<ByteBuf, MapItemColor> f_316757_ = ByteBufCodecs.f_316612_.m_323038_(MapItemColor::new, MapItemColor::f_315574_);
    public static final MapItemColor f_314320_ = new MapItemColor(4603950);
}