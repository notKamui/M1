package fr.umlv.seq;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.Objects.requireNonNull;

public class Seq<E> implements Iterable<E> {
    private final List<?> internal;
    private final Function<? super Object, E> mapper;

    private Seq(List<?> internal, Function<? super Object, E> mapper) {
        this.internal = requireNonNull(List.copyOf(internal));
        this.mapper = requireNonNull(mapper);
    }

    @SuppressWarnings("unchecked")
    public static <T> Seq<T> from(List<? extends T> list) {
        return new Seq<>(requireNonNull(list), it -> (T) it);
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... elements) {
        return from(List.of(requireNonNull(elements)));
    }

    public E get(int n) {
        if (n < 0 || n >= size())
            throw new IndexOutOfBoundsException("Index out of bounds 0 until" + size() + " for " + n);
        return mapper.apply(internal.get(n));
    }

    public int size() {
        return internal.size();
    }

    public <R> Seq<R> map(Function<? super E, ? extends R> mapper) {
        requireNonNull(mapper);
        return new Seq<>(internal, mapper.compose(this.mapper));
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return counter < size();
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException("No next value");
                var ret = get(counter);
                counter++;
                return ret;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        requireNonNull(action);
        internal.forEach(e -> action.accept(mapper.apply(e)));
    }

    public Optional<E> findFirst() {
        if (size() <= 0) return Optional.empty();
        else return Optional.of(get(0));
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "<", ">");
        internal.forEach(e -> sj.add(mapper.apply(e).toString()));
        return sj.toString();
    }
}
