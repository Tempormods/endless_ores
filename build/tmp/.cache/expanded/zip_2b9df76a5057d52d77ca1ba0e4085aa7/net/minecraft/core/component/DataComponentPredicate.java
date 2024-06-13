package net.minecraft.core.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class DataComponentPredicate implements Predicate<DataComponentMap> {
    public static final Codec<DataComponentPredicate> f_314199_ = DataComponentType.f_314535_
        .xmap(
            p_336043_ -> new DataComponentPredicate(p_336043_.entrySet().stream().map(TypedDataComponent::m_322622_).collect(Collectors.toList())),
            p_335229_ -> p_335229_.f_316555_
                    .stream()
                    .filter(p_332263_ -> !p_332263_.f_316611_().m_322187_())
                    .collect(Collectors.toMap(TypedDataComponent::f_316611_, TypedDataComponent::f_314804_))
        );
    public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate> f_317058_ = TypedDataComponent.f_315130_
        .m_321801_(ByteBufCodecs.m_324765_())
        .m_323038_(DataComponentPredicate::new, p_334923_ -> p_334923_.f_316555_);
    public static final DataComponentPredicate f_314891_ = new DataComponentPredicate(List.of());
    private final List<TypedDataComponent<?>> f_316555_;

    DataComponentPredicate(List<TypedDataComponent<?>> p_328927_) {
        this.f_316555_ = p_328927_;
    }

    public static DataComponentPredicate.Builder m_321115_() {
        return new DataComponentPredicate.Builder();
    }

    public static DataComponentPredicate m_322694_(DataComponentMap p_333484_) {
        return new DataComponentPredicate(ImmutableList.copyOf(p_333484_));
    }

    @Override
    public boolean equals(Object p_331290_) {
        if (p_331290_ instanceof DataComponentPredicate datacomponentpredicate && this.f_316555_.equals(datacomponentpredicate.f_316555_)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.f_316555_.hashCode();
    }

    @Override
    public String toString() {
        return this.f_316555_.toString();
    }

    public boolean test(DataComponentMap p_329561_) {
        for (TypedDataComponent<?> typeddatacomponent : this.f_316555_) {
            Object object = p_329561_.m_318834_(typeddatacomponent.f_316611_());
            if (!Objects.equals(typeddatacomponent.f_314804_(), object)) {
                return false;
            }
        }

        return true;
    }

    public boolean m_323113_(DataComponentHolder p_332932_) {
        return this.test(p_332932_.m_318732_());
    }

    public boolean m_323401_() {
        return this.f_316555_.isEmpty();
    }

    public DataComponentPatch m_323520_() {
        DataComponentPatch.Builder datacomponentpatch$builder = DataComponentPatch.m_322543_();

        for (TypedDataComponent<?> typeddatacomponent : this.f_316555_) {
            datacomponentpatch$builder.m_323094_(typeddatacomponent);
        }

        return datacomponentpatch$builder.m_323652_();
    }

    public static class Builder {
        private final List<TypedDataComponent<?>> f_314051_ = new ArrayList<>();

        Builder() {
        }

        public <T> DataComponentPredicate.Builder m_319245_(DataComponentType<? super T> p_332211_, T p_330030_) {
            for (TypedDataComponent<?> typeddatacomponent : this.f_314051_) {
                if (typeddatacomponent.f_316611_() == p_332211_) {
                    throw new IllegalArgumentException("Predicate already has component of type: '" + p_332211_ + "'");
                }
            }

            this.f_314051_.add(new TypedDataComponent<>(p_332211_, p_330030_));
            return this;
        }

        public DataComponentPredicate m_324461_() {
            return new DataComponentPredicate(List.copyOf(this.f_314051_));
        }
    }
}