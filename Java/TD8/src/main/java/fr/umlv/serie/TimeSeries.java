package fr.umlv.serie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public final class TimeSeries<E> {

    private final ArrayList<Data<E>> series = new ArrayList<>();

    public record Data<T>(long timestamp, T element) {
        public Data {
            requireNonNull(element);
        }

        @Override
        public String toString() {
            return "%d | %s".formatted(timestamp, element);
        }
    }

    public final class Index implements Iterable<Data<E>> {
        private final TimeSeries<E> self;
        private final int[] indices;

        private Index(TimeSeries<E> self, Predicate<? super E> filter) {
            this.self = requireNonNull(self);
            requireNonNull(filter);
            indices = IntStream.range(0, series.size())
                .filter(i -> filter.test(get(i).element))
                .toArray();
        }

        private Index(TimeSeries<E> self, int[] indices) {
            this.self = requireNonNull(self);
            this.indices = requireNonNull(indices);
        }

        public int size() {
            return indices.length;
        }

        public Index or(TimeSeries<? extends E>.Index other) {
            checkInstance(requireNonNull(other));
            return new Index(self, IntStream
                .concat(Arrays.stream(this.indices), Arrays.stream(other.indices))
                .distinct()
                .sorted()
                .toArray());
        }

        public Index and(TimeSeries<? extends E>.Index other) {
            checkInstance(requireNonNull(other));
            var cross = Arrays.stream(other.indices)
                .boxed()
                .collect(Collectors.toSet());
            return new Index(self, Arrays.stream(this.indices)
                .filter(cross::contains)
                .toArray());
        }

        @Override
        public void forEach(Consumer<? super Data<E>> action) {
            Arrays.stream(indices).forEach(i -> action.accept(get(i)));
        }

        @Override
        public Iterator<Data<E>> iterator() {
            return new Iterator<>() {
                private int counter = 0;

                @Override
                public boolean hasNext() {
                    return counter < size();
                }

                @Override
                public Data<E> next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    var data = get(indices[counter]);
                    counter++;
                    return data;
                }
            };
        }

        @Override
        public String toString() {
            return Arrays.stream(indices)
                .mapToObj(i -> get(i).toString())
                .collect(Collectors.joining("\n"));
        }

        private void checkInstance(TimeSeries<? extends E>.Index other) {
            if (this.self != other.self) {
                throw new IllegalArgumentException("Indices are not from the same instance");
            }
        }
    }

    public int size() {
        return series.size();
    }

    public void add(long timestamp, E element) {
        requireNonNull(element);
        if (size() > 0) {
            var last = series.get(series.size()-1).timestamp;
            if (timestamp < last) {
                throw new IllegalStateException("The timestamp (" + timestamp + ") must be greater or equal to the last one (" + last + ")");
            }
        }

        series.add(new Data<>(timestamp, element));
    }

    public Data<E> get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("index should be positive and smaller than " + size() + " but was " + index);
        }

        return series.get(index);
    }

    public Index index() {
        return new Index(this, __ -> true);
    }

    public Index index(Predicate<? super E> filter) {
        return new Index(this, filter);
    }
}
