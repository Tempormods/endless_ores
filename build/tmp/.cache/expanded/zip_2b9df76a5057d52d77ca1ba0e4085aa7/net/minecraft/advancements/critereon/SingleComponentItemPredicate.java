package net.minecraft.advancements.critereon;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface SingleComponentItemPredicate<T> extends ItemSubPredicate {
    @Override
    default boolean m_321281_(ItemStack p_332866_) {
        T t = p_332866_.m_323252_(this.m_318698_());
        return t != null && this.m_318913_(p_332866_, t);
    }

    DataComponentType<T> m_318698_();

    boolean m_318913_(ItemStack p_329983_, T p_333057_);
}