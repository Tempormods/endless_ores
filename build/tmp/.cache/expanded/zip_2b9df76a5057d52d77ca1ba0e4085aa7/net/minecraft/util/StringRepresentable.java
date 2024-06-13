package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;

public interface StringRepresentable {
    int PRE_BUILT_MAP_THRESHOLD = 16;

    String getSerializedName();

    static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnum(Supplier<E[]> pElementsSupplier) {
        return fromEnumWithMapping(pElementsSupplier, p_312201_ -> p_312201_);
    }

    static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnumWithMapping(Supplier<E[]> pEnumValues, Function<String, String> pKeyFunction) {
        E[] ae = (E[])pEnumValues.get();
        Function<String, E> function = m_306378_(ae, pKeyFunction);
        return new StringRepresentable.EnumCodec<>(ae, function);
    }

    static <T extends StringRepresentable> Codec<T> m_306774_(Supplier<T[]> p_311788_) {
        T[] at = (T[])p_311788_.get();
        Function<String, T> function = m_306378_(at, p_308975_ -> p_308975_);
        ToIntFunction<T> tointfunction = Util.createIndexLookup(Arrays.asList(at));
        return new StringRepresentable.StringRepresentableCodec<>(at, function, tointfunction);
    }

    static <T extends StringRepresentable> Function<String, T> m_306378_(T[] p_312243_, Function<String, String> p_313109_) {
        if (p_312243_.length > 16) {
            Map<String, T> map = Arrays.<StringRepresentable>stream(p_312243_)
                .collect(Collectors.toMap(p_308977_ -> p_313109_.apply(p_308977_.getSerializedName()), p_311743_ -> (T)p_311743_));
            return p_308974_ -> p_308974_ == null ? null : map.get(p_308974_);
        } else {
            return p_308972_ -> {
                for (T t : p_312243_) {
                    if (p_313109_.apply(t.getSerializedName()).equals(p_308972_)) {
                        return t;
                    }
                }

                return null;
            };
        }
    }

    static Keyable keys(final StringRepresentable[] pSerializables) {
        return new Keyable() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> p_184758_) {
                return Arrays.stream(pSerializables).map(StringRepresentable::getSerializedName).map(p_184758_::createString);
            }
        };
    }

    @Deprecated
    public static class EnumCodec<E extends Enum<E> & StringRepresentable> extends StringRepresentable.StringRepresentableCodec<E> {
        private final Function<String, E> resolver;

        public EnumCodec(E[] pValues, Function<String, E> pResolver) {
            super(pValues, pResolver, p_216454_ -> p_216454_.ordinal());
            this.resolver = pResolver;
        }

        @Nullable
        public E byName(@Nullable String pName) {
            return this.resolver.apply(pName);
        }

        public E byName(@Nullable String pName, E pDefaultValue) {
            return Objects.requireNonNullElse(this.byName(pName), pDefaultValue);
        }
    }

    public static class StringRepresentableCodec<S extends StringRepresentable> implements Codec<S> {
        private final Codec<S> f_302920_;

        public StringRepresentableCodec(S[] p_309730_, Function<String, S> p_311107_, ToIntFunction<S> p_312549_) {
            this.f_302920_ = ExtraCodecs.orCompressed(
                Codec.stringResolver(StringRepresentable::getSerializedName, p_311107_),
                ExtraCodecs.idResolverCodec(p_312549_, p_312747_ -> p_312747_ >= 0 && p_312747_ < p_309730_.length ? p_309730_[p_312747_] : null, -1)
            );
        }

        @Override
        public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> p_310491_, T p_312317_) {
            return this.f_302920_.decode(p_310491_, p_312317_);
        }

        public <T> DataResult<T> encode(S p_312413_, DynamicOps<T> p_310685_, T p_312430_) {
            return this.f_302920_.encode(p_312413_, p_310685_, p_312430_);
        }
    }
}