package fr.umlv.set;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public final class DynamicHashSet<E> {

    @SuppressWarnings("unchecked")
    private Entry<E>[] internal = (Entry<E>[]) new Entry[2];
    private int size = 0;

    private record Entry<T>(T value, Entry<T> next) {
    }

    public int size() {
        return size;
    }

    public void add(E value) {
        if (contains(value)) return;
        if (size + 1 >= internal.length / 2) expand();
        var id = hash(value);
        internal[id] = new Entry<>(value, internal[id]);
        size++;
    }

    public void addAll(Collection<? extends E> c) {
        c.forEach(this::add);
    }

    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (var head : internal) {
            forEachEntry(head, e -> action.accept(e.value));
        }
    }

    public boolean contains(Object o) {
        var id = hash(o);
        for (var entry = internal[id]; entry != null; entry = entry.next) {
            if (entry.value.equals(o)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void expand() {
        var old = internal;
        internal = (Entry<E>[]) new Entry[internal.length * 2];
        for (var head : old) {
            forEachEntry(head, e -> {
                var id = hash(e.value);
                internal[id] = new Entry<>(e.value, internal[id]);
            });
        }
    }

    private void forEachEntry(Entry<E> head, Consumer<? super Entry<E>> action) {
        for (var entry = head; entry != null; entry = entry.next) {
            action.accept(entry);
        }
    }

    private int hash(Object o) {
        return o.hashCode() & (internal.length - 1);
    }
}
