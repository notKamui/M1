# TD5 - Java - Jimmy Teillard - Group 2

## Exercise 1

### Q1

This program will be stuck in an infinite loop and print nothing.

### Q2

I was right. The variable `stop` is read from the cache, so modifications from
another thread are not visible.

### Q3

```
public void runCounter() {
    var localCounter = 0;
        for(;;) {
            synchronized(lock) {
                if (stop) {
                    break;
                }
            }
            localCounter++;
        }
    System.out.println(localCounter);
}
```

We have to synchronize the read access of the variable `stop` to force
it to be read from the RAM instead of the cache.

### Q4

To force the variable `stop` to be read from the RAM in every situation, we
can declare it as volatile : `private volatile boolean stop`.

We call this kind of implementation "lock free".

## Exercise 2

### Q1

"Reentrant" means that a thread can claim a lock several times.

### Q2

We expect the program to output 2,000,000 ; although, it is not thread safe
because several read/write access can occur at the same time on the counter.
`volatile` only guarantees how the memory is accessed, not the fact that 
several operations are synchronized atomically.

### Q3

If a thread cannot acquire a lock, it just has to wait and retry later. However,
waiting with `wait` will block the allocated CPU, while `onSpinWait` doesn't :
it will allow us to wait for the lock to become available while doing other
things in the background.

## Exercise 3

### Q1

A pseudo random generator contains a seed on which
every obtention of a number depends on
(generally a complex mathematical function)
that is updated everytime we get a new value.

However in this case, our `next` does several
complex operations (they are not atomic),
so that can lead to a lot of inconsistencies
in what's to be expected.

### Q2

```
public long next() {  // Marsaglia's XorShift
    while (true) {
        var now = x.get();
        var next = now;
        next ^= next >>> 12;
        next ^= next << 25;
        next ^= next >>> 27;
        if (x.compareAndSet(now, next)) {
            return next * 2685821657736338717L;
        }
    }
}
```

### Q3

```
public long next() {  // Marsaglia's XorShift
    return x.updateAndGet(n -> {
        n ^= n >>> 12;
        n ^= n << 25;
        n ^= n >>> 27;
        return n;
    }) * 2685821657736338717L;
}
```