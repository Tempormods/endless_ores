package net.minecraft.advancements.critereon;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;

public record CollectionPredicate<T, P extends Predicate<T>>(
    Optional<CollectionContentsPredicate<T, P>> f_315792_, Optional<CollectionCountsPredicate<T, P>> f_314648_, Optional<MinMaxBounds.Ints> f_316667_
) implements Predicate<Iterable<T>> {
    public static <T, P extends Predicate<T>> Codec<CollectionPredicate<T, P>> m_321514_(Codec<P> p_330735_) {
        return RecordCodecBuilder.create(
            p_335025_ -> p_335025_.group(
                        CollectionContentsPredicate.<T, P>m_323600_(p_330735_).optionalFieldOf("contains").forGetter(CollectionPredicate::f_315792_),
                        CollectionCountsPredicate.<T, P>m_321426_(p_330735_).optionalFieldOf("count").forGetter(CollectionPredicate::f_314648_),
                        MinMaxBounds.Ints.CODEC.optionalFieldOf("size").forGetter(CollectionPredicate::f_316667_)
                    )
                    .apply(p_335025_, CollectionPredicate::new)
        );
    }

    public boolean test(Iterable<T> p_332418_) {
        if (this.f_315792_.isPresent() && !this.f_315792_.get().test(p_332418_)) {
            return false;
        } else {
            return this.f_314648_.isPresent() && !this.f_314648_.get().test(p_332418_)
                ? false
                : !this.f_316667_.isPresent() || this.f_316667_.get().matches(Iterables.size(p_332418_));
        }
    }
}