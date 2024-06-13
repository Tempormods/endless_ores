package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class ArrayListDeque<T> extends AbstractList<T> implements ListAndDeque<T> {
    private static final int MIN_GROWTH = 1;
    private Object[] contents;
    private int head;
    private int size;

    public ArrayListDeque() {
        this(1);
    }

    public ArrayListDeque(int pSize) {
        this.contents = new Object[pSize];
        this.head = 0;
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @VisibleForTesting
    public int capacity() {
        return this.contents.length;
    }

    private int getIndex(int pIndex) {
        return (pIndex + this.head) % this.contents.length;
    }

    @Override
    public T get(int pIndex) {
        this.verifyIndexInRange(pIndex);
        return this.getInner(this.getIndex(pIndex));
    }

    private static void verifyIndexInRange(int pIndex, int pSize) {
        if (pIndex < 0 || pIndex >= pSize) {
            throw new IndexOutOfBoundsException(pIndex);
        }
    }

    private void verifyIndexInRange(int pIndex) {
        verifyIndexInRange(pIndex, this.size);
    }

    private T getInner(int pIndex) {
        return (T)this.contents[pIndex];
    }

    @Override
    public T set(int pIndex, T pValue) {
        this.verifyIndexInRange(pIndex);
        Objects.requireNonNull(pValue);
        int i = this.getIndex(pIndex);
        T t = this.getInner(i);
        this.contents[i] = pValue;
        return t;
    }

    @Override
    public void add(int pIndex, T pElement) {
        verifyIndexInRange(pIndex, this.size + 1);
        Objects.requireNonNull(pElement);
        if (this.size == this.contents.length) {
            this.grow();
        }

        int i = this.getIndex(pIndex);
        if (pIndex == this.size) {
            this.contents[i] = pElement;
        } else if (pIndex == 0) {
            this.head--;
            if (this.head < 0) {
                this.head = this.head + this.contents.length;
            }

            this.contents[this.getIndex(0)] = pElement;
        } else {
            for (int j = this.size - 1; j >= pIndex; j--) {
                this.contents[this.getIndex(j + 1)] = this.contents[this.getIndex(j)];
            }

            this.contents[i] = pElement;
        }

        this.modCount++;
        this.size++;
    }

    private void grow() {
        int i = this.contents.length + Math.max(this.contents.length >> 1, 1);
        Object[] aobject = new Object[i];
        this.copyCount(aobject, this.size);
        this.head = 0;
        this.contents = aobject;
    }

    @Override
    public T remove(int pIndex) {
        this.verifyIndexInRange(pIndex);
        int i = this.getIndex(pIndex);
        T t = this.getInner(i);
        if (pIndex == 0) {
            this.contents[i] = null;
            this.head++;
        } else if (pIndex == this.size - 1) {
            this.contents[i] = null;
        } else {
            for (int j = pIndex + 1; j < this.size; j++) {
                this.contents[this.getIndex(j - 1)] = this.get(j);
            }

            this.contents[this.getIndex(this.size - 1)] = null;
        }

        this.modCount++;
        this.size--;
        return t;
    }

    @Override
    public boolean removeIf(Predicate<? super T> pPredicate) {
        int i = 0;

        for (int j = 0; j < this.size; j++) {
            T t = this.get(j);
            if (pPredicate.test(t)) {
                i++;
            } else if (i != 0) {
                this.contents[this.getIndex(j - i)] = t;
                this.contents[this.getIndex(j)] = null;
            }
        }

        this.modCount += i;
        this.size -= i;
        return i != 0;
    }

    private void copyCount(Object[] pOutput, int pCount) {
        for (int i = 0; i < pCount; i++) {
            pOutput[i] = this.get(i);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<T> pOperator) {
        for (int i = 0; i < this.size; i++) {
            int j = this.getIndex(i);
            this.contents[j] = Objects.requireNonNull(pOperator.apply(this.getInner(i)));
        }
    }

    @Override
    public void forEach(Consumer<? super T> pAction) {
        for (int i = 0; i < this.size; i++) {
            pAction.accept(this.get(i));
        }
    }

    @Override
    public void addFirst(T pElement) {
        this.add(0, pElement);
    }

    @Override
    public void addLast(T pElement) {
        this.add(this.size, pElement);
    }

    @Override
    public boolean offerFirst(T pElement) {
        this.addFirst(pElement);
        return true;
    }

    @Override
    public boolean offerLast(T pElement) {
        this.addLast(pElement);
        return true;
    }

    @Override
    public T removeFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        } else {
            return this.remove(0);
        }
    }

    @Override
    public T removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        } else {
            return this.remove(this.size - 1);
        }
    }

    @Override
    public ListAndDeque<T> reversed() {
        return new ArrayListDeque.ReversedView(this);
    }

    @Nullable
    @Override
    public T pollFirst() {
        return this.size == 0 ? null : this.removeFirst();
    }

    @Nullable
    @Override
    public T pollLast() {
        return this.size == 0 ? null : this.removeLast();
    }

    @Override
    public T getFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        } else {
            return this.get(0);
        }
    }

    @Override
    public T getLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        } else {
            return this.get(this.size - 1);
        }
    }

    @Nullable
    @Override
    public T peekFirst() {
        return this.size == 0 ? null : this.getFirst();
    }

    @Nullable
    @Override
    public T peekLast() {
        return this.size == 0 ? null : this.getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object pElement) {
        for (int i = 0; i < this.size; i++) {
            T t = this.get(i);
            if (Objects.equals(pElement, t)) {
                this.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object pElement) {
        for (int i = this.size - 1; i >= 0; i--) {
            T t = this.get(i);
            if (Objects.equals(pElement, t)) {
                this.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<T> descendingIterator() {
        return new ArrayListDeque.DescendingIterator();
    }

    class DescendingIterator implements Iterator<T> {
        private int index = ArrayListDeque.this.size() - 1;

        public DescendingIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.index >= 0;
        }

        @Override
        public T next() {
            return ArrayListDeque.this.get(this.index--);
        }

        @Override
        public void remove() {
            ArrayListDeque.this.remove(this.index + 1);
        }
    }

    class ReversedView extends AbstractList<T> implements ListAndDeque<T> {
        private final ArrayListDeque<T> f_316355_;

        public ReversedView(final ArrayListDeque<T> p_335912_) {
            this.f_316355_ = p_335912_;
        }

        @Override
        public ListAndDeque<T> reversed() {
            return this.f_316355_;
        }

        @Override
        public T getFirst() {
            return this.f_316355_.getLast();
        }

        @Override
        public T getLast() {
            return this.f_316355_.getFirst();
        }

        @Override
        public void addFirst(T p_336272_) {
            this.f_316355_.addLast(p_336272_);
        }

        @Override
        public void addLast(T p_333987_) {
            this.f_316355_.addFirst(p_333987_);
        }

        @Override
        public boolean offerFirst(T p_331206_) {
            return this.f_316355_.offerLast(p_331206_);
        }

        @Override
        public boolean offerLast(T p_334399_) {
            return this.f_316355_.offerFirst(p_334399_);
        }

        @Override
        public T pollFirst() {
            return this.f_316355_.pollLast();
        }

        @Override
        public T pollLast() {
            return this.f_316355_.pollFirst();
        }

        @Override
        public T peekFirst() {
            return this.f_316355_.peekLast();
        }

        @Override
        public T peekLast() {
            return this.f_316355_.peekFirst();
        }

        @Override
        public T removeFirst() {
            return this.f_316355_.removeLast();
        }

        @Override
        public T removeLast() {
            return this.f_316355_.removeFirst();
        }

        @Override
        public boolean removeFirstOccurrence(Object p_332292_) {
            return this.f_316355_.removeLastOccurrence(p_332292_);
        }

        @Override
        public boolean removeLastOccurrence(Object p_328218_) {
            return this.f_316355_.removeFirstOccurrence(p_328218_);
        }

        @Override
        public Iterator<T> descendingIterator() {
            return this.f_316355_.iterator();
        }

        @Override
        public int size() {
            return this.f_316355_.size();
        }

        @Override
        public boolean isEmpty() {
            return this.f_316355_.isEmpty();
        }

        @Override
        public boolean contains(Object p_328039_) {
            return this.f_316355_.contains(p_328039_);
        }

        @Override
        public T get(int p_330114_) {
            return this.f_316355_.get(this.m_322291_(p_330114_));
        }

        @Override
        public T set(int p_328364_, T p_330947_) {
            return this.f_316355_.set(this.m_322291_(p_328364_), p_330947_);
        }

        @Override
        public void add(int p_328176_, T p_334553_) {
            this.f_316355_.add(this.m_322291_(p_328176_) + 1, p_334553_);
        }

        @Override
        public T remove(int p_334028_) {
            return this.f_316355_.remove(this.m_322291_(p_334028_));
        }

        @Override
        public int indexOf(Object p_330150_) {
            return this.m_322291_(this.f_316355_.lastIndexOf(p_330150_));
        }

        @Override
        public int lastIndexOf(Object p_332172_) {
            return this.m_322291_(this.f_316355_.indexOf(p_332172_));
        }

        @Override
        public List<T> subList(int p_331831_, int p_330462_) {
            return this.f_316355_.subList(this.m_322291_(p_330462_) + 1, this.m_322291_(p_331831_) + 1).reversed();
        }

        @Override
        public Iterator<T> iterator() {
            return this.f_316355_.descendingIterator();
        }

        @Override
        public void clear() {
            this.f_316355_.clear();
        }

        private int m_322291_(int p_335640_) {
            return p_335640_ == -1 ? -1 : this.f_316355_.size() - 1 - p_335640_;
        }
    }
}