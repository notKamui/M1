package fr.umlv.td3;

import java.util.ArrayList;

public final class HelloListSafe {

    public static void main(String[] args) throws InterruptedException{
        final var threads = new ArrayList<Thread>();
        final var nums = new ThreadSafeList<Integer>();
        final var removed = new ThreadSafeList<Integer>();
        final Runnable runner = () -> {
            for (int i = 0; i < 5000; i++) {
                nums.add(i);
                if (i % 2 == 0) {
                    removed.add(nums.remove(0));
                }
            }
        };
        for (int t = 0; t < 4; t++) {
            var thread = new Thread(runner);
            thread.start();
            threads.add(thread);
        }
        for (var thread : threads) {
            thread.join();
        }
        removed.forEach(System.out::println);
        System.out.println("Le programme est fini\nTaille de la list : " + nums.size());
    }
}
