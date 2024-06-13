package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ProblemReporter {
    ProblemReporter m_306146_(String p_311126_);

    void m_305802_(String p_312072_);

    public static class Collector implements ProblemReporter {
        private final Multimap<String, String> f_302906_;
        private final Supplier<String> f_303400_;
        @Nullable
        private String f_302537_;

        public Collector() {
            this(HashMultimap.create(), () -> "");
        }

        private Collector(Multimap<String, String> p_311018_, Supplier<String> p_312668_) {
            this.f_302906_ = p_311018_;
            this.f_303400_ = p_312668_;
        }

        private String m_304869_() {
            if (this.f_302537_ == null) {
                this.f_302537_ = this.f_303400_.get();
            }

            return this.f_302537_;
        }

        @Override
        public ProblemReporter m_306146_(String p_311756_) {
            return new ProblemReporter.Collector(this.f_302906_, () -> this.m_304869_() + p_311756_);
        }

        @Override
        public void m_305802_(String p_310299_) {
            this.f_302906_.put(this.m_304869_(), p_310299_);
        }

        public Multimap<String, String> m_306090_() {
            return ImmutableMultimap.copyOf(this.f_302906_);
        }
    }
}