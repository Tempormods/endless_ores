package net.minecraft.util.parsing.packrat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class ParseState<S> {
    private final Map<ParseState.CacheKey<?>, ParseState.CacheEntry<?>> f_315699_ = new HashMap<>();
    private final Dictionary<S> f_315927_;
    private final ErrorCollector<S> f_316195_;

    protected ParseState(Dictionary<S> p_331339_, ErrorCollector<S> p_333871_) {
        this.f_315927_ = p_331339_;
        this.f_316195_ = p_333871_;
    }

    public ErrorCollector<S> m_323339_() {
        return this.f_316195_;
    }

    public <T> Optional<T> m_319664_(Atom<T> p_334307_) {
        Optional<T> optional = this.m_324142_(p_334307_);
        if (optional.isPresent()) {
            this.f_316195_.m_318862_(this.m_320129_());
        }

        return optional;
    }

    public <T> Optional<T> m_324142_(Atom<T> p_335708_) {
        ParseState.CacheKey<T> cachekey = new ParseState.CacheKey<>(p_335708_, this.m_320129_());
        ParseState.CacheEntry<T> cacheentry = this.m_319809_(cachekey);
        if (cacheentry != null) {
            this.m_321642_(cacheentry.f_314517_());
            return cacheentry.f_314994_;
        } else {
            Rule<S, T> rule = this.f_315927_.m_318657_(p_335708_);
            if (rule == null) {
                throw new IllegalStateException("No symbol " + p_335708_);
            } else {
                Optional<T> optional = rule.m_319437_(this);
                this.m_323197_(cachekey, optional);
                return optional;
            }
        }
    }

    @Nullable
    private <T> ParseState.CacheEntry<T> m_319809_(ParseState.CacheKey<T> p_333102_) {
        return (ParseState.CacheEntry<T>)this.f_315699_.get(p_333102_);
    }

    private <T> void m_323197_(ParseState.CacheKey<T> p_333772_, Optional<T> p_329813_) {
        this.f_315699_.put(p_333772_, new ParseState.CacheEntry<>(p_329813_, this.m_320129_()));
    }

    public abstract S m_322193_();

    public abstract int m_320129_();

    public abstract void m_321642_(int p_331216_);

    static record CacheEntry<T>(Optional<T> f_314994_, int f_314517_) {
    }

    static record CacheKey<T>(Atom<T> f_314600_, int f_314127_) {
    }
}