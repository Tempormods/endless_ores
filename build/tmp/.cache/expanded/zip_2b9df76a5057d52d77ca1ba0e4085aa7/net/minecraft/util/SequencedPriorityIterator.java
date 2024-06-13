package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.Deque;
import javax.annotation.Nullable;

public final class SequencedPriorityIterator<T> extends AbstractIterator<T> {
    private static final int f_315833_ = Integer.MIN_VALUE;
    @Nullable
    private Deque<T> f_314101_ = null;
    private int f_316037_ = Integer.MIN_VALUE;
    private final Int2ObjectMap<Deque<T>> f_313918_ = new Int2ObjectOpenHashMap<>();

    public void m_307049_(T p_312570_, int p_312199_) {
        if (p_312199_ == this.f_316037_ && this.f_314101_ != null) {
            this.f_314101_.addLast(p_312570_);
        } else {
            Deque<T> deque = this.f_313918_.computeIfAbsent(p_312199_, p_310516_ -> Queues.newArrayDeque());
            deque.addLast(p_312570_);
            if (p_312199_ >= this.f_316037_) {
                this.f_314101_ = deque;
                this.f_316037_ = p_312199_;
            }
        }
    }

    @Nullable
    @Override
    protected T computeNext() {
        if (this.f_314101_ == null) {
            return this.endOfData();
        } else {
            T t = this.f_314101_.removeFirst();
            if (t == null) {
                return this.endOfData();
            } else {
                if (this.f_314101_.isEmpty()) {
                    this.m_323324_();
                }

                return t;
            }
        }
    }

    private void m_323324_() {
        int i = Integer.MIN_VALUE;
        Deque<T> deque = null;

        for (Entry<Deque<T>> entry : Int2ObjectMaps.fastIterable(this.f_313918_)) {
            Deque<T> deque1 = entry.getValue();
            int j = entry.getIntKey();
            if (j > i && !deque1.isEmpty()) {
                i = j;
                deque = deque1;
                if (j == this.f_316037_ - 1) {
                    break;
                }
            }
        }

        this.f_316037_ = i;
        this.f_314101_ = deque;
    }
}