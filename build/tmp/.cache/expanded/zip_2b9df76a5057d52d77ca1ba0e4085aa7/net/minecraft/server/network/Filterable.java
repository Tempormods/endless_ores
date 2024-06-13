package net.minecraft.server.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Filterable<T>(T f_315590_, Optional<T> f_316433_) {
    public static <T> Codec<Filterable<T>> m_322486_(Codec<T> p_327990_) {
        Codec<Filterable<T>> codec = RecordCodecBuilder.create(
            p_328042_ -> p_328042_.group(
                        p_327990_.fieldOf("raw").forGetter(Filterable::f_315590_), p_327990_.optionalFieldOf("filtered").forGetter(Filterable::f_316433_)
                    )
                    .apply(p_328042_, Filterable::new)
        );
        Codec<Filterable<T>> codec1 = p_327990_.xmap(Filterable::m_323001_, Filterable::f_315590_);
        return Codec.withAlternative(codec, codec1);
    }

    public static <B extends ByteBuf, T> StreamCodec<B, Filterable<T>> m_323964_(StreamCodec<B, T> p_328361_) {
        return StreamCodec.m_320349_(p_328361_, Filterable::f_315590_, p_328361_.m_321801_(ByteBufCodecs::m_319027_), Filterable::f_316433_, Filterable::new);
    }

    public static <T> Filterable<T> m_323001_(T p_333360_) {
        return new Filterable<>(p_333360_, Optional.empty());
    }

    public static Filterable<String> m_320455_(FilteredText p_332002_) {
        return new Filterable<>(p_332002_.raw(), p_332002_.isFiltered() ? Optional.of(p_332002_.filteredOrEmpty()) : Optional.empty());
    }

    public T m_323302_(boolean p_335502_) {
        return p_335502_ ? this.f_316433_.orElse(this.f_315590_) : this.f_315590_;
    }

    public <U> Filterable<U> m_321832_(Function<T, U> p_328140_) {
        return new Filterable<>(p_328140_.apply(this.f_315590_), this.f_316433_.map(p_328140_));
    }

    public <U> Optional<Filterable<U>> m_320562_(Function<T, Optional<U>> p_335887_) {
        Optional<U> optional = p_335887_.apply(this.f_315590_);
        if (optional.isEmpty()) {
            return Optional.empty();
        } else if (this.f_316433_.isPresent()) {
            Optional<U> optional1 = p_335887_.apply(this.f_316433_.get());
            return optional1.isEmpty() ? Optional.empty() : Optional.of(new Filterable<>(optional.get(), optional1));
        } else {
            return Optional.of(new Filterable<>(optional.get(), Optional.empty()));
        }
    }
}