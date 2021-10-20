package fr.umlv.conc;

public class Interruptus {
    private static int slow() throws InterruptedException {
        var result = 1;
        for (var i = 0; i < 1_000_000; i++) {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            result += (result * 7) % 513;
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        var thread = new Thread(() -> {
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
        thread.interrupt();
    }
}
