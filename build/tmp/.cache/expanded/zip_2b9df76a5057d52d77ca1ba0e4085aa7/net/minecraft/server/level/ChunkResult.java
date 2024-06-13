package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ChunkResult<T> {
    static <T> ChunkResult<T> m_323605_(T p_333970_) {
        return new ChunkResult.Success<>(p_333970_);
    }

    static <T> ChunkResult<T> m_322259_(String p_331314_) {
        return m_324523_(() -> p_331314_);
    }

    static <T> ChunkResult<T> m_324523_(Supplier<String> p_331628_) {
        return new ChunkResult.Fail<>(p_331628_);
    }

    boolean m_321137_();

    @Nullable
    T m_318814_(@Nullable T p_329164_);

    @Nullable
    static <R> R m_319813_(ChunkResult<? extends R> p_331028_, @Nullable R p_331551_) {
        R r = (R)p_331028_.m_318814_(null);
        return r != null ? r : p_331551_;
    }

    @Nullable
    String m_321629_();

    ChunkResult<T> m_320477_(Consumer<T> p_334389_);

    <R> ChunkResult<R> m_320014_(Function<T, R> p_334390_);

    <E extends Throwable> T m_319590_(Supplier<E> p_330106_) throws E;

    public static record Fail<T>(Supplier<String> f_314236_) implements ChunkResult<T> {
        @Override
        public boolean m_321137_() {
            return false;
        }

        @Nullable
        @Override
        public T m_318814_(@Nullable T p_330895_) {
            return p_330895_;
        }

        @Override
        public String m_321629_() {
            return this.f_314236_.get();
        }

        @Override
        public ChunkResult<T> m_320477_(Consumer<T> p_331855_) {
            return this;
        }

        @Override
        public <R> ChunkResult<R> m_320014_(Function<T, R> p_333275_) {
            return new ChunkResult.Fail(this.f_314236_);
        }

        @Override
        public <E extends Throwable> T m_319590_(Supplier<E> p_331734_) throws E {
            throw p_331734_.get();
        }
    }

    public static record Success<T>(T f_314875_) implements ChunkResult<T> {
        @Override
        public boolean m_321137_() {
            return true;
        }

        @Override
        public T m_318814_(@Nullable T p_332434_) {
            return this.f_314875_;
        }

        @Nullable
        @Override
        public String m_321629_() {
            return null;
        }

        @Override
        public ChunkResult<T> m_320477_(Consumer<T> p_328048_) {
            p_328048_.accept(this.f_314875_);
            return this;
        }

        @Override
        public <R> ChunkResult<R> m_320014_(Function<T, R> p_331436_) {
            return new ChunkResult.Success<>(p_331436_.apply(this.f_314875_));
        }

        @Override
        public <E extends Throwable> T m_319590_(Supplier<E> p_335933_) throws E {
            return this.f_314875_;
        }
    }
}