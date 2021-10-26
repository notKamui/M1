package fr.umlv.conc;

import java.util.ArrayList;
import java.util.Scanner;

public class Interruptus {
    private static int slow() throws InterruptedException {
        var result = 1;
        for (var i = 0; i < 1_000_000; i++) {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            result += (result * 7) % 513;
        }
        return result;
    }

    public static void main(String[] args) {
        /*var thread = new Thread(() -> {
            var forNothing = 0;
            while(true) {
                try {
                    forNothing += slow();
                    Thread.sleep(1_000);
                    forNothing += slow();
                } catch (InterruptedException e) {
                    System.out.println(forNothing);
                    break;
                }
            }
        });
        thread.start();
        Thread.sleep(1_000);
        thread.interrupt();*/

        var threads = new ArrayList<Thread>();
        for (int i = 0; i < 4; i++) {
            var tID = i;
            var thread = new Thread(() -> {
                var count = 0;
                while (true) {
                    System.out.println("Thread " + tID + " : " + (count++));
                    try {
                        Thread.sleep(1_000);
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + tID + " has been stopped");
                        return;
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        System.out.println("Enter a thread id:");
        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNextInt()) {
                var tID = scanner.nextInt();
                threads.get(tID).interrupt();
            }
        }
        System.out.println("Forced stop");
        for (var thread : threads) thread.interrupt();
    }
}
