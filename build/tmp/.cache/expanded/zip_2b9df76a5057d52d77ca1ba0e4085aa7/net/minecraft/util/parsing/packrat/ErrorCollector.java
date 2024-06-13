package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;

public interface ErrorCollector<S> {
    void m_322006_(int p_334236_, SuggestionSupplier<S> p_329361_, Object p_331748_);

    default void m_323756_(int p_330627_, Object p_332187_) {
        this.m_322006_(p_330627_, SuggestionSupplier.m_319654_(), p_332187_);
    }

    void m_318862_(int p_334270_);

    public static class LongestOnly<S> implements ErrorCollector<S> {
        private final List<ErrorEntry<S>> f_315382_ = new ArrayList<>();
        private int f_316841_ = -1;

        private void m_321550_(int p_331637_) {
            if (p_331637_ > this.f_316841_) {
                this.f_316841_ = p_331637_;
                this.f_315382_.clear();
            }
        }

        @Override
        public void m_318862_(int p_334009_) {
            this.m_321550_(p_334009_);
        }

        @Override
        public void m_322006_(int p_331115_, SuggestionSupplier<S> p_329965_, Object p_332125_) {
            this.m_321550_(p_331115_);
            if (p_331115_ == this.f_316841_) {
                this.f_315382_.add(new ErrorEntry<>(p_331115_, p_329965_, p_332125_));
            }
        }

        public List<ErrorEntry<S>> m_321518_() {
            return this.f_315382_;
        }

        public int m_323319_() {
            return this.f_316841_;
        }
    }
}