package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContentsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
    List<P> m_319415_();

    static <T, P extends Predicate<T>> Codec<CollectionContentsPredicate<T, P>> m_323600_(Codec<P> p_330819_) {
        return p_330819_.listOf().xmap(CollectionContentsPredicate::m_322502_, CollectionContentsPredicate::m_319415_);
    }

    @SafeVarargs
    static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> m_320641_(P... p_329822_) {
        return m_322502_(List.of(p_329822_));
    }

    static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> m_322502_(List<P> p_330160_) {
        return (CollectionContentsPredicate<T, P>)(switch (p_330160_.size()) {
            case 0 -> new CollectionContentsPredicate.Zero();
            case 1 -> new CollectionContentsPredicate.Single(p_330160_.getFirst());
            default -> new CollectionContentsPredicate.Multiple(p_330160_);
        });
    }

    public static record Multiple<T, P extends Predicate<T>>(List<P> f_316175_) implements CollectionContentsPredicate<T, P> {
        public boolean test(Iterable<T> p_334780_) {
            List<Predicate<T>> list = new ArrayList<>(this.f_316175_);

            for (T t : p_334780_) {
                list.removeIf(p_331259_ -> p_331259_.test(t));
                if (list.isEmpty()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public List<P> m_319415_() {
            return this.f_316175_;
        }
    }

    public static record Single<T, P extends Predicate<T>>(P f_316674_) implements CollectionContentsPredicate<T, P> {
        public boolean test(Iterable<T> p_332451_) {
            for (T t : p_332451_) {
                if (this.f_316674_.test(t)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public List<P> m_319415_() {
            return List.of(this.f_316674_);
        }
    }

    public static class Zero<T, P extends Predicate<T>> implements CollectionContentsPredicate<T, P> {
        public boolean test(Iterable<T> p_333955_) {
            return true;
        }

        @Override
        public List<P> m_319415_() {
            return List.of();
        }
    }
}