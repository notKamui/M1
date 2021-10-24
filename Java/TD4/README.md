# TD4 - Java - Jimmy Teillard - Group 2

## Exercise 1

### Q1

A class that is called not *thread-safe* means that it doesn't guarantee that
the integrity of its content is kept over manipulation on multiple threads.

### Q2

`HonorBoard` is not *thread-safe* because there is a possibility that
two threads try to set the `firstname` and `lastname` at the same time.
Hence, sometimes, the board may hold the values "John Odd" even though
that's not the name of either of the two "hackers".

Note that there is also the fact that the `toString` method may
try to access both members of the class asynchronously.

### Q4

We absolutely **CANNOT** substitute the `toString` call with two getter calls because
there would be no way to make sure they are both accessed at the same time from
within the class (There would be the need to use `synchronized` in the main function, which is bad)

## Exercise 2

### Q1

A re-entrant lock means that it's a lock that holds an internal counter.
It gets incremented everytime we lock it, and decremented everytime we unlock it.

## Exercise 3

### Q1

A thread has to be interrupted in a cooperative manner. Indeed, sometimes the thread will be doing operations
that need to be *done* before accepting to be interrupted (e.g. IO operations ; you can't stop that).

### Q2

A blocking operation is an operation that literally stops the current thread until done.
For example, `sleep` will pause the thread for a given amount of time.
`join` will pause the thread until the given thread is effectively terminated, etc.

When a thread is interrupted, it will throw an `InterruptedException` from within the thread,
if busy doing a blocking operation, which is catchable. It's like a signal.

### Q3

```
new Thread(() -> {
    var forNothing = 0;
    while (true) {
        forNothing += slow();
        if (Thread.currentThread().isInterrupted()) {
            System.out.println(forNothing);
            return;
        }
    }
});
```

When we call `interrupt` on the thread, a flag is set so that, within the thread,
we check its status. This is perfect for threads that hold no blocking operations.

### Q4

```
private static int slow() {
    var result = 1;
    for (var i = 0; i < 1_000_000; i++) {
        if (Thread.currentThread().isInterrupted()) return result;
        result += (result * 7) % 513;
    }
    return result;
}

...

new Thread(() -> {
    var forNothing = 0;
    while (true) {
        forNothing += slow();
        if (Thread.currentThread().isInterrupted()) {
            System.out.println(forNothing);
            return;
        }
    }
});
```

We can stop the `slow` function when the thread is interrupted.

### Q5

```
private static int slow() {
    var result = 1;
    for (var i = 0; i < 1_000_000; i++) {
        if (Thread.interrupted()) return result;
        result += (result * 7) % 513;
    }
    return result;
}

...

new Thread(() -> {
    var forNothing = 0;
    while(true) {
        forNothing += slow();
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            System.out.println(forNothing);
            return;
        }
        forNothing += slow();
        if (Thread.currentThread().isInterrupted()) {
            System.out.println(forNothing);
            return;
        }
    }
});
```

The function `Thread.interruped()` resets the interrupt flag ; that way,
we can still catch blocking operations.

### Q6

```
private static int slow() throws InterruptedException {
    var result = 1;
    for (var i = 0; i < 1_000_000; i++) {
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
        result += (result * 7) % 513;
    }
    return result;
}

...

new Thread(() -> {
    var forNothing = 0;
    while(true) {
        try {
            forNothing += slow();
            Thread.sleep(1_000);
            forNothing += slow();
        } catch (InterruptedException e) {
            System.out.println(forNothing);
            return;
        }
    }
});
```

If we just propagate an `InterruptedException` as if `slow` is like
a blocking operation, then we can just catch that same exception.

### Q7

The difference between `Thread.interruped()` and `thread.isInterrupted()`
is that the first one resets the interrupt flag back to 0, while
the other does not.
`Thread.interruped()` is poorly named because it doesn't state explicitly
that it has side effects in its name.

### Q8

```
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
```

### Q9

We just need to add this at the very end of the program.

```
System.out.println("Forced stop");
for (var thread : threads) thread.interrupt();
```

If the loop receives anything other than an integer (e.g. null if we CTRL-D),
it will break out of it and close the scanner. Then we just need to make sure
we interrupt every running thread.