package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MapId(int f_315413_) {
    public static final Codec<MapId> f_316792_ = Codec.INT.xmap(MapId::new, MapId::f_315413_);
    public static final StreamCodec<ByteBuf, MapId> f_315416_ = ByteBufCodecs.f_316730_.m_323038_(MapId::new, MapId::f_315413_);

    public String m_322142_() {
        return "map_" + this.f_315413_;
    }
}