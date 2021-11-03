package fr.umlv.conc;

public class BogusVolatile {
    private volatile boolean stop;

    public static void main(String[] args) throws InterruptedException {
        var bogus = new Bogus();
        var thread = new Thread(bogus::runCounter);
        thread.start();
        Thread.sleep(100);
        bogus.stop();
        thread.join();
    }

    public void runCounter() {
        var localCounter = 0;
        while (!stop) {
            localCounter++;
        }
        System.out.println(localCounter);
    }

    public void stop() {
        stop = true;
    }
}
