package net.minecraft.world.level.storage.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface ContainerComponentManipulator<T> {
    DataComponentType<T> m_319799_();

    T m_320702_();

    T m_318985_(T p_331842_, Stream<ItemStack> p_327717_);

    Stream<ItemStack> m_321528_(T p_336301_);

    default void m_321977_(ItemStack p_333844_, T p_334408_, Stream<ItemStack> p_331739_) {
        T t = p_333844_.m_322304_(this.m_319799_(), p_334408_);
        T t1 = this.m_318985_(t, p_331739_);
        p_333844_.m_322496_(this.m_319799_(), t1);
    }

    default void m_321924_(ItemStack p_331343_, Stream<ItemStack> p_333653_) {
        this.m_321977_(p_331343_, this.m_320702_(), p_333653_);
    }

    default void m_319566_(ItemStack p_335094_, UnaryOperator<ItemStack> p_330990_) {
        T t = p_335094_.m_323252_(this.m_319799_());
        if (t != null) {
            UnaryOperator<ItemStack> unaryoperator = p_327931_ -> p_327931_.isEmpty() ? p_327931_ : p_330990_.apply(p_327931_);
            this.m_321924_(p_335094_, this.m_321528_(t).map(unaryoperator));
        }
    }
}