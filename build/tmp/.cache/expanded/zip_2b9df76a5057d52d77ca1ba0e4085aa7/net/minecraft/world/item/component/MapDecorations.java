package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecorations(Map<String, MapDecorations.Entry> f_315837_) {
    public static final MapDecorations f_317068_ = new MapDecorations(Map.of());
    public static final Codec<MapDecorations> f_315314_ = Codec.unboundedMap(Codec.STRING, MapDecorations.Entry.f_315452_)
        .xmap(MapDecorations::new, MapDecorations::f_315837_);

    public MapDecorations m_320603_(String p_327714_, MapDecorations.Entry p_334134_) {
        return new MapDecorations(Util.m_321632_(this.f_315837_, p_327714_, p_334134_));
    }

    public static record Entry(Holder<MapDecorationType> f_315480_, double f_316470_, double f_314992_, float f_315365_) {
        public static final Codec<MapDecorations.Entry> f_315452_ = RecordCodecBuilder.create(
            p_334294_ -> p_334294_.group(
                        MapDecorationType.f_316487_.fieldOf("type").forGetter(MapDecorations.Entry::f_315480_),
                        Codec.DOUBLE.fieldOf("x").forGetter(MapDecorations.Entry::f_316470_),
                        Codec.DOUBLE.fieldOf("z").forGetter(MapDecorations.Entry::f_314992_),
                        Codec.FLOAT.fieldOf("rotation").forGetter(MapDecorations.Entry::f_315365_)
                    )
                    .apply(p_334294_, MapDecorations.Entry::new)
        );
    }
}