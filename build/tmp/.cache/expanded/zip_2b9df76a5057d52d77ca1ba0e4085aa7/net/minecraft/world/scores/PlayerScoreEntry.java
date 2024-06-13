package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public record PlayerScoreEntry(String f_302847_, int f_303807_, @Nullable Component f_303157_, @Nullable NumberFormat f_303706_) {
    public boolean m_307477_() {
        return this.f_302847_.startsWith("#");
    }

    public Component m_305530_() {
        return (Component)(this.f_303157_ != null ? this.f_303157_ : Component.literal(this.f_302847_()));
    }

    public MutableComponent m_304640_(NumberFormat p_312365_) {
        return Objects.requireNonNullElse(this.f_303706_, p_312365_).m_305266_(this.f_303807_);
    }
}