package fr.umlv.td3;

import java.util.*;
import java.util.function.Consumer;

public final class ThreadSafeList<E> implements List<E> {

    private final ArrayList<E> internal = new ArrayList<>();

    @Override
    public boolean add(E e) {
        Objects.requireNonNull(e);
        synchronized (internal) {
            return internal.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        Objects.requireNonNull(o);
        synchronized (internal) {
            return internal.remove(o);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c);
        synchronized (internal) {
            return internal.containsAll(c);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        synchronized (internal) {
            return internal.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        synchronized (internal) {
            return internal.addAll(index, c);
        }
    }

    @Override
    public E remove(int index) throws IndexOutOfBoundsException, UnsupportedOperationException {
        synchronized (internal) {
            return internal.remove(index);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        synchronized (internal) {
            return internal.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        synchronized (internal) {
            return internal.retainAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (internal) {
            internal.clear();
        }
    }

    @Override
    public int indexOf(Object o) {
        Objects.requireNonNull(o);
        synchronized (internal) {
            return internal.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        Objects.requireNonNull(o);
        synchronized (internal) {
            return internal.lastIndexOf(o);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        synchronized (internal) {
            return internal.listIterator();
        }
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        synchronized (internal) {
            return internal.listIterator(index);
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        synchronized (internal) {
            return internal.subList(fromIndex, toIndex);
        }
    }

    @Override
    public E get(int index) {
        synchronized (internal) {
            return internal.get(index);
        }
    }

    @Override
    public E set(int index, E e) {
        Objects.requireNonNull(e);
        synchronized (internal) {
            return internal.set(index, e);
        }
    }

    @Override
    public void add(int index, E e) {
        Objects.requireNonNull(e);
        synchronized (internal) {
            internal.add(index, e);
        }
    }

    @Override
    public int size() {
        synchronized (internal) {
            return internal.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (internal) {
            return internal.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        Objects.requireNonNull(o);
        synchronized (internal) {
            return internal.contains(o);
        }
    }

    @Override
    public Iterator<E> iterator() {
        synchronized (internal) {
            return internal.iterator();
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (internal) {
            return internal.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Objects.requireNonNull(a);
        synchronized (internal) {
            return internal.toArray(a);
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        synchronized (internal) {
            internal.forEach(action);
        }
    }
}
