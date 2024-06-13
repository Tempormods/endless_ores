package net.minecraft.util.parsing.packrat;

import java.util.Optional;

public interface Rule<S, T> {
    Optional<T> m_319437_(ParseState<S> p_335539_);

    static <S, T> Rule<S, T> m_323835_(Term<S> p_334127_, Rule.RuleAction<S, T> p_334890_) {
        return new Rule.WrappedTerm<>(p_334890_, p_334127_);
    }

    static <S, T> Rule<S, T> m_321433_(Term<S> p_336211_, Rule.SimpleRuleAction<T> p_332994_) {
        return new Rule.WrappedTerm<>((p_331302_, p_331658_) -> Optional.of(p_332994_.m_323771_(p_331658_)), p_336211_);
    }

    @FunctionalInterface
    public interface RuleAction<S, T> {
        Optional<T> m_324817_(ParseState<S> p_332162_, Scope p_335135_);
    }

    @FunctionalInterface
    public interface SimpleRuleAction<T> {
        T m_323771_(Scope p_332535_);
    }

    public static record WrappedTerm<S, T>(Rule.RuleAction<S, T> f_316194_, Term<S> f_316968_) implements Rule<S, T> {
        @Override
        public Optional<T> m_319437_(ParseState<S> p_328860_) {
            Scope scope = new Scope();
            return this.f_316968_.m_319964_(p_328860_, scope, Control.f_316309_) ? this.f_316194_.m_324817_(p_328860_, scope) : Optional.empty();
        }
    }
}