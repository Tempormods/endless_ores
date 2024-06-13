package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
    int DEFAULT = -1;

    int getId(T pValue);

    @Nullable
    T byId(int pId);

    default T byIdOrThrow(int pId) {
        T t = this.byId(pId);
        if (t == null) {
            throw new IllegalArgumentException("No value with id " + pId);
        } else {
            return t;
        }
    }

    default int m_322535_(T p_329088_) {
        int i = this.getId(p_329088_);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + p_329088_ + "' in map " + this);
        } else {
            return i;
        }
    }

    int size();
}