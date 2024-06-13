package net.minecraft.util.parsing.packrat;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.mutable.MutableBoolean;

public interface Term<S> {
    boolean m_319964_(ParseState<S> p_334989_, Scope p_334936_, Control p_335743_);

    static <S> Term<S> m_321288_(Atom<?> p_334806_) {
        return new Term.Reference<>(p_334806_);
    }

    static <S, T> Term<S> m_324581_(Atom<T> p_333477_, T p_335010_) {
        return new Term.Marker<>(p_333477_, p_335010_);
    }

    @SafeVarargs
    static <S> Term<S> m_322077_(Term<S>... p_331306_) {
        return new Term.Sequence<>(List.of(p_331306_));
    }

    @SafeVarargs
    static <S> Term<S> m_319180_(Term<S>... p_334441_) {
        return new Term.Alternative<>(List.of(p_334441_));
    }

    static <S> Term<S> m_325045_(Term<S> p_335256_) {
        return new Term.Maybe<>(p_335256_);
    }

    static <S> Term<S> m_324824_() {
        return new Term<S>() {
            @Override
            public boolean m_319964_(ParseState<S> p_333527_, Scope p_336097_, Control p_335047_) {
                p_335047_.m_322456_();
                return true;
            }

            @Override
            public String toString() {
                return "\u2191";
            }
        };
    }

    static <S> Term<S> m_324056_() {
        return new Term<S>() {
            @Override
            public boolean m_319964_(ParseState<S> p_328418_, Scope p_332040_, Control p_328784_) {
                return true;
            }

            @Override
            public String toString() {
                return "\u03b5";
            }
        };
    }

    public static record Alternative<S>(List<Term<S>> f_314042_) implements Term<S> {
        @Override
        public boolean m_319964_(ParseState<S> p_328094_, Scope p_331753_, Control p_334626_) {
            MutableBoolean mutableboolean = new MutableBoolean();
            Control control = mutableboolean::setTrue;
            int i = p_328094_.m_320129_();

            for (Term<S> term : this.f_314042_) {
                if (mutableboolean.isTrue()) {
                    break;
                }

                Scope scope = new Scope();
                if (term.m_319964_(p_328094_, scope, control)) {
                    p_331753_.m_319401_(scope);
                    return true;
                }

                p_328094_.m_321642_(i);
            }

            return false;
        }
    }

    public static record Marker<S, T>(Atom<T> f_316500_, T f_316744_) implements Term<S> {
        @Override
        public boolean m_319964_(ParseState<S> p_332878_, Scope p_331621_, Control p_334053_) {
            p_331621_.m_325086_(this.f_316500_, this.f_316744_);
            return true;
        }
    }

    public static record Maybe<S>(Term<S> f_315826_) implements Term<S> {
        @Override
        public boolean m_319964_(ParseState<S> p_332001_, Scope p_329861_, Control p_331352_) {
            int i = p_332001_.m_320129_();
            if (!this.f_315826_.m_319964_(p_332001_, p_329861_, p_331352_)) {
                p_332001_.m_321642_(i);
            }

            return true;
        }
    }

    public static record Reference<S, T>(Atom<T> f_315671_) implements Term<S> {
        @Override
        public boolean m_319964_(ParseState<S> p_332365_, Scope p_333205_, Control p_334292_) {
            Optional<T> optional = p_332365_.m_324142_(this.f_315671_);
            if (optional.isEmpty()) {
                return false;
            } else {
                p_333205_.m_325086_(this.f_315671_, optional.get());
                return true;
            }
        }
    }

    public static record Sequence<S>(List<Term<S>> f_316370_) implements Term<S> {
        @Override
        public boolean m_319964_(ParseState<S> p_330195_, Scope p_336361_, Control p_328798_) {
            int i = p_330195_.m_320129_();

            for (Term<S> term : this.f_316370_) {
                if (!term.m_319964_(p_330195_, p_336361_, p_328798_)) {
                    p_330195_.m_321642_(i);
                    return false;
                }
            }

            return true;
        }
    }
}