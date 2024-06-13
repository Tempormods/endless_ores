package net.minecraft.commands;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CacheableFunction {
    public static final Codec<CacheableFunction> f_303873_ = ResourceLocation.CODEC.xmap(CacheableFunction::new, CacheableFunction::m_305018_);
    private final ResourceLocation f_302248_;
    private boolean f_303631_;
    private Optional<CommandFunction<CommandSourceStack>> f_303221_ = Optional.empty();

    public CacheableFunction(ResourceLocation p_312073_) {
        this.f_302248_ = p_312073_;
    }

    public Optional<CommandFunction<CommandSourceStack>> m_306832_(ServerFunctionManager p_310125_) {
        if (!this.f_303631_) {
            this.f_303221_ = p_310125_.get(this.f_302248_);
            this.f_303631_ = true;
        }

        return this.f_303221_;
    }

    public ResourceLocation m_305018_() {
        return this.f_302248_;
    }

    @Override
    public boolean equals(Object p_313210_) {
        if (p_313210_ == this) {
            return true;
        } else {
            if (p_313210_ instanceof CacheableFunction cacheablefunction && this.m_305018_().equals(cacheablefunction.m_305018_())) {
                return true;
            }

            return false;
        }
    }
}