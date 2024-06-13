package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentType<T> {
    Codec<DataComponentType<?>> f_314889_ = Codec.lazyInitialized(() -> BuiltInRegistries.f_315333_.byNameCodec());
    StreamCodec<RegistryFriendlyByteBuf, DataComponentType<?>> f_315368_ = StreamCodec.m_320869_(p_335400_ -> ByteBufCodecs.m_320159_(Registries.f_316190_));
    Codec<DataComponentType<?>> f_315188_ = f_314889_.validate(
        p_331558_ -> p_331558_.m_322187_()
                ? DataResult.error(() -> "Encountered transient component " + BuiltInRegistries.f_315333_.getKey(p_331558_))
                : DataResult.success(p_331558_)
    );
    Codec<Map<DataComponentType<?>, Object>> f_314535_ = Codec.dispatchedMap(f_315188_, DataComponentType::m_319588_);

    static <T> DataComponentType.Builder<T> m_320209_() {
        return new DataComponentType.Builder<>();
    }

    @Nullable
    Codec<T> m_319878_();

    default Codec<T> m_319588_() {
        Codec<T> codec = this.m_319878_();
        if (codec == null) {
            throw new IllegalStateException(this + " is not a persistent component");
        } else {
            return codec;
        }
    }

    default boolean m_322187_() {
        return this.m_319878_() == null;
    }

    StreamCodec<? super RegistryFriendlyByteBuf, T> m_318786_();

    public static class Builder<T> {
        @Nullable
        private Codec<T> f_315486_;
        @Nullable
        private StreamCodec<? super RegistryFriendlyByteBuf, T> f_314262_;
        private boolean f_314972_;

        public DataComponentType.Builder<T> m_319357_(Codec<T> p_334382_) {
            this.f_315486_ = p_334382_;
            return this;
        }

        public DataComponentType.Builder<T> m_321554_(StreamCodec<? super RegistryFriendlyByteBuf, T> p_328597_) {
            this.f_314262_ = p_328597_;
            return this;
        }

        public DataComponentType.Builder<T> m_319193_() {
            this.f_314972_ = true;
            return this;
        }

        public DataComponentType<T> m_318929_() {
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamcodec = Objects.requireNonNullElseGet(
                this.f_314262_, () -> ByteBufCodecs.m_319284_(Objects.requireNonNull(this.f_315486_, "Missing Codec for component"))
            );
            Codec<T> codec = this.f_314972_ && this.f_315486_ != null ? DataComponents.f_315405_.m_321472_(this.f_315486_) : this.f_315486_;
            return new DataComponentType.Builder.SimpleType<>(codec, streamcodec);
        }

        static class SimpleType<T> implements DataComponentType<T> {
            @Nullable
            private final Codec<T> f_315164_;
            private final StreamCodec<? super RegistryFriendlyByteBuf, T> f_314669_;

            SimpleType(@Nullable Codec<T> p_335427_, StreamCodec<? super RegistryFriendlyByteBuf, T> p_335369_) {
                this.f_315164_ = p_335427_;
                this.f_314669_ = p_335369_;
            }

            @Nullable
            @Override
            public Codec<T> m_319878_() {
                return this.f_315164_;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> m_318786_() {
                return this.f_314669_;
            }

            @Override
            public String toString() {
                return Util.m_322642_((Registry)BuiltInRegistries.f_315333_, this);
            }
        }
    }
}