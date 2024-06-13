package net.minecraft.util.parsing.packrat;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Dictionary<S> {
    private final Map<Atom<?>, Rule<S, ?>> f_315744_ = new HashMap<>();

    public <T> void m_323201_(Atom<T> p_329080_, Rule<S, T> p_331073_) {
        Rule<S, ?> rule = this.f_315744_.putIfAbsent(p_329080_, p_331073_);
        if (rule != null) {
            throw new IllegalArgumentException("Trying to override rule: " + p_329080_);
        }
    }

    public <T> void m_323801_(Atom<T> p_335785_, Term<S> p_327787_, Rule.RuleAction<S, T> p_333483_) {
        this.m_323201_(p_335785_, Rule.m_323835_(p_327787_, p_333483_));
    }

    public <T> void m_320832_(Atom<T> p_333993_, Term<S> p_331531_, Rule.SimpleRuleAction<T> p_336076_) {
        this.m_323201_(p_333993_, Rule.m_321433_(p_331531_, p_336076_));
    }

    @Nullable
    public <T> Rule<S, T> m_318657_(Atom<T> p_335131_) {
        return (Rule<S, T>)this.f_315744_.get(p_335131_);
    }
}