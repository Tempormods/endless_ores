package net.minecraft.core.component;

import javax.annotation.Nullable;

public interface DataComponentHolder {
    DataComponentMap m_318732_();

    @Nullable
    default <T> T m_323252_(DataComponentType<? extends T> p_331483_) {
        return this.m_318732_().m_318834_(p_331483_);
    }

    default <T> T m_322304_(DataComponentType<? extends T> p_328483_, T p_333219_) {
        return this.m_318732_().m_322806_(p_328483_, p_333219_);
    }

    default boolean m_319951_(DataComponentType<?> p_333597_) {
        return this.m_318732_().m_321946_(p_333597_);
    }
}