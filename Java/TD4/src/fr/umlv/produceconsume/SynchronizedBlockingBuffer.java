package fr.umlv.produceconsume;

import java.util.ArrayDeque;
import java.util.Objects;

public class SynchronizedBlockingBuffer<E> {
    private final ArrayDeque<E> queue;
    private final int capacity;

    public SynchronizedBlockingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("size must be strictly positive");
        this.capacity = capacity;
        queue = new ArrayDeque<>(capacity);
    }

    public void put(E e) throws InterruptedException {
        synchronized (queue) {
            while (queue.size() >= capacity)
                queue.wait();
            queue.add(e);
            queue.notifyAll();
        }
    }

    public E take() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty())
                queue.wait();
            queue.notifyAll();
            return queue.pop();
        }
    }
}
