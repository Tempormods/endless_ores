package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> implements DefaultedRegistry<T> {
    private final ResourceLocation defaultKey;
    private Holder.Reference<T> defaultValue;

    public DefaultedMappedRegistry(String pDefaultKey, ResourceKey<? extends Registry<T>> pKey, Lifecycle pRegistryLifecycle, boolean pHasIntrusiveHolders) {
        super(pKey, pRegistryLifecycle, pHasIntrusiveHolders);
        this.defaultKey = new ResourceLocation(pDefaultKey);
    }

    @Override
    public Holder.Reference<T> register(ResourceKey<T> p_332872_, T p_328327_, RegistrationInfo p_331941_) {
        Holder.Reference<T> reference = super.register(p_332872_, p_328327_, p_331941_);
        if (this.defaultKey.equals(p_332872_.location())) {
            this.defaultValue = reference;
        }

        return reference;
    }

    @Override
    public int getId(@Nullable T pValue) {
        int i = super.getId(pValue);
        return i == -1 ? super.getId(this.defaultValue.value()) : i;
    }

    @Nonnull
    @Override
    public ResourceLocation getKey(T pValue) {
        ResourceLocation resourcelocation = super.getKey(pValue);
        return resourcelocation == null ? this.defaultKey : resourcelocation;
    }

    @Nonnull
    @Override
    public T get(@Nullable ResourceLocation pName) {
        T t = super.get(pName);
        return t == null && this.defaultValue != null ? this.defaultValue.value() : t; // Add null check as it errors out in validation
    }

    @Override
    public Optional<T> getOptional(@Nullable ResourceLocation pName) {
        return Optional.ofNullable(super.get(pName));
    }

    @Nonnull
    @Override
    public T byId(int pId) {
        T t = super.byId(pId);
        return t == null ? this.defaultValue.value() : t;
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource pRandom) {
        return super.getRandom(pRandom).or(() -> Optional.of(this.defaultValue));
    }

    @Override
    public ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }
}
