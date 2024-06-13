package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionCountsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
    List<CollectionCountsPredicate.Entry<T, P>> m_319753_();

    static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate<T, P>> m_321426_(Codec<P> p_335836_) {
        return CollectionCountsPredicate.Entry.<T, P>m_319511_(p_335836_)
            .listOf()
            .xmap(CollectionCountsPredicate::m_319310_, CollectionCountsPredicate::m_319753_);
    }

    @SafeVarargs
    static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> m_323251_(CollectionCountsPredicate.Entry<T, P>... p_332496_) {
        return m_319310_(List.of(p_332496_));
    }

    static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> m_319310_(List<CollectionCountsPredicate.Entry<T, P>> p_334665_) {
        return (CollectionCountsPredicate<T, P>)(switch (p_334665_.size()) {
            case 0 -> new CollectionCountsPredicate.Zero();
            case 1 -> new CollectionCountsPredicate.Single(p_334665_.getFirst());
            default -> new CollectionCountsPredicate.Multiple(p_334665_);
        });
    }

    public static record Entry<T, P extends Predicate<T>>(P f_313895_, MinMaxBounds.Ints f_315065_) {
        public static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate.Entry<T, P>> m_319511_(Codec<P> p_334145_) {
            return RecordCodecBuilder.create(
                p_334567_ -> p_334567_.group(
                            p_334145_.fieldOf("test").forGetter(CollectionCountsPredicate.Entry::f_313895_),
                            MinMaxBounds.Ints.CODEC.fieldOf("count").forGetter(CollectionCountsPredicate.Entry::f_315065_)
                        )
                        .apply(p_334567_, CollectionCountsPredicate.Entry::new)
            );
        }

        public boolean m_322881_(Iterable<T> p_329726_) {
            int i = 0;

            for (T t : p_329726_) {
                if (this.f_313895_.test(t)) {
                    i++;
                }
            }

            return this.f_315065_.matches(i);
        }
    }

    public static record Multiple<T, P extends Predicate<T>>(List<CollectionCountsPredicate.Entry<T, P>> f_315031_) implements CollectionCountsPredicate<T, P> {
        public boolean test(Iterable<T> p_329412_) {
            for (CollectionCountsPredicate.Entry<T, P> entry : this.f_315031_) {
                if (!entry.m_322881_(p_329412_)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public List<CollectionCountsPredicate.Entry<T, P>> m_319753_() {
            return this.f_315031_;
        }
    }

    public static record Single<T, P extends Predicate<T>>(CollectionCountsPredicate.Entry<T, P> f_316803_) implements CollectionCountsPredicate<T, P> {
        public boolean test(Iterable<T> p_333879_) {
            return this.f_316803_.m_322881_(p_333879_);
        }

        @Override
        public List<CollectionCountsPredicate.Entry<T, P>> m_319753_() {
            return List.of(this.f_316803_);
        }
    }

    public static class Zero<T, P extends Predicate<T>> implements CollectionCountsPredicate<T, P> {
        public boolean test(Iterable<T> p_329157_) {
            return true;
        }

        @Override
        public List<CollectionCountsPredicate.Entry<T, P>> m_319753_() {
            return List.of();
        }
    }
}