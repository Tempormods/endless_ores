package net.minecraft.world.item.component;

import java.util.List;
import net.minecraft.server.network.Filterable;

public interface BookContent<T, C> {
    List<Filterable<T>> m_319402_();

    C m_319955_(List<Filterable<T>> p_328949_);
}