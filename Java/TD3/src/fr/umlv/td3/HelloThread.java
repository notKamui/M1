package fr.umlv.td3;

public final class HelloThread {

    public static void main(String[] args) {
        Runnable runner = () -> {
            for (int i = 0; i <= 5000; i++) {
                System.out.println(i);
            }
        };
        for (int i = 0; i < 4; i++) {
            new Thread(runner).start();
        }
    }
}
