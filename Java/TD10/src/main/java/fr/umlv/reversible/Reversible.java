package fr.umlv.reversible;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static java.util.Objects.requireNonNull;

public interface Reversible<E> extends Iterable<E> {

    int size();

    E get(int index);

    Reversible<E> reversed();

    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), true);
    }

    static <E> Reversible<E> fromArray(E... elements) {
        requireNonNull(elements);
        for (var e : elements) {
            requireNonNull(e);
        }
        return instance(Arrays.asList(elements), elements.length, false, null);
    }

    static <E> Reversible<E> fromList(List<? extends E> list) {
        requireNonNull(list);
        for (var e : list) {
            requireNonNull(e);
        }
        return instance(list, list.size(), false, null);
    }

    private static <E> Reversible<E> instance(List<? extends E> list, int size, boolean isReversed, Reversible<E> reverse) {
        return new Reversible<>() {
            private Reversible<E> reverseSelf = reverse;

            @Override
            public Iterator<E> iterator() {
                return new Iterator<>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < size();
                    }

                    @Override
                    public E next() {
                        if (list.size() < size) throw new ConcurrentModificationException();
                        if (!hasNext()) throw new NoSuchElementException();
                        var e = get(index);
                        index++;
                        return e;
                    }
                };
            }

            private Spliterator<E> spliterator(int start, int end) {
                return new Spliterator<>() {
                    private int index = start;

                    @Override
                    public boolean tryAdvance(Consumer<? super E> action) {
                        requireNonNull(action);
                        if (index >= end) return false;
                        if (list.size() < size) throw new ConcurrentModificationException();
                        action.accept(get(index));
                        index++;
                        return true;
                    }

                    @Override
                    public Spliterator<E> trySplit() {
                        var left = index;
                        index = (index + end) >>> 1;
                        return left < index
                            ? spliterator(left, index)
                            : null;
                    }

                    @Override
                    public long estimateSize() {
                        return end - index;
                    }

                    @Override
                    public int characteristics() {
                        return SIZED | ORDERED | NONNULL;
                    }
                };
            }

            @Override
            public Spliterator<E> spliterator() {
                return spliterator(0, size);
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public E get(int index) {
                Objects.checkIndex(index, size);
                if (list.size() < size) throw new IllegalStateException();
                var e = list.get(isReversed ? size - index - 1 : index);
                requireNonNull(e);
                return e;
            }

            @Override
            public Reversible<E> reversed() {
                if (reverseSelf == null) {
                    reverseSelf = instance(list, size, !isReversed, this);
                }
                return reverseSelf;
            }
        };
    }
}