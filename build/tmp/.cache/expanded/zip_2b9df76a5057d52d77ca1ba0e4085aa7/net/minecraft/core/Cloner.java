package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;

public class Cloner<T> {
    private final Codec<T> f_302775_;

    Cloner(Codec<T> p_312738_) {
        this.f_302775_ = p_312738_;
    }

    public T m_306098_(T p_311468_, HolderLookup.Provider p_309751_, HolderLookup.Provider p_312597_) {
        DynamicOps<Object> dynamicops = p_309751_.m_318927_(JavaOps.INSTANCE);
        DynamicOps<Object> dynamicops1 = p_312597_.m_318927_(JavaOps.INSTANCE);
        Object object = this.f_302775_.encodeStart(dynamicops, p_311468_).getOrThrow(p_311642_ -> new IllegalStateException("Failed to encode: " + p_311642_));
        return this.f_302775_.parse(dynamicops1, object).getOrThrow(p_311707_ -> new IllegalStateException("Failed to decode: " + p_311707_));
    }

    public static class Factory {
        private final Map<ResourceKey<? extends Registry<?>>, Cloner<?>> f_303339_ = new HashMap<>();

        public <T> Cloner.Factory m_304739_(ResourceKey<? extends Registry<? extends T>> p_310427_, Codec<T> p_312943_) {
            this.f_303339_.put(p_310427_, new Cloner<>(p_312943_));
            return this;
        }

        @Nullable
        public <T> Cloner<T> m_305497_(ResourceKey<? extends Registry<? extends T>> p_311946_) {
            return (Cloner<T>)this.f_303339_.get(p_311946_);
        }
    }
}