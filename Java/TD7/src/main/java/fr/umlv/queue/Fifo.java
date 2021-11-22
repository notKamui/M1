package fr.umlv.queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E> {
    private final E[] internal;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public Fifo(int initialSize) {
        if (initialSize <= 0) throw new IllegalArgumentException("initialSize must be strictly positive");
        internal = (E[]) new Object[initialSize];
    }

    public void offer(E e) {
        if (size == internal.length) throw new IllegalStateException("FIFO is full");
        Objects.requireNonNull(e);
        internal[tail] = e;
        tail = wrapInc(tail, internal.length);
        size++;
    }

    public E poll() {
        if (isEmpty()) throw new IllegalStateException("FIFO is empty");
        var e = internal[head];
        internal[head] = null;
        head = wrapInc(head, internal.length);
        size--;
        return e;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

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

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        var sj = new StringJoiner(", ", "[", "]");
        var i = head;
        do {
            sj.add(internal[i].toString());
            i = wrapInc(i, internal.length);
        } while (i != tail);
        return sj.toString();
    }

    private static int wrapInc(int i, int mod) {
        i++;
        if (i == mod) {
            i = 0;
        }
        return i;
    }
}
