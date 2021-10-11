package fr.umlv.td3;

import java.util.ArrayList;

public final class HelloListBug {

    public static void main(String[] args) throws InterruptedException {
        final var threads = new ArrayList<Thread>();
        final var nums = new ArrayList<Integer>();
        for (int t = 0; t < 4; t++) {
            var thread = new Thread(() -> {
                for (int i = 0; i < 5000; i++) {
                    synchronized (nums) {
                        nums.add(i);
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (var thread : threads) {
            thread.join();
        }
        System.out.println("Le programme est fini\nTaille de la list : " + nums.size());
    }
}
