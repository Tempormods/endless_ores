package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.apache.commons.lang3.mutable.MutableObject;

public class RegistrySetBuilder {
    private final List<RegistrySetBuilder.RegistryStub<?>> entries = new ArrayList<>();

    static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> pOwner) {
        return new RegistrySetBuilder.EmptyTagLookup<T>(pOwner) {
            @Override
            public Optional<Holder.Reference<T>> m_254926_(ResourceKey<T> p_255765_) {
                return pOwner.m_254926_(p_255765_);
            }
        };
    }

    static <T> HolderLookup.RegistryLookup<T> m_305117_(
        final ResourceKey<? extends Registry<? extends T>> p_311196_,
        final Lifecycle p_311352_,
        HolderOwner<T> p_335968_,
        final Map<ResourceKey<T>, Holder.Reference<T>> p_311458_
    ) {
        return new RegistrySetBuilder.EmptyTagRegistryLookup<T>(p_335968_) {
            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return p_311196_;
            }

            @Override
            public Lifecycle m_255098_() {
                return p_311352_;
            }

            @Override
            public Optional<Holder.Reference<T>> m_254926_(ResourceKey<T> p_310975_) {
                return Optional.ofNullable(p_311458_.get(p_310975_));
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return p_311458_.values().stream();
            }
        };
    }

    public <T> RegistrySetBuilder add(
        ResourceKey<? extends Registry<T>> pKey, Lifecycle pLifecycle, RegistrySetBuilder.RegistryBootstrap<T> pBootstrap
    ) {
        this.entries.add(new RegistrySetBuilder.RegistryStub<>(pKey, pLifecycle, pBootstrap));
        return this;
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> pKey, RegistrySetBuilder.RegistryBootstrap<T> pBootstrap) {
        return this.add(pKey, Lifecycle.stable(), pBootstrap);
    }

    public List<? extends ResourceKey<? extends Registry<?>>> getEntryKeys() {
        return this.entries.stream().map(RegistrySetBuilder.RegistryStub::key).toList();
    }

    private RegistrySetBuilder.BuildState createState(RegistryAccess pRegistryAccess) {
        RegistrySetBuilder.BuildState registrysetbuilder$buildstate = RegistrySetBuilder.BuildState.create(
            pRegistryAccess, this.entries.stream().map(RegistrySetBuilder.RegistryStub::key)
        );
        this.entries.forEach(p_255629_ -> p_255629_.apply(registrysetbuilder$buildstate));
        return registrysetbuilder$buildstate;
    }

    private static HolderLookup.Provider m_307871_(
        RegistrySetBuilder.UniversalOwner p_328219_, RegistryAccess p_311176_, Stream<HolderLookup.RegistryLookup<?>> p_311668_
    ) {
        record Entry<T>(HolderLookup.RegistryLookup<T> f_314871_, RegistryOps.RegistryInfo<T> f_315726_) {
            public static <T> Entry<T> m_322129_(HolderLookup.RegistryLookup<T> p_332859_) {
                return new Entry<>(new RegistrySetBuilder.EmptyTagLookupWrapper<>(p_332859_, p_332859_), RegistryOps.RegistryInfo.m_322557_(p_332859_));
            }

            public static <T> Entry<T> m_324954_(RegistrySetBuilder.UniversalOwner p_329338_, HolderLookup.RegistryLookup<T> p_334381_) {
                return new Entry<>(
                    new RegistrySetBuilder.EmptyTagLookupWrapper<>(p_329338_.m_323396_(), p_334381_),
                    new RegistryOps.RegistryInfo<>(p_329338_.m_323396_(), p_334381_, p_334381_.m_255098_())
                );
            }
        }

        final Map<ResourceKey<? extends Registry<?>>, Entry<?>> map = new HashMap<>();
        p_311176_.registries().forEach(p_325687_ -> map.put(p_325687_.key(), Entry.m_322129_(p_325687_.value().asLookup())));
        p_311668_.forEach(p_325692_ -> map.put(p_325692_.key(), Entry.m_324954_(p_328219_, p_325692_)));
        return new HolderLookup.Provider() {
            @Override
            public Stream<ResourceKey<? extends Registry<?>>> m_305097_() {
                return map.keySet().stream();
            }

            <T> Optional<Entry<T>> m_321364_(ResourceKey<? extends Registry<? extends T>> p_332279_) {
                return Optional.ofNullable((Entry<T>)map.get(p_332279_));
            }

            @Override
            public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_328757_) {
                return this.m_321364_(p_328757_).map(Entry::f_314871_);
            }

            @Override
            public <V> RegistryOps<V> m_318927_(DynamicOps<V> p_334886_) {
                return RegistryOps.create(p_334886_, new RegistryOps.RegistryInfoLookup() {
                    @Override
                    public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_331561_) {
                        return m_321364_(p_331561_).map(Entry::f_315726_);
                    }
                });
            }
        };
    }

    public HolderLookup.Provider build(RegistryAccess pRegistryAccess) {
        RegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(pRegistryAccess);
        Stream<HolderLookup.RegistryLookup<?>> stream = this.entries
            .stream()
            .map(p_325689_ -> p_325689_.collectChanges(registrysetbuilder$buildstate).buildAsLookup(registrysetbuilder$buildstate.owner));
        HolderLookup.Provider holderlookup$provider = m_307871_(registrysetbuilder$buildstate.owner, pRegistryAccess, stream);
        registrysetbuilder$buildstate.m_307079_();
        registrysetbuilder$buildstate.m_306434_();
        registrysetbuilder$buildstate.throwOnError();
        return holderlookup$provider;
    }

    private HolderLookup.Provider m_305743_(
        RegistryAccess p_312999_,
        HolderLookup.Provider p_309815_,
        Cloner.Factory p_311992_,
        Map<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryContents<?>> p_309672_,
        HolderLookup.Provider p_312434_
    ) {
        RegistrySetBuilder.UniversalOwner registrysetbuilder$universalowner = new RegistrySetBuilder.UniversalOwner();
        MutableObject<HolderLookup.Provider> mutableobject = new MutableObject<>();
        List<HolderLookup.RegistryLookup<?>> list = p_309672_.keySet()
            .stream()
            .map(
                p_308443_ -> this.m_306817_(
                        registrysetbuilder$universalowner,
                        p_311992_,
                        (ResourceKey<? extends Registry<? extends Object>>)p_308443_,
                        p_312434_,
                        p_309815_,
                        mutableobject
                    )
            )
            .collect(Collectors.toUnmodifiableList());
        HolderLookup.Provider holderlookup$provider = m_307871_(registrysetbuilder$universalowner, p_312999_, list.stream());
        mutableobject.setValue(holderlookup$provider);
        return holderlookup$provider;
    }

    private <T> HolderLookup.RegistryLookup<T> m_306817_(
        HolderOwner<T> p_312548_,
        Cloner.Factory p_312934_,
        ResourceKey<? extends Registry<? extends T>> p_313093_,
        HolderLookup.Provider p_311682_,
        HolderLookup.Provider p_313198_,
        MutableObject<HolderLookup.Provider> p_311605_
    ) {
        Cloner<T> cloner = p_312934_.m_305497_(p_313093_);
        if (cloner == null) {
            throw new NullPointerException("No cloner for " + p_313093_.location());
        } else {
            Map<ResourceKey<T>, Holder.Reference<T>> map = new HashMap<>();
            HolderLookup.RegistryLookup<T> registrylookup = p_311682_.lookup(p_313093_).orElse(null);
            registrylookup.listElements().forEach(p_308453_ -> {
                ResourceKey<T> resourcekey = p_308453_.key();
                RegistrySetBuilder.LazyHolder<T> lazyholder = new RegistrySetBuilder.LazyHolder<>(p_312548_, resourcekey);
                lazyholder.f_303404_ = () -> cloner.m_306098_((T)p_308453_.value(), p_311682_, p_311605_.getValue());
                map.put(resourcekey, lazyholder);
            });
            HolderLookup.RegistryLookup<T> registrylookup1 = p_313198_.lookup(p_313093_).orElse(null);
            Lifecycle lifecycle = registrylookup.m_255098_();
            if (registrylookup1 != null) {
            registrylookup1.listElements().forEach(p_308430_ -> {
                ResourceKey<T> resourcekey = p_308430_.key();
                map.computeIfAbsent(resourcekey, p_308437_ -> {
                    RegistrySetBuilder.LazyHolder<T> lazyholder = new RegistrySetBuilder.LazyHolder<>(p_312548_, resourcekey);
                    lazyholder.f_303404_ = () -> cloner.m_306098_((T)p_308430_.value(), p_313198_, p_311605_.getValue());
                    return lazyholder;
                });
            });
            lifecycle = registrylookup.m_255098_().add(registrylookup1.m_255098_());
            }
            return m_305117_(p_313093_, lifecycle, p_312548_, map);
        }
    }

    public RegistrySetBuilder.PatchedRegistries buildPatch(RegistryAccess pRegistryAccess, HolderLookup.Provider pOriginal, Cloner.Factory p_310265_) {
        RegistrySetBuilder.BuildState registrysetbuilder$buildstate = this.createState(pRegistryAccess);
        Map<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryContents<?>> map = new HashMap<>();
        this.entries
            .stream()
            .map(p_308447_ -> p_308447_.collectChanges(registrysetbuilder$buildstate))
            .forEach(p_272339_ -> map.put(p_272339_.key, (RegistrySetBuilder.RegistryContents<?>)p_272339_));
        Set<ResourceKey<? extends Registry<?>>> set = pRegistryAccess.m_305097_().collect(Collectors.toUnmodifiableSet());
        pOriginal.m_305097_()
            .filter(p_308455_ -> !set.contains(p_308455_))
            .forEach(
                p_308463_ -> map.putIfAbsent(
                        (ResourceKey<? extends Registry<?>>)p_308463_,
                        new RegistrySetBuilder.RegistryContents<>((ResourceKey<? extends Registry<?>>)p_308463_, Lifecycle.stable(), Map.of())
                    )
            );
        Stream<HolderLookup.RegistryLookup<?>> stream = map.values().stream().map(p_325694_ -> p_325694_.buildAsLookup(registrysetbuilder$buildstate.owner));
        HolderLookup.Provider holderlookup$provider = m_307871_(registrysetbuilder$buildstate.owner, pRegistryAccess, stream);
        registrysetbuilder$buildstate.m_306434_();
        registrysetbuilder$buildstate.throwOnError();
        HolderLookup.Provider holderlookup$provider1 = this.m_305743_(pRegistryAccess, pOriginal, p_310265_, map, holderlookup$provider);
        return new RegistrySetBuilder.PatchedRegistries(holderlookup$provider1, holderlookup$provider);
    }

    static record BuildState(
        RegistrySetBuilder.UniversalOwner owner,
        RegistrySetBuilder.UniversalLookup lookup,
        Map<ResourceLocation, HolderGetter<?>> registries,
        Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues,
        List<RuntimeException> errors
    ) {
        public static RegistrySetBuilder.BuildState create(RegistryAccess pRegistryAccess, Stream<ResourceKey<? extends Registry<?>>> pRegistries) {
            RegistrySetBuilder.UniversalOwner registrysetbuilder$universalowner = new RegistrySetBuilder.UniversalOwner();
            List<RuntimeException> list = new ArrayList<>();
            RegistrySetBuilder.UniversalLookup registrysetbuilder$universallookup = new RegistrySetBuilder.UniversalLookup(registrysetbuilder$universalowner);
            Builder<ResourceLocation, HolderGetter<?>> builder = ImmutableMap.builder();
            pRegistryAccess.registries()
                .forEach(p_258197_ -> builder.put(p_258197_.key().location(), RegistrySetBuilder.wrapContextLookup(p_258197_.value().asLookup())));
            pRegistries.forEach(p_256603_ -> builder.put(p_256603_.location(), registrysetbuilder$universallookup));
            return new RegistrySetBuilder.BuildState(
                registrysetbuilder$universalowner, registrysetbuilder$universallookup, builder.build(), new HashMap<>(), list
            );
        }

        public <T> BootstrapContext<T> bootstapContext() {
            return new BootstrapContext<T>() {
                @Override
                public Holder.Reference<T> register(ResourceKey<T> p_256176_, T p_256422_, Lifecycle p_255924_) {
                    RegistrySetBuilder.RegisteredValue<?> registeredvalue = BuildState.this.registeredValues
                        .put(p_256176_, new RegistrySetBuilder.RegisteredValue(p_256422_, p_255924_));
                    if (registeredvalue != null) {
                        BuildState.this.errors
                            .add(
                                new IllegalStateException(
                                    "Duplicate registration for " + p_256176_ + ", new=" + p_256422_ + ", old=" + registeredvalue.value
                                )
                            );
                    }

                    return BuildState.this.lookup.getOrCreate(p_256176_);
                }

                @Override
                public <S> HolderGetter<S> m_255434_(ResourceKey<? extends Registry<? extends S>> p_255961_) {
                    return (HolderGetter<S>)BuildState.this.registries.getOrDefault(p_255961_.location(), BuildState.this.lookup);
                }

                @Override
                public <S> Optional<HolderLookup.RegistryLookup<S>> registryLookup(ResourceKey<? extends Registry<? extends S>> registry) {
                   return Optional.ofNullable((HolderLookup.RegistryLookup<S>) BuildState.this.registries.get(registry.location()));
                }
            };
        }

        public void m_306434_() {
            this.registeredValues
                .forEach(
                    (p_325695_, p_325696_) -> this.errors.add(new IllegalStateException("Orpaned value " + p_325696_.value + " for key " + p_325695_))
                );
        }

        public void m_307079_() {
            for (ResourceKey<Object> resourcekey : this.lookup.holders.keySet()) {
                this.errors.add(new IllegalStateException("Unreferenced key: " + resourcekey));
            }
        }

        public void throwOnError() {
            if (!this.errors.isEmpty()) {
                IllegalStateException illegalstateexception = new IllegalStateException("Errors during registry creation");

                for (RuntimeException runtimeexception : this.errors) {
                    illegalstateexception.addSuppressed(runtimeexception);
                }

                throw illegalstateexception;
            }
        }
    }

    abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
        protected final HolderOwner<T> owner;

        protected EmptyTagLookup(HolderOwner<T> pOwner) {
            this.owner = pOwner;
        }

        @Override
        public Optional<HolderSet.Named<T>> m_255050_(TagKey<T> pTagKey) {
            return Optional.of(HolderSet.emptyNamed(this.owner, pTagKey));
        }
    }

    static class EmptyTagLookupWrapper<T> extends RegistrySetBuilder.EmptyTagRegistryLookup<T> implements HolderLookup.RegistryLookup.Delegate<T> {
        private final HolderLookup.RegistryLookup<T> f_314163_;

        EmptyTagLookupWrapper(HolderOwner<T> p_327736_, HolderLookup.RegistryLookup<T> p_328715_) {
            super(p_327736_);
            this.f_314163_ = p_328715_;
        }

        @Override
        public HolderLookup.RegistryLookup<T> parent() {
            return this.f_314163_;
        }
    }

    abstract static class EmptyTagRegistryLookup<T> extends RegistrySetBuilder.EmptyTagLookup<T> implements HolderLookup.RegistryLookup<T> {
        protected EmptyTagRegistryLookup(HolderOwner<T> p_334522_) {
            super(p_334522_);
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            throw new UnsupportedOperationException("Tags are not available in datagen");
        }
    }

    static class LazyHolder<T> extends Holder.Reference<T> {
        @Nullable
        Supplier<T> f_303404_;

        protected LazyHolder(HolderOwner<T> p_311720_, @Nullable ResourceKey<T> p_312254_) {
            super(Holder.Reference.Type.STAND_ALONE, p_311720_, p_312254_, null);
        }

        @Override
        public void bindValue(T p_309503_) {
            super.bindValue(p_309503_);
            this.f_303404_ = null;
        }

        @Override
        public T value() {
            if (this.f_303404_ != null) {
                this.bindValue(this.f_303404_.get());
            }

            return super.value();
        }
    }

    public static record PatchedRegistries(HolderLookup.Provider f_303626_, HolderLookup.Provider f_302264_) {
    }

    static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
    }

    @FunctionalInterface
    public interface RegistryBootstrap<T> {
        void run(BootstrapContext<T> p_331285_);
    }

    static record RegistryContents<T>(
        ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values
    ) {
        public HolderLookup.RegistryLookup<T> buildAsLookup(RegistrySetBuilder.UniversalOwner p_333021_) {
            Map<ResourceKey<T>, Holder.Reference<T>> map = this.values
                .entrySet()
                .stream()
                .collect(
                    Collectors.toUnmodifiableMap(
                        java.util.Map.Entry::getKey,
                        p_311794_ -> {
                            RegistrySetBuilder.ValueAndHolder<T> valueandholder = p_311794_.getValue();
                            Holder.Reference<T> reference = valueandholder.holder()
                                .orElseGet(() -> Holder.Reference.createStandAlone(p_333021_.m_323396_(), p_311794_.getKey()));
                            reference.bindValue(valueandholder.value().value());
                            return reference;
                        }
                    )
                );
            return RegistrySetBuilder.m_305117_(this.key, this.lifecycle, p_333021_.m_323396_(), map);
        }
    }

    static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
        void apply(RegistrySetBuilder.BuildState pState) {
            this.bootstrap.run(pState.bootstapContext());
        }

        public RegistrySetBuilder.RegistryContents<T> collectChanges(RegistrySetBuilder.BuildState pState) {
            Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> map = new HashMap<>();
            Iterator<java.util.Map.Entry<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>>> iterator = pState.registeredValues.entrySet().iterator();

            while (iterator.hasNext()) {
                java.util.Map.Entry<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> entry = iterator.next();
                ResourceKey<?> resourcekey = entry.getKey();
                if (resourcekey.isFor(this.key)) {
                    RegistrySetBuilder.RegisteredValue<T> registeredvalue = (RegistrySetBuilder.RegisteredValue<T>)entry.getValue();
                    Holder.Reference<T> reference = (Holder.Reference<T>)pState.lookup.holders.remove(resourcekey);
                    map.put((ResourceKey<T>)resourcekey, new RegistrySetBuilder.ValueAndHolder<>(registeredvalue, Optional.ofNullable(reference)));
                    iterator.remove();
                }
            }

            return new RegistrySetBuilder.RegistryContents<>(this.key, this.lifecycle, map);
        }
    }

    static class UniversalLookup extends RegistrySetBuilder.EmptyTagLookup<Object> {
        final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap<>();

        public UniversalLookup(HolderOwner<Object> pOwner) {
            super(pOwner);
        }

        @Override
        public Optional<Holder.Reference<Object>> m_254926_(ResourceKey<Object> pResourceKey) {
            return Optional.of(this.getOrCreate(pResourceKey));
        }

        <T> Holder.Reference<T> getOrCreate(ResourceKey<T> pKey) {
            return (Holder.Reference<T>)((Map)this.holders)
                .computeIfAbsent(pKey, p_256154_ -> Holder.Reference.createStandAlone(this.owner, (ResourceKey<Object>)p_256154_));
        }
    }

    static class UniversalOwner implements HolderOwner<Object> {
        public <T> HolderOwner<T> m_323396_() {
            return (HolderOwner<T>)this;
        }
    }

    static record ValueAndHolder<T>(RegistrySetBuilder.RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
    }
}
