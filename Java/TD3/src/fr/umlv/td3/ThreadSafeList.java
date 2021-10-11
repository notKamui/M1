package fr.umlv.td3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public final class ThreadSafeList<E> implements Iterable<E> {

    private final ArrayList<E> internal = new ArrayList<>();

    public void add(E e) throws NullPointerException, UnsupportedOperationException, IllegalArgumentException, ClassCastException {
        Objects.requireNonNull(e);
        synchronized (internal) {
            internal.add(e);
        }
    }

    public E remove(int index) throws IndexOutOfBoundsException, UnsupportedOperationException {
        synchronized (internal) {
            return internal.remove(index);
        }
    }

    public E get(int index) throws IndexOutOfBoundsException {
        synchronized (internal) {
            return internal.get(index);
        }
    }

    public int size() {
        return internal.size();
    }

    @Override
    public Iterator<E> iterator() {
        synchronized (internal) {
            return internal.iterator();
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        synchronized (internal) {
            for (var e : internal) {
                action.accept(e);
            }
        }
    }
}
