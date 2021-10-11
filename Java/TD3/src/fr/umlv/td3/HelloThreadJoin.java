package fr.umlv.td3;

import java.util.ArrayList;

public class HelloThreadJoin {

    public static void main(String[] args) throws InterruptedException {
        var threads = new ArrayList<Thread>();
        for (int t = 0; t < 4; t++) {
            final var threadN = t;
            var thread = new Thread(() -> {
                for (int i = 0; i <= 5000; i++) {
                    System.out.println("thread " + threadN + " " + i);
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (var thread : threads) {
            thread.join();
        }
        System.out.println("Le programme est fini");
    }
}
