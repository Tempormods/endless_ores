package net.minecraft.core.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public interface DataComponentMap extends Iterable<TypedDataComponent<?>> {
    DataComponentMap f_314291_ = new DataComponentMap() {
        @Nullable
        @Override
        public <T> T m_318834_(DataComponentType<? extends T> p_331068_) {
            return null;
        }

        @Override
        public Set<DataComponentType<?>> m_319675_() {
            return Set.of();
        }

        @Override
        public Iterator<TypedDataComponent<?>> iterator() {
            return Collections.emptyIterator();
        }
    };
    Codec<DataComponentMap> f_315283_ = DataComponentType.f_314535_.flatComapMap(DataComponentMap.Builder::m_319295_, p_329446_ -> {
        int i = p_329446_.m_319491_();
        if (i == 0) {
            return DataResult.success(Reference2ObjectMaps.emptyMap());
        } else {
            Reference2ObjectMap<DataComponentType<?>, Object> reference2objectmap = new Reference2ObjectArrayMap<>(i);

            for (TypedDataComponent<?> typeddatacomponent : p_329446_) {
                if (!typeddatacomponent.f_316611_().m_322187_()) {
                    reference2objectmap.put(typeddatacomponent.f_316611_(), typeddatacomponent.f_314804_());
                }
            }

            return DataResult.success(reference2objectmap);
        }
    });

    static DataComponentMap m_319349_(final DataComponentMap p_329885_, final DataComponentMap p_330534_) {
        return new DataComponentMap() {
            @Nullable
            @Override
            public <T> T m_318834_(DataComponentType<? extends T> p_330817_) {
                T t = p_330534_.m_318834_(p_330817_);
                return t != null ? t : p_329885_.m_318834_(p_330817_);
            }

            @Override
            public Set<DataComponentType<?>> m_319675_() {
                return Sets.union(p_329885_.m_319675_(), p_330534_.m_319675_());
            }
        };
    }

    static DataComponentMap.Builder m_323371_() {
        return new DataComponentMap.Builder();
    }

    @Nullable
    <T> T m_318834_(DataComponentType<? extends T> p_331367_);

    Set<DataComponentType<?>> m_319675_();

    default boolean m_321946_(DataComponentType<?> p_334046_) {
        return this.m_318834_(p_334046_) != null;
    }

    default <T> T m_322806_(DataComponentType<? extends T> p_333956_, T p_334477_) {
        T t = this.m_318834_(p_333956_);
        return t != null ? t : p_334477_;
    }

    @Nullable
    default <T> TypedDataComponent<T> m_319453_(DataComponentType<T> p_334795_) {
        T t = this.m_318834_(p_334795_);
        return t != null ? new TypedDataComponent<>(p_334795_, t) : null;
    }

    @Override
    default Iterator<TypedDataComponent<?>> iterator() {
        return Iterators.transform(this.m_319675_().iterator(), p_336195_ -> Objects.requireNonNull(this.m_319453_((DataComponentType<?>)p_336195_)));
    }

    default Stream<TypedDataComponent<?>> m_322172_() {
        return StreamSupport.stream(Spliterators.spliterator(this.iterator(), (long)this.m_319491_(), 1345), false);
    }

    default int m_319491_() {
        return this.m_319675_().size();
    }

    default boolean m_323714_() {
        return this.m_319491_() == 0;
    }

    default DataComponentMap m_322426_(final Predicate<DataComponentType<?>> p_329403_) {
        return new DataComponentMap() {
            @Nullable
            @Override
            public <T> T m_318834_(DataComponentType<? extends T> p_329684_) {
                return p_329403_.test(p_329684_) ? DataComponentMap.this.m_318834_(p_329684_) : null;
            }

            @Override
            public Set<DataComponentType<?>> m_319675_() {
                return Sets.filter(DataComponentMap.this.m_319675_(), p_329403_::test);
            }
        };
    }

    public static class Builder {
        private final Reference2ObjectMap<DataComponentType<?>, Object> f_314275_ = new Reference2ObjectArrayMap<>();

        Builder() {
        }

        public <T> DataComponentMap.Builder m_322739_(DataComponentType<T> p_336133_, @Nullable T p_329579_) {
            this.m_321201_(p_336133_, p_329579_);
            return this;
        }

        <T> void m_321201_(DataComponentType<T> p_331443_, @Nullable Object p_334337_) {
            if (p_334337_ != null) {
                this.f_314275_.put(p_331443_, p_334337_);
            } else {
                this.f_314275_.remove(p_331443_);
            }
        }

        public DataComponentMap.Builder m_321974_(DataComponentMap p_335426_) {
            for (TypedDataComponent<?> typeddatacomponent : p_335426_) {
                this.f_314275_.put(typeddatacomponent.f_316611_(), typeddatacomponent.f_314804_());
            }

            return this;
        }

        public DataComponentMap m_318826_() {
            return m_319295_(this.f_314275_);
        }

        private static DataComponentMap m_319295_(Map<DataComponentType<?>, Object> p_330455_) {
            if (p_330455_.isEmpty()) {
                return DataComponentMap.f_314291_;
            } else {
                return p_330455_.size() < 8
                    ? new DataComponentMap.Builder.SimpleMap(new Reference2ObjectArrayMap<>(p_330455_))
                    : new DataComponentMap.Builder.SimpleMap(new Reference2ObjectOpenHashMap<>(p_330455_));
            }
        }

        static record SimpleMap(Reference2ObjectMap<DataComponentType<?>, Object> f_314581_) implements DataComponentMap {
            @Nullable
            @Override
            public <T> T m_318834_(DataComponentType<? extends T> p_335671_) {
                return (T)this.f_314581_.get(p_335671_);
            }

            @Override
            public boolean m_321946_(DataComponentType<?> p_335479_) {
                return this.f_314581_.containsKey(p_335479_);
            }

            @Override
            public Set<DataComponentType<?>> m_319675_() {
                return this.f_314581_.keySet();
            }

            @Override
            public Iterator<TypedDataComponent<?>> iterator() {
                return Iterators.transform(Reference2ObjectMaps.fastIterator(this.f_314581_), TypedDataComponent::m_322622_);
            }

            @Override
            public int m_319491_() {
                return this.f_314581_.size();
            }

            @Override
            public String toString() {
                return this.f_314581_.toString();
            }
        }
    }
}