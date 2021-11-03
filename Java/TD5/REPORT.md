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