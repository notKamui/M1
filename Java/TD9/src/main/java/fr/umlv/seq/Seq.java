package fr.umlv.seq;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static java.util.Objects.requireNonNull;

public class Seq<E> implements Iterable<E> {
    private final List<?> internal;
    private final Function<Object, E> mapper;

    private Seq(List<?> internal, Function<Object, E> mapper) {
        this.internal = List.copyOf(requireNonNull(internal));
        this.mapper = requireNonNull(mapper);
    }

    @SuppressWarnings("unchecked")
    public static <T> Seq<T> from(List<? extends T> list) {
        return new Seq<>(list, it -> (T) it);
    }

    @SafeVarargs
    public static <T> Seq<T> of(T... elements) {
        return from(List.of(elements));
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
        return new Seq<>(internal, this.mapper.andThen(mapper));
    }

    public Optional<E> findFirst() {
        if (size() <= 0) return Optional.empty();
        else return Optional.of(get(0));
    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Iterator<E> iterator() {
        var size = size();
        return new Iterator<>() {
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return counter < size;
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

    private Spliterator<E> splitMapper(Spliterator<?> self) {
        if (self == null) return null;
        return new Spliterator<>() {

            @Override
            public boolean tryAdvance(Consumer<? super E> action) {
                requireNonNull(action);
                return self.tryAdvance(e -> action.accept(mapper.apply(e)));
            }

            @Override
            public Spliterator<E> trySplit() {
                return splitMapper(self.trySplit());
            }

            @Override
            public long estimateSize() {
                return self.estimateSize();
            }

            @Override
            public int characteristics() {
                return self.characteristics() | IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
            }
        };
    }

    @Override
    public Spliterator<E> spliterator() {
        return splitMapper(internal.spliterator());
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        requireNonNull(action);
        internal.forEach(e -> action.accept(mapper.apply(e)));
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "<", ">");
        forEach(e -> sj.add(e.toString()));
        return sj.toString();
    }
}
