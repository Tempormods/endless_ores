package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public abstract class ResourceLookupRule<C, V> implements Rule<StringReader, V>, ResourceSuggestion {
    private final Atom<ResourceLocation> f_316581_;
    protected final C f_314968_;

    protected ResourceLookupRule(Atom<ResourceLocation> p_330644_, C p_330414_) {
        this.f_316581_ = p_330644_;
        this.f_314968_ = p_330414_;
    }

    @Override
    public Optional<V> m_319437_(ParseState<StringReader> p_332578_) {
        p_332578_.m_322193_().skipWhitespace();
        int i = p_332578_.m_320129_();
        Optional<ResourceLocation> optional = p_332578_.m_324142_(this.f_316581_);
        if (optional.isPresent()) {
            try {
                return Optional.of(this.m_319888_(p_332578_.m_322193_(), optional.get()));
            } catch (Exception exception) {
                p_332578_.m_323339_().m_322006_(i, this, exception);
                return Optional.empty();
            }
        } else {
            p_332578_.m_323339_().m_322006_(i, this, ResourceLocation.ERROR_INVALID.createWithContext(p_332578_.m_322193_()));
            return Optional.empty();
        }
    }

    protected abstract V m_319888_(ImmutableStringReader p_336199_, ResourceLocation p_330230_) throws Exception;
}