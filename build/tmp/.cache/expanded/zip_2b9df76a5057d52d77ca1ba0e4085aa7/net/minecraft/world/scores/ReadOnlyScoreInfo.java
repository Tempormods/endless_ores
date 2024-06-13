package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public interface ReadOnlyScoreInfo {
    int m_305685_();

    boolean isLocked();

    @Nullable
    NumberFormat m_305750_();

    default MutableComponent m_307457_(NumberFormat p_313073_) {
        return Objects.requireNonNullElse(this.m_305750_(), p_313073_).m_305266_(this.m_305685_());
    }

    static MutableComponent m_305849_(@Nullable ReadOnlyScoreInfo p_312063_, NumberFormat p_312422_) {
        return p_312063_ != null ? p_312063_.m_307457_(p_312422_) : p_312422_.m_305266_(0);
    }
}