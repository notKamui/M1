package fr.umlv.produceconsume;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockedBlockingBuffer<E> {
    private final ArrayDeque<E> queue;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition in = lock.newCondition();
    private final Condition out = lock.newCondition();
    private final int capacity;

    public LockedBlockingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("size must be strictly positive");
        this.capacity = capacity;
        queue = new ArrayDeque<>(capacity);
    }

    public void put(E e) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() >= capacity)
                in.await();
            queue.add(e);
            out.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty())
                out.await();
            in.signalAll();
            return queue.pop();
        } finally {
            lock.unlock();
        }
    }
}
