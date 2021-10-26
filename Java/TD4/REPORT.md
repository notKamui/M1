# Java - TD4 - Jimmy Teillard - Group 2

## Exercise 1

### Q1

The Producer/Consumer pattern allows us to manipulate data
in asynchronous and continuous flows (this is analogous to Reactive Programming).
We can periodically produce data (let's say we fetch from an API), and
we can consume them in a queue later.
This clears problems that can occur when we have less consumers than producers.

### Q2

We use the method `put` to insert a value inside a `BlockingQueue`, and `take`
to retrieve one. There are other options, but they are non-blocking, which is not
what we want.

### Q3

```java
import java.util.function.Consumer;

public class Main {

    private static Thread repeat(int id, Consumer<Integer> action, long wait) {
        return new Thread(() -> {
            while (true) {
                action.accept(id);
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
    }

    public static void main(String[] args) {
        Consumer<Integer> action = (id) -> System.out.println("hello " + id);
        repeat(0, action, 1).start();
        repeat(1, action, 4).start();
    }
}
```

### Q4

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class Main {

    private static Thread repeat(int id, Function<Integer, Boolean> action, long wait) {
        return new Thread(() -> {
            while (true) {
                if (!action.apply(id)) return;
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                   return;
                }
            }
        });
    }

    public static void main(String[] args) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        // BlockingQueue<String> queue = new ArrayBlockingQueue<>(60);
        Function<Integer, Boolean> action = (id) -> {
            var msg = "hello " + id;
            System.out.println(msg);
            try {
                queue.put(msg);
            } catch (InterruptedException ignored) {
                return false;
            }
            return true;
        };
        repeat(0, action, 1).start();
        repeat(1, action, 4).start();
    }
}
```

In the case of the `ArrayBlockingQueue` implementation,
like the name suggests, when we try to add another value even though
the queue is already full, it blocks the thread until it can
receive said value.

### Q5

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    @FunctionalInterface
    private interface BlockingConsumer<T> {
        void accept(T t) throws InterruptedException;
    }

    private static Thread repeat(int id, BlockingConsumer<Integer> action, long wait) {
        return new Thread(() -> {
            while (true) {
                try {
                    action.accept(id);
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                   return;
                }
            }
        });
    }

    public static void main(String[] args) {
        // BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1); // it works !
        BlockingConsumer<Integer> in = id -> queue.put("hello " + id);
        BlockingConsumer<Integer> out = ignored -> System.out.println(queue.take());

        repeat(0, in, 1).start();
        repeat(1, in, 4).start();

        repeat(2, out, 2).start();
        repeat(3, out, 3).start();
        repeat(4, out, 5).start();
    }
}
```

## Exercise 2

### Q1

```java
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SynchronizedBlockingBuffer<E> {
    private final ArrayDeque<E> queue;
    private final int capacity;

    public SynchronizedBlockingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("size must be strictly positive");
        this.capacity = capacity;
        queue = new ArrayDeque<>(capacity);
    }

    public void put(E e) throws IllegalStateException {
        synchronized (queue) {
            if (queue.size() >= capacity) throw new IllegalStateException("the buffer is full");
            queue.add(e);
        }
    }

    public E take() throws NoSuchElementException {
        synchronized (queue) {
            if (queue.isEmpty()) throw new NoSuchElementException("buffer is empty");
            return queue.pop();
        }
    }
}
```

### Q2 + 3

```java
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
```

### Q4

```java
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
```
