package fr.umlv.seq;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import static java.util.Objects.requireNonNull;

public class Seq<E> {
    private final List<? extends E> internal;

    private Seq(List<? extends E> internal) {
        this.internal = requireNonNull(List.copyOf(internal));
    }

    public static <T> Seq<T> from(List<? extends T> list) {
        return new Seq<>(requireNonNull(list));
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... elements) {
        return new Seq<>(requireNonNull(Arrays.stream(elements).toList()));
    }

    public E get(int n) {
        if (n < 0 || n >= size())
            throw new IndexOutOfBoundsException("Index out of bounds 0 until" + size() + " for " + n);
        return internal.get(n);
    }

    public int size() {
        return internal.size();
    }

    public void forEach(Consumer<? super E> action) {
        internal.forEach(action);
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "<", ">");
        internal.forEach(e -> sj.add(e.toString()));
        return sj.toString();
    }
}
