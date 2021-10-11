package fr.umlv.td3;

public final class HelloThread {

    public static void main(String[] args) {
        for (int t = 0; t < 4; t++) {
            final var threadN = t;
            new Thread(() -> {
                for (int i = 0; i <= 5000; i++) {
                    System.out.println("thread " + threadN + " " + i);
                }
            }).start();
        }
    }
}
