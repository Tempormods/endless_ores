package net.minecraft.util.parsing.packrat;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Objects;
import javax.annotation.Nullable;

public final class Scope {
    private final Object2ObjectMap<Atom<?>, Object> f_314034_ = new Object2ObjectArrayMap<>();

    public <T> void m_325086_(Atom<T> p_329036_, @Nullable T p_328259_) {
        this.f_314034_.put(p_329036_, p_328259_);
    }

    @Nullable
    public <T> T m_324672_(Atom<T> p_331470_) {
        return (T)this.f_314034_.get(p_331470_);
    }

    public <T> T m_324370_(Atom<T> p_332933_) {
        return Objects.requireNonNull(this.m_324672_(p_332933_));
    }

    public <T> T m_319930_(Atom<T> p_335515_, T p_333340_) {
        return Objects.requireNonNullElse(this.m_324672_(p_335515_), p_333340_);
    }

    @Nullable
    @SafeVarargs
    public final <T> T m_319017_(Atom<T>... p_331175_) {
        for (Atom<T> atom : p_331175_) {
            T t = this.m_324672_(atom);
            if (t != null) {
                return t;
            }
        }

        return null;
    }

    @SafeVarargs
    public final <T> T m_320837_(Atom<T>... p_330748_) {
        return Objects.requireNonNull(this.m_319017_(p_330748_));
    }

    @Override
    public String toString() {
        return this.f_314034_.toString();
    }

    public void m_319401_(Scope p_334073_) {
        this.f_314034_.putAll(p_334073_.f_314034_);
    }

    @Override
    public boolean equals(Object p_331272_) {
        if (this == p_331272_) {
            return true;
        } else {
            return p_331272_ instanceof Scope scope ? this.f_314034_.equals(scope.f_314034_) : false;
        }
    }

    @Override
    public int hashCode() {
        return this.f_314034_.hashCode();
    }
}