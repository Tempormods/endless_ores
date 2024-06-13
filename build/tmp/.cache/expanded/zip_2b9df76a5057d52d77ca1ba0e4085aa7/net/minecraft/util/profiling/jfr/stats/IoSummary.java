package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public final class IoSummary<T> {
    private final IoSummary.CountAndSize f_314697_;
    private final List<Pair<T, IoSummary.CountAndSize>> f_315261_;
    private final Duration f_314585_;

    public IoSummary(Duration p_336341_, List<Pair<T, IoSummary.CountAndSize>> p_328382_) {
        this.f_314585_ = p_336341_;
        this.f_314697_ = p_328382_.stream().map(Pair::getSecond).reduce(new IoSummary.CountAndSize(0L, 0L), IoSummary.CountAndSize::m_320497_);
        this.f_315261_ = p_328382_.stream().sorted(Comparator.comparing(Pair::getSecond, IoSummary.CountAndSize.f_315757_)).limit(10L).toList();
    }

    public double m_321055_() {
        return (double)this.f_314697_.f_314527_ / (double)this.f_314585_.getSeconds();
    }

    public double m_323182_() {
        return (double)this.f_314697_.f_316214_ / (double)this.f_314585_.getSeconds();
    }

    public long m_319079_() {
        return this.f_314697_.f_314527_;
    }

    public long m_324825_() {
        return this.f_314697_.f_316214_;
    }

    public List<Pair<T, IoSummary.CountAndSize>> m_324176_() {
        return this.f_315261_;
    }

    public static record CountAndSize(long f_314527_, long f_316214_) {
        static final Comparator<IoSummary.CountAndSize> f_315757_ = Comparator.comparing(IoSummary.CountAndSize::f_316214_)
            .thenComparing(IoSummary.CountAndSize::f_314527_)
            .reversed();

        IoSummary.CountAndSize m_320497_(IoSummary.CountAndSize p_335537_) {
            return new IoSummary.CountAndSize(this.f_314527_ + p_335537_.f_314527_, this.f_316214_ + p_335537_.f_316214_);
        }

        public float m_324761_() {
            return (float)this.f_316214_ / (float)this.f_314527_;
        }
    }
}