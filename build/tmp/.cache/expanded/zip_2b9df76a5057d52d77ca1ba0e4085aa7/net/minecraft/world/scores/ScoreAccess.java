package net.minecraft.world.scores;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;

public interface ScoreAccess {
    int m_306505_();

    void m_305183_(int p_309760_);

    default int m_305196_(int p_310289_) {
        int i = this.m_306505_() + p_310289_;
        this.m_305183_(i);
        return i;
    }

    default int m_306809_() {
        return this.m_305196_(1);
    }

    default void m_307709_() {
        this.m_305183_(0);
    }

    boolean m_304717_();

    void m_305539_();

    void m_305263_();

    @Nullable
    Component m_305613_();

    void m_306789_(@Nullable Component p_313008_);

    void m_304839_(@Nullable NumberFormat p_310218_);
}