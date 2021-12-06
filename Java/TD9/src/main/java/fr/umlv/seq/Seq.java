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

//    private Spliterator<E> spliterator(int start, int end) {
//        return new Spliterator<>() {
//            private int counter = start;
//
//            @Override
//            public boolean tryAdvance(Consumer<? super E> action) {
//                if (counter >= end) return false;
//                action.accept(get(counter));
//                counter++;
//                return true;
//            }
//
//            @Override
//            public Spliterator<E> trySplit() {
//                var left = counter;
//                counter = (counter + end) >>> 1;
//                return left < counter
//                    ? spliterator(left, counter)
//                    : null;
//            }
//
//            @Override
//            public long estimateSize() {
//                return end - counter;
//            }
//
//            @Override
//            public int characteristics() {
//                return SIZED | NONNULL | ORDERED | IMMUTABLE;
//            }
//        };
//    }
//
//    @Override
//    public Spliterator<E> spliterator() {
//        return spliterator(0, internal.size());
//    }


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
                return self.characteristics() | IMMUTABLE | NONNULL | ORDERED;
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
        internal.forEach(e -> sj.add(mapper.apply(e).toString()));
        return sj.toString();
    }
}
