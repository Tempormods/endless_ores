package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;

public class ClassTreeIdRegistry {
    public static final int f_313901_ = -1;
    private final Object2IntMap<Class<?>> f_316601_ = Util.make(new Object2IntOpenHashMap<>(), p_327761_ -> p_327761_.defaultReturnValue(-1));

    public int m_321546_(Class<?> p_330417_) {
        int i = this.f_316601_.getInt(p_330417_);
        if (i != -1) {
            return i;
        } else {
            Class<?> oclass = p_330417_;

            while ((oclass = oclass.getSuperclass()) != Object.class) {
                int j = this.f_316601_.getInt(oclass);
                if (j != -1) {
                    return j;
                }
            }

            return -1;
        }
    }

    public int m_321486_(Class<?> p_335504_) {
        return this.m_321546_(p_335504_) + 1;
    }

    public int m_321864_(Class<?> p_334105_) {
        int i = this.m_321546_(p_334105_);
        int j = i == -1 ? 0 : i + 1;
        this.f_316601_.put(p_334105_, j);
        return j;
    }
}