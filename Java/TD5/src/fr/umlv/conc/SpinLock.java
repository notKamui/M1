package fr.umlv.conc;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class SpinLock {
    private volatile boolean token = false;
    private static final VarHandle HANDLE;

    static {
        try {
            HANDLE = MethodHandles.lookup().findVarHandle(SpinLock.class, "token", boolean.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var runnable = new Runnable() {
            private final SpinLock spinLock = new SpinLock();
            private int counter;

            @Override
            public void run() {
                for (int i = 0; i < 1_000_000; i++) {
                    spinLock.lock();
                    try {
                        counter++;
                    } finally {
                        spinLock.unlock();
                    }
                }
            }
        };
        var t1 = new Thread(runnable);
        var t2 = new Thread(runnable);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("counter " + runnable.counter);
    }

    public void lock() {
        while(!HANDLE.compareAndSet(this, false, true)) {
            Thread.onSpinWait();
        }
    }

    public void unlock() {
        token = false;
    }
}
