package net.minecraft.core;

public interface HolderOwner<T> {
    default boolean m_255301_(HolderOwner<T> pOwner) {
        return pOwner == this;
    }
}