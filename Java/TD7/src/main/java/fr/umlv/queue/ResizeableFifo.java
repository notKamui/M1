package fr.umlv.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;

public class ResizeableFifo<E> extends AbstractQueue<E> implements Iterable<E>, Queue<E> {
    private E[] internal;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public ResizeableFifo(int initialSize) {
        if (initialSize <= 0) throw new IllegalArgumentException("initialSize must be strictly positive");
        internal = (E[]) new Object[initialSize];
    }

    @Override
    public boolean offer(E e) {
        if (size >= internal.length / 2) grow();
        Objects.requireNonNull(e);
        internal[tail] = e;
        tail = wrapInc(tail, internal.length);
        size++;
        return true;
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        var e = internal[head];
        internal[head] = null;
        head = wrapInc(head, internal.length);
        size--;
        return e;
    }

    @Override
    public E peek() {
        return internal[head];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int h = head;
            private int remaining = size;

            @Override
            public boolean hasNext() {
                return remaining > 0;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Iterator has no next");
                }
                var e = internal[h];
                h = wrapInc(h, internal.length);
                remaining--;
                return e;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void grow() {
        var newInternal = (E[]) new Object[internal.length * 2];
        System.arraycopy(internal, head, newInternal, 0, internal.length - head);
        System.arraycopy(internal, 0, newInternal, internal.length - head, tail);
        internal = newInternal;
        head = 0;
        tail = size;
    }

    private static int wrapInc(int i, int mod) {
        i++;
        if (i == mod) {
            i = 0;
        }
        return i;
    }
}
