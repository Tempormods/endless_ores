package net.minecraft.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.Tag;

public class EncoderCache {
    final LoadingCache<EncoderCache.Key<?, ?>, DataResult<?>> f_316677_;

    public EncoderCache(int p_328135_) {
        this.f_316677_ = CacheBuilder.newBuilder()
            .maximumSize((long)p_328135_)
            .concurrencyLevel(1)
            .softValues()
            .build(new CacheLoader<EncoderCache.Key<?, ?>, DataResult<?>>() {
                public DataResult<?> load(EncoderCache.Key<?, ?> p_334212_) {
                    return p_334212_.m_320909_();
                }
            });
    }

    public <A> Codec<A> m_321472_(final Codec<A> p_332774_) {
        return new Codec<A>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> p_335845_, T p_329817_) {
                return p_332774_.decode(p_335845_, p_329817_);
            }

            @Override
            public <T> DataResult<T> encode(A p_328409_, DynamicOps<T> p_330058_, T p_328392_) {
                return (DataResult<T>) EncoderCache.this.f_316677_
                    .getUnchecked(new EncoderCache.Key<>(p_332774_, p_328409_, p_330058_))
                    .map(p_336406_ -> p_336406_ instanceof Tag tag ? tag.copy() : p_336406_);
            }
        };
    }

    static record Key<A, T>(Codec<A> f_315841_, A f_315763_, DynamicOps<T> f_313909_) {
        public DataResult<T> m_320909_() {
            return this.f_315841_.encodeStart(this.f_313909_, this.f_315763_);
        }

        @Override
        public boolean equals(Object p_334040_) {
            if (this == p_334040_) {
                return true;
            } else {
                return !(p_334040_ instanceof EncoderCache.Key<?, ?> key)
                    ? false
                    : this.f_315841_ == key.f_315841_ && this.f_315763_.equals(key.f_315763_) && this.f_313909_.equals(key.f_313909_);
            }
        }

        @Override
        public int hashCode() {
            int i = System.identityHashCode(this.f_315841_);
            i = 31 * i + this.f_315763_.hashCode();
            return 31 * i + this.f_313909_.hashCode();
        }
    }
}