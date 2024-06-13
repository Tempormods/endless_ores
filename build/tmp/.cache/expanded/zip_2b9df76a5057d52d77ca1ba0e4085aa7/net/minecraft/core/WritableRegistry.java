package net.minecraft.core;

import net.minecraft.resources.ResourceKey;

public interface WritableRegistry<T> extends Registry<T> {
    Holder.Reference<T> register(ResourceKey<T> pKey, T pValue, RegistrationInfo p_329112_);

    boolean isEmpty();

    HolderGetter<T> createRegistrationLookup();
}