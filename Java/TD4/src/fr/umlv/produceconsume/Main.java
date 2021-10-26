package fr.umlv.produceconsume;

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
        // var queue = new LinkedBlockingQueue<String>();
        // var queue = new ArrayBlockingQueue<String>(1);
        // var queue = new SynchronizedBlockingBuffer<String>(1);
        var queue = new LockedBlockingBuffer<String>(1);
        BlockingConsumer<Integer> in = id -> queue.put("hello " + id);
        BlockingConsumer<Integer> out = ignored -> System.out.println(queue.take());

        repeat(0, in, 1).start();
        repeat(1, in, 4).start();

        repeat(2, out, 2).start();
        repeat(3, out, 3).start();
        repeat(4, out, 5).start();
    }
}
