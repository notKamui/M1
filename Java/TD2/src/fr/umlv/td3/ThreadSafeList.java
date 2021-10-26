package fr.umlv.td3;

import java.util.*;
import java.util.function.Consumer;

public final class ThreadSafeList<E> implements List<E> {

    private final ArrayList<E> internal = new ArrayList<>();
    private final Object lock = new Object();

    @Override
    public boolean add(E e) {
        Objects.requireNonNull(e);
        synchronized (lock) {
            return internal.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        Objects.requireNonNull(o);
        synchronized (lock) {
            return internal.remove(o);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c);
        synchronized (lock) {
            return internal.containsAll(c);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        synchronized (lock) {
            return internal.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        synchronized (lock) {
            return internal.addAll(index, c);
        }
    }

    @Override
    public E remove(int index) throws IndexOutOfBoundsException, UnsupportedOperationException {
        synchronized (lock) {
            return internal.remove(index);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        synchronized (lock) {
            return internal.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        synchronized (lock) {
            return internal.retainAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            internal.clear();
        }
    }

    @Override
    public int indexOf(Object o) {
        Objects.requireNonNull(o);
        synchronized (lock) {
            return internal.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        Objects.requireNonNull(o);
        synchronized (lock) {
            return internal.lastIndexOf(o);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        synchronized (lock) {
            return internal.listIterator();
        }
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        synchronized (lock) {
            return internal.listIterator(index);
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        synchronized (lock) {
            return internal.subList(fromIndex, toIndex);
        }
    }

    @Override
    public E get(int index) {
        synchronized (lock) {
            return internal.get(index);
        }
    }

    @Override
    public E set(int index, E e) {
        Objects.requireNonNull(e);
        synchronized (lock) {
            return internal.set(index, e);
        }
    }

    @Override
    public void add(int index, E e) {
        Objects.requireNonNull(e);
        synchronized (lock) {
            internal.add(index, e);
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            return internal.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return internal.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        Objects.requireNonNull(o);
        synchronized (lock) {
            return internal.contains(o);
        }
    }

    @Override
    public Iterator<E> iterator() {
        synchronized (lock) {
            return internal.iterator();
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (lock) {
            return internal.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Objects.requireNonNull(a);
        synchronized (lock) {
            return internal.toArray(a);
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        synchronized (lock) {
            internal.forEach(action);
        }
    }
}
