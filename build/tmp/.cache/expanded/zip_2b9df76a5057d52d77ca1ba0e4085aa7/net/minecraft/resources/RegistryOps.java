package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DelegatingOps<T> {
    private final RegistryOps.RegistryInfoLookup lookupProvider;

    public static <T> RegistryOps<T> create(DynamicOps<T> pDelegate, HolderLookup.Provider pRegistries) {
        return create(pDelegate, new RegistryOps.HolderLookupAdapter(pRegistries));
    }

    public static <T> RegistryOps<T> create(DynamicOps<T> pDelegate, RegistryOps.RegistryInfoLookup pLookupProvider) {
        return new RegistryOps<>(pDelegate, pLookupProvider);
    }

    public static <T> Dynamic<T> m_321059_(Dynamic<T> p_331489_, HolderLookup.Provider p_331408_) {
        return new Dynamic<>(p_331408_.m_318927_(p_331489_.getOps()), p_331489_.getValue());
    }

    private RegistryOps(DynamicOps<T> pDelegate, RegistryOps.RegistryInfoLookup pLookupProvider) {
        super(pDelegate);
        this.lookupProvider = pLookupProvider;
    }

    public <U> RegistryOps<U> m_322470_(DynamicOps<U> p_332969_) {
        return (RegistryOps<U>)(p_332969_ == this.delegate ? this : new RegistryOps<>(p_332969_, this.lookupProvider));
    }

    public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends Registry<? extends E>> pRegistryKey) {
        return this.lookupProvider.lookup(pRegistryKey).map(RegistryOps.RegistryInfo::owner);
    }

    public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends Registry<? extends E>> pRegistryKey) {
        return this.lookupProvider.lookup(pRegistryKey).map(RegistryOps.RegistryInfo::getter);
    }

    @Override
    public boolean equals(Object p_332753_) {
        if (this == p_332753_) {
            return true;
        } else if (p_332753_ != null && this.getClass() == p_332753_.getClass()) {
            RegistryOps<?> registryops = (RegistryOps<?>)p_332753_;
            return this.delegate.equals(registryops.delegate) && this.lookupProvider.equals(registryops.lookupProvider);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode() * 31 + this.lookupProvider.hashCode();
    }

    public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends Registry<? extends E>> pRegistryOps) {
        return ExtraCodecs.retrieveContext(
                p_274811_ -> p_274811_ instanceof RegistryOps<?> registryops
                        ? registryops.lookupProvider
                            .lookup(pRegistryOps)
                            .map(p_255527_ -> DataResult.success(p_255527_.getter(), p_255527_.elementsLifecycle()))
                            .orElseGet(() -> DataResult.error(() -> "Unknown registry: " + pRegistryOps))
                        : DataResult.error(() -> "Not a registry ops")
            )
            .forGetter(p_255526_ -> null);
    }

    public static <E> com.mojang.serialization.MapCodec<HolderLookup.RegistryLookup<E>> retrieveRegistryLookup(ResourceKey<? extends Registry<? extends E>> resourceKey) {
       return ExtraCodecs.retrieveContext(ops -> {
           if (!(ops instanceof RegistryOps<?> registryOps)) {
              return DataResult.error(() -> "Not a registry ops");
           }

           return registryOps.lookupProvider.lookup(resourceKey).map(registryInfo -> {
               if (!(registryInfo.owner() instanceof HolderLookup.RegistryLookup<E> registryLookup)) {
                   return DataResult.<HolderLookup.RegistryLookup<E>>error(() -> "Found holder getter but was not a registry lookup for " + resourceKey);
               }

               return DataResult.success(registryLookup, registryInfo.elementsLifecycle());
           }).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + resourceKey));
       });
    }

    public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(ResourceKey<E> pKey) {
        ResourceKey<? extends Registry<E>> resourcekey = ResourceKey.createRegistryKey(pKey.registry());
        return ExtraCodecs.retrieveContext(
                p_274808_ -> p_274808_ instanceof RegistryOps<?> registryops
                        ? registryops.lookupProvider
                            .lookup(resourcekey)
                            .flatMap(p_255518_ -> p_255518_.getter().m_254926_(pKey))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Can't find value: " + pKey))
                        : DataResult.error(() -> "Not a registry ops")
            )
            .forGetter(p_255524_ -> null);
    }

    static final class HolderLookupAdapter implements RegistryOps.RegistryInfoLookup {
        private final HolderLookup.Provider f_315402_;
        private final Map<ResourceKey<? extends Registry<?>>, Optional<? extends RegistryOps.RegistryInfo<?>>> f_316306_ = new ConcurrentHashMap<>();

        public HolderLookupAdapter(HolderLookup.Provider p_335468_) {
            this.f_315402_ = p_335468_;
        }

        @Override
        public <E> Optional<RegistryOps.RegistryInfo<E>> lookup(ResourceKey<? extends Registry<? extends E>> p_330389_) {
            return (Optional<RegistryOps.RegistryInfo<E>>)this.f_316306_.computeIfAbsent(p_330389_, this::m_320069_);
        }

        private Optional<RegistryOps.RegistryInfo<Object>> m_320069_(ResourceKey<? extends Registry<?>> p_335602_) {
            return this.f_315402_.lookup(p_335602_).map(RegistryOps.RegistryInfo::m_322557_);
        }

        @Override
        public boolean equals(Object p_330775_) {
            if (this == p_330775_) {
                return true;
            } else {
                if (p_330775_ instanceof RegistryOps.HolderLookupAdapter registryops$holderlookupadapter
                    && this.f_315402_.equals(registryops$holderlookupadapter.f_315402_)) {
                    return true;
                }

                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.f_315402_.hashCode();
        }
    }

    public static record RegistryInfo<T>(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
        public static <T> RegistryOps.RegistryInfo<T> m_322557_(HolderLookup.RegistryLookup<T> p_329148_) {
            return new RegistryOps.RegistryInfo<>(p_329148_, p_329148_, p_329148_.m_255098_());
        }
    }

    public interface RegistryInfoLookup {
        <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> pRegistryKey);
    }
}
