package fr.umlv.set;

import java.util.function.Consumer;

public final class DynamicHashSet<T> {

    @SuppressWarnings("unchecked")
    private final Entry<T>[] internal = (Entry<T>[]) new Entry[2];
    private int size = 0;

    private record Entry<E>(E value, Entry<E> next) {
    }

    public int size() {
        return size;
    }

    public void add(T value) {
        if (contains(value)) return;
        var id = hash(value);
        internal[id] = new Entry<>(value, internal[id]);
        size++;
    }

    public void forEach(Consumer<? super T> action) {
        for (var head : internal) {
            for (var entry = head; entry != null; entry = entry.next) {
                action.accept(entry.value);
            }
        }
    }

    public boolean contains(T value) {
        var id = hash(value);
        for (var entry = internal[id]; entry != null; entry = entry.next) {
            if (entry.value == value) return true;
        }
        return false;
    }

    private int hash(T value) {
        return value.hashCode() & (internal.length - 1);
    }
}
