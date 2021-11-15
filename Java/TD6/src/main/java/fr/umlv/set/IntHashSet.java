package fr.umlv.set;

import java.util.function.Consumer;

public final class IntHashSet {

    private final Entry[] internal = new Entry[8];
    private int size = 0;

    private record Entry(int value, Entry next) {
    }

    public int size() {
        return size;
    }

    public void add(int value) {
        if (contains(value)) return;
        var id = hash(value);
        internal[id] = new Entry(value, internal[id]);
        size++;
    }

    public void forEach(Consumer<Integer> action) {
        for (var head : internal) {
            for (var entry = head; entry != null; entry = entry.next) {
                action.accept(entry.value);
            }
        }
    }

    public boolean contains(int value) {
        var id = hash(value);
        for (var entry = internal[id]; entry != null; entry = entry.next) {
            if (entry.value == value) return true;
        }
        return false;
    }

    private int hash(int value) {
        return value & (internal.length - 1);
    }
}
