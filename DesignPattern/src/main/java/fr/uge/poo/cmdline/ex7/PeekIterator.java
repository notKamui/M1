package fr.uge.poo.cmdline.ex7;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * An iterator that can peek at the next element without advancing the iterator.
 *
 * @param <T> the underlying type of the contents of the iterator.
 */
public class PeekIterator<T> implements Iterator<T> {

    private final Iterator<? extends T> iterator;
    private T peeked = null;

    private PeekIterator(Iterator<? extends T> iterator) {
        this.iterator = requireNonNull(iterator);
    }

    /**
     * Creates a new peek-able iterator.
     *
     * @param iterable the iterable to wrap.
     * @return a new peek-able iterator.
     */
    public static <T> PeekIterator<T> of(Iterable<? extends T> iterable) {
        return new PeekIterator<>(iterable.iterator());
    }

    /**
     * Checks if there is a next element.
     *
     * @return true if there is a next element, false otherwise.
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Gets the next element.
     *
     * @return the next element.
     * @throws NoSuchElementException if there is no next element.
     */
    public T next() {
        if (!hasNext()) throw new NoSuchElementException("No more elements");
        T value;
        if (peeked != null) {
            value = peeked;
            peeked = null;
        } else {
            value = iterator.next();
        }
        return value;
    }

    /**
     * Peeks at the next element without advancing the iterator.
     *
     * @return the next element.
     * @throws NoSuchElementException if there is no next element.
     */
    public T peek() {
        if (!hasNext()) throw new NoSuchElementException("No more elements");
        T value;
        if (peeked == null) {
            peeked = iterator.next();
        }
        value = peeked;
        return value;
    }
}
