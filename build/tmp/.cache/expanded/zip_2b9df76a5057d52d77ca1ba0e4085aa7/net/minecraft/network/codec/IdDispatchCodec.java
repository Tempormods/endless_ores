package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.VarInt;

public class IdDispatchCodec<B extends ByteBuf, V, T> implements StreamCodec<B, V> {
    private static final int f_314336_ = -1;
    private final Function<V, ? extends T> f_316164_;
    private final List<IdDispatchCodec.Entry<B, V, T>> f_316269_;
    private final Object2IntMap<T> f_316567_;

    IdDispatchCodec(Function<V, ? extends T> p_330610_, List<IdDispatchCodec.Entry<B, V, T>> p_334834_, Object2IntMap<T> p_327784_) {
        this.f_316164_ = p_330610_;
        this.f_316269_ = p_334834_;
        this.f_316567_ = p_327784_;
    }

    public V m_318688_(B p_327793_) {
        int i = VarInt.read(p_327793_);
        if (i >= 0 && i < this.f_316269_.size()) {
            IdDispatchCodec.Entry<B, V, T> entry = this.f_316269_.get(i);

            try {
                return (V)entry.f_314678_.m_318688_(p_327793_);
            } catch (Exception exception) {
                throw new DecoderException("Failed to decode packet '" + entry.f_316955_ + "'", exception);
            }
        } else {
            throw new DecoderException("Received unknown packet id " + i);
        }
    }

    public void m_318638_(B p_336072_, V p_327912_) {
        T t = (T)this.f_316164_.apply(p_327912_);
        int i = this.f_316567_.getOrDefault(t, -1);
        if (i == -1) {
            throw new EncoderException("Sending unknown packet '" + t + "'");
        } else {
            VarInt.write(p_336072_, i);
            IdDispatchCodec.Entry<B, V, T> entry = this.f_316269_.get(i);

            try {
                StreamCodec<? super B, V> streamcodec = (StreamCodec<? super B, V>)entry.f_314678_;
                streamcodec.m_318638_(p_336072_, p_327912_);
            } catch (Exception exception) {
                throw new EncoderException("Failed to encode packet '" + t + "'", exception);
            }
        }
    }

    public static <B extends ByteBuf, V, T> IdDispatchCodec.Builder<B, V, T> m_323921_(Function<V, ? extends T> p_331962_) {
        return new IdDispatchCodec.Builder<>(p_331962_);
    }

    public static class Builder<B extends ByteBuf, V, T> {
        private final List<IdDispatchCodec.Entry<B, V, T>> f_315818_ = new ArrayList<>();
        private final Function<V, ? extends T> f_316945_;

        Builder(Function<V, ? extends T> p_330341_) {
            this.f_316945_ = p_330341_;
        }

        public IdDispatchCodec.Builder<B, V, T> m_321255_(T p_333313_, StreamCodec<? super B, ? extends V> p_330239_) {
            this.f_315818_.add(new IdDispatchCodec.Entry<>(p_330239_, p_333313_));
            return this;
        }

        public IdDispatchCodec<B, V, T> m_324285_() {
            Object2IntOpenHashMap<T> object2intopenhashmap = new Object2IntOpenHashMap<>();
            object2intopenhashmap.defaultReturnValue(-2);

            for (IdDispatchCodec.Entry<B, V, T> entry : this.f_315818_) {
                int i = object2intopenhashmap.size();
                int j = object2intopenhashmap.putIfAbsent(entry.f_316955_, i);
                if (j != -2) {
                    throw new IllegalStateException("Duplicate registration for type " + entry.f_316955_);
                }
            }

            return new IdDispatchCodec<>(this.f_316945_, List.copyOf(this.f_315818_), object2intopenhashmap);
        }
    }

    static record Entry<B, V, T>(StreamCodec<? super B, ? extends V> f_314678_, T f_316955_) {
    }
}