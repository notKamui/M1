package fr.umlv.seq;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.Objects.requireNonNull;

public class Seq<E> {
    private final List<?> internal;
    private final Function<?, ? extends E> mapper;

    private Seq(List<?> internal, Function<?, ? extends E> mapper) {
        this.internal = requireNonNull(List.copyOf(internal));
        this.mapper = requireNonNull(mapper);
    }

    public static <T> Seq<T> from(List<? extends T> list) {
        return new Seq<>(requireNonNull(list), Function.identity());
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... elements) {
        return new Seq<>(requireNonNull(Arrays.stream(elements).toList()), Function.identity());
    }

    public E get(int n) {
        if (n < 0 || n >= size())
            throw new IndexOutOfBoundsException("Index out of bounds 0 until" + size() + " for " + n);
        return mapper.apply(internal.get(n));
    }

    public int size() {
        return internal.size();
    }

    public void forEach(Consumer<? super E> action) {
        internal.forEach(e -> action.accept(mapper.apply(e)));
    }

    public <R> Seq<R> map(Function<? super E, ? extends R> mapper) {
        return new Seq<>(internal, mapper);
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "<", ">");
        internal.forEach(e -> sj.add(e.toString()));
        return sj.toString();
    }
}
