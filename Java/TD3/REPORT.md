# TD3 - Jimmy Teillard - Group 2

## Exercise 1

### Q1

`Runnable` is a functional interface that represents functions that take no arguments and return nothing.
Essentially, these functions only do side effects.
A `Thread` needs a `Runnable` to execute operations.

### Q3

The different threads do execute concurrently : the numbers are mixed up.
However, the scheduler is unpredictable so the order changes everytime ; this is *usual*.

## Exercise 3

### Q2

The size of the list is smaller than the amount of calls to `ArrayList::add`.
This happens because `ArrayList` is not made for asynchronous manipulation (cf. Javadoc), unlike `Vector`.

### Q3

That happens sometimes :
```
Exception in thread "Thread-0" java.lang.ArrayIndexOutOfBoundsException: Index 2793 out of bounds for length 1851
at java.base/java.util.ArrayList.add(ArrayList.java:455)
at java.base/java.util.ArrayList.add(ArrayList.java:467)
at fr.umlv.td3.HelloListBug.lambda$main$0(HelloListBug.java:13)
at java.base/java.lang.Thread.run(Thread.java:833)
```
I think this happens when we add an element to the list in a thread while it's being resized by another thread.

### Q6

A thread-safe class is a guaranty that, no matter what happens to the class, even if modified in multiple threads,
its integrity is kept. For example, for a list, that every element added really is in the list, and every removed
element really is removed.