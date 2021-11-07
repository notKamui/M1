package fr.umlv.conc;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class RNGVolatile {
    private final static VarHandle HANDLE;
    private final long x;

    static {
        try {
            HANDLE = MethodHandles
                .lookup()
                .findVarHandle(RNGVolatile.class, "x", long.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public RNGVolatile(long seed) {
        if (seed == 0) {
            throw new IllegalArgumentException("seed == 0");
        }
        x = seed;
    }

    public static long xorShift(long x) {
        x ^= x >>> 12;
        x ^= x << 25;
        x ^= x >>> 27;
        return x;
    }

    public long next() {
        while (true) {
            var now = x;
            var next = xorShift(now);
            if (HANDLE.compareAndSet(this, now, next)) {
                return next * 2685821657736338717L;
            }
        }
    }
}
