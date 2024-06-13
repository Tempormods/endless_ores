package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class MappedRegistry<T> implements WritableRegistry<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final ResourceKey<? extends Registry<T>> key;
    private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList<>(256);
    private final Reference2IntMap<T> toId = Util.make(new Reference2IntOpenHashMap<>(), p_308420_ -> p_308420_.defaultReturnValue(-1));
    private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap<>();
    private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap<>();
    private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap<>();
    private final Map<ResourceKey<T>, RegistrationInfo> f_314552_ = new IdentityHashMap<>();
    private Lifecycle registryLifecycle;
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();
    private boolean frozen;
    @Nullable
    protected Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;
    private final HolderLookup.RegistryLookup<T> lookup = new HolderLookup.RegistryLookup<T>() {
        @Override
        public ResourceKey<? extends Registry<? extends T>> key() {
            return MappedRegistry.this.key;
        }

        @Override
        public Lifecycle m_255098_() {
            return MappedRegistry.this.registryLifecycle();
        }

        @Override
        public Optional<Holder.Reference<T>> m_254926_(ResourceKey<T> p_255624_) {
            return MappedRegistry.this.getHolder(p_255624_);
        }

        @Override
        public Stream<Holder.Reference<T>> listElements() {
            return MappedRegistry.this.holders();
        }

        @Override
        public Optional<HolderSet.Named<T>> m_255050_(TagKey<T> p_256277_) {
            return MappedRegistry.this.getTag(p_256277_);
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            return MappedRegistry.this.getTags().map(Pair::getSecond);
        }
    };
    private final Object f_336400_ = new Object();

    public MappedRegistry(ResourceKey<? extends Registry<T>> pKey, Lifecycle pRegistryLifecycle) {
        this(pKey, pRegistryLifecycle, false);
    }

    public MappedRegistry(ResourceKey<? extends Registry<T>> pKey, Lifecycle pRegistryLifecycle, boolean pHasIntrusiveHolders) {
        this.key = pKey;
        this.registryLifecycle = pRegistryLifecycle;
        if (pHasIntrusiveHolders) {
            this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
        }
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.key;
    }

    @Override
    public String toString() {
        return "Registry[" + this.key + " (" + this.registryLifecycle + ")]";
    }

    private void validateWrite() {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen");
        }
    }

    private void validateWrite(ResourceKey<T> pKey) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + pKey + ")");
        }
    }

    @Override
    public Holder.Reference<T> register(ResourceKey<T> pKey, T pValue, RegistrationInfo p_329661_) {
        markKnown();
        this.validateWrite(pKey);
        Objects.requireNonNull(pKey);
        Objects.requireNonNull(pValue);
        if (this.byLocation.containsKey(pKey.location())) {
            Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + pKey + "' to registry"));
        }

        if (this.byValue.containsKey(pValue)) {
            Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + pValue + "' to registry"));
        }

        Holder.Reference<T> reference;
        if (this.unregisteredIntrusiveHolders != null) {
            reference = this.unregisteredIntrusiveHolders.remove(pValue);
            if (reference == null) {
                throw new AssertionError("Missing intrusive holder for " + pKey + ":" + pValue);
            }

            reference.bindKey(pKey);
        } else {
            reference = this.byKey.computeIfAbsent(pKey, p_258168_ -> Holder.Reference.createStandAlone(this.holderOwner(), (ResourceKey<T>)p_258168_));
            // Forge: Bind the value immediately so it can be queried while the registry is not frozen
            reference.bindValue(pValue);
        }

        this.byKey.put(pKey, reference);
        this.byLocation.put(pKey.location(), reference);
        this.byValue.put(pValue, reference);
        int i = this.byId.size();
        this.byId.add(reference);
        this.toId.put(pValue, i);
        this.f_314552_.put(pKey, p_329661_);
        this.registryLifecycle = this.registryLifecycle.add(p_329661_.f_313951_());
        return reference;
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T pValue) {
        Holder.Reference<T> reference = this.byValue.get(pValue);
        return reference != null ? reference.key().location() : null;
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T pValue) {
        return Optional.ofNullable(this.byValue.get(pValue)).map(Holder.Reference::key);
    }

    @Override
    public int getId(@Nullable T pValue) {
        return this.toId.getInt(pValue);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceKey<T> pKey) {
        return getValueFromNullable(this.byKey.get(pKey));
    }

    @Nullable
    @Override
    public T byId(int pId) {
        return pId >= 0 && pId < this.byId.size() ? this.byId.get(pId).value() : null;
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(int pId) {
        return pId >= 0 && pId < this.byId.size() ? Optional.ofNullable(this.byId.get(pId)) : Optional.empty();
    }

    @Override
    public Optional<Holder.Reference<T>> m_320017_(ResourceLocation p_333710_) {
        return Optional.ofNullable(this.byLocation.get(p_333710_));
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> pKey) {
        return Optional.ofNullable(this.byKey.get(pKey));
    }

    @Override
    public Holder<T> wrapAsHolder(T pValue) {
        Holder.Reference<T> reference = this.byValue.get(pValue);
        return (Holder<T>)(reference != null ? reference : Holder.direct(pValue));
    }

    protected Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> pKey) {
        return this.byKey.computeIfAbsent(pKey, p_258169_ -> {
            if (this.unregisteredIntrusiveHolders != null) {
                throw new IllegalStateException("This registry can't create new holders without value");
            } else {
                this.validateWrite((ResourceKey<T>)p_258169_);
                return Holder.Reference.createStandAlone(this.holderOwner(), (ResourceKey<T>)p_258169_);
            }
        });
    }

    @Override
    public int size() {
        return this.byKey.size();
    }

    @Override
    public Optional<RegistrationInfo> lifecycle(ResourceKey<T> p_331530_) {
        return Optional.ofNullable(this.f_314552_.get(p_331530_));
    }

    @Override
    public Lifecycle registryLifecycle() {
        return this.registryLifecycle;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.transform(this.byId.iterator(), Holder::value);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation pName) {
        Holder.Reference<T> reference = this.byLocation.get(pName);
        return getValueFromNullable(reference);
    }

    @Nullable
    private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> pHolder) {
        return pHolder != null ? pHolder.value() : null;
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return Collections.unmodifiableSet(this.byLocation.keySet());
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return Collections.unmodifiableSet(this.byKey.keySet());
    }

    @Override
    public Set<Entry<ResourceKey<T>, T>> entrySet() {
        return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Holder::value).entrySet());
    }

    @Override
    public Stream<Holder.Reference<T>> holders() {
        return this.byId.stream();
    }

    @Override
    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return this.tags.entrySet().stream().map(p_211060_ -> Pair.of(p_211060_.getKey(), p_211060_.getValue()));
    }

    @Override
    public HolderSet.Named<T> getOrCreateTag(TagKey<T> pKey) {
        HolderSet.Named<T> named = this.tags.get(pKey);
        if (named != null) {
            return named;
        } else {
            synchronized (this.f_336400_) {
                named = this.tags.get(pKey);
                if (named != null) {
                    return named;
                } else {
                    named = this.createTag(pKey);
                    Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap<>(this.tags);
                    map.put(pKey, named);
                    this.tags = map;
                    return named;
                }
            }
        }
    }

    private HolderSet.Named<T> createTag(TagKey<T> p_211068_) {
        return new HolderSet.Named<>(this.holderOwner(), p_211068_);
    }

    @Override
    public Stream<TagKey<T>> getTagNames() {
        return this.tags.keySet().stream();
    }

    @Override
    public boolean isEmpty() {
        return this.byKey.isEmpty();
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource pRandom) {
        return Util.getRandomSafe(this.byId, pRandom);
    }

    @Override
    public boolean containsKey(ResourceLocation pName) {
        return this.byLocation.containsKey(pName);
    }

    @Override
    public boolean containsKey(ResourceKey<T> pKey) {
        return this.byKey.containsKey(pKey);
    }

    @Override
    public Registry<T> freeze() {
        if (this.frozen) {
            return this;
        } else {
            this.frozen = true;
            List<ResourceLocation> list = this.byKey
                .entrySet()
                .stream()
                .filter(p_211055_ -> !p_211055_.getValue().isBound())
                .map(p_211794_ -> p_211794_.getKey().location())
                .sorted()
                .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + list);
            } else {
                if (this.unregisteredIntrusiveHolders != null) {
                    if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                        throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values());
                    }

                    // Forge: We freeze/unfreeze vanilla registries more than once, so we need to keep the unregistered intrusive holders map around.
                    //this.unregisteredIntrusiveHolders = null;
                }

                return this;
            }
        }
    }

    @Override
    public Holder.Reference<T> createIntrusiveHolder(T pValue) {
        if (this.unregisteredIntrusiveHolders == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        } else {
            this.validateWrite();
            return this.unregisteredIntrusiveHolders.computeIfAbsent(pValue, p_258166_ -> Holder.Reference.createIntrusive(this.asLookup(), (T)p_258166_));
        }
    }

    @Override
    public Optional<HolderSet.Named<T>> getTag(TagKey<T> pKey) {
        return Optional.ofNullable(this.tags.get(pKey));
    }

    @Override
    public void bindTags(Map<TagKey<T>, List<Holder<T>>> pTagMap) {
        Map<Holder.Reference<T>, List<TagKey<T>>> map = new IdentityHashMap<>();
        this.byKey.values().forEach(p_211801_ -> map.put((Holder.Reference<T>)p_211801_, new ArrayList<>()));
        pTagMap.forEach((p_325673_, p_325674_) -> {
            for (Holder<T> holder : p_325674_) {
                if (!holder.canSerializeIn(this.asLookup())) {
                    throw new IllegalStateException("Can't create named set " + p_325673_ + " containing value " + holder + " from outside registry " + this);
                }

                if (!(holder instanceof Holder.Reference<T> reference)) {
                    throw new IllegalStateException("Found direct holder " + holder + " value in tag " + p_325673_);
                }

                map.get(reference).add((TagKey<T>)p_325673_);
            }
        });
        Set<TagKey<T>> set = Sets.difference(this.tags.keySet(), pTagMap.keySet());
        if (!set.isEmpty()) {
            LOGGER.warn(
                "Not all defined tags for registry {} are present in data pack: {}",
                this.key(),
                set.stream().map(p_211811_ -> p_211811_.location().toString()).sorted().collect(Collectors.joining(", "))
            );
        }

        synchronized (this.f_336400_) {
            Map<TagKey<T>, HolderSet.Named<T>> map1 = new IdentityHashMap<>(this.tags);
            pTagMap.forEach((p_211797_, p_211798_) -> map1.computeIfAbsent((TagKey<T>)p_211797_, this::createTag).bind((List<Holder<T>>)p_211798_));
            map.forEach(Holder.Reference::bindTags);
            this.tags = map1;
        }
    }

    @Override
    public void resetTags() {
        this.tags.values().forEach(p_211792_ -> p_211792_.bind(List.of()));
        this.byKey.values().forEach(p_211803_ -> p_211803_.bindTags(Set.of()));
    }

    @Override
    public HolderGetter<T> createRegistrationLookup() {
        this.validateWrite();
        return new HolderGetter<T>() {
            @Override
            public Optional<Holder.Reference<T>> m_254926_(ResourceKey<T> p_259097_) {
                return Optional.of(this.getOrThrow(p_259097_));
            }

            @Override
            public Holder.Reference<T> getOrThrow(ResourceKey<T> p_259750_) {
                return MappedRegistry.this.getOrCreateHolderOrThrow(p_259750_);
            }

            @Override
            public Optional<HolderSet.Named<T>> m_255050_(TagKey<T> p_259486_) {
                return Optional.of(this.getOrThrow(p_259486_));
            }

            @Override
            public HolderSet.Named<T> getOrThrow(TagKey<T> p_260298_) {
                return MappedRegistry.this.getOrCreateTag(p_260298_);
            }
        };
    }

    @Override
    public HolderOwner<T> holderOwner() {
        return this.lookup;
    }

    @Override
    public HolderLookup.RegistryLookup<T> asLookup() {
        return this.lookup;
    }

    public boolean isIntrusive() {
        return this.unregisteredIntrusiveHolders != null;
    }

    private static final Set<ResourceLocation> KNOWN = new java.util.LinkedHashSet<>();
    public static Set<ResourceLocation> getKnownRegistries() {
        return java.util.Collections.unmodifiableSet(KNOWN);
    }

    protected final void markKnown() {
        KNOWN.add(key().location());
    }

    /** @deprecated Forge: For internal use only. Use the Register events when registering values. */
    @Deprecated
    public void unfreeze() {
        this.frozen = false;
    }
}