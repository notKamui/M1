# TD3 - Jimmy Teillard - Group 2

## Exercise 1

### Q1

`Runnable` is a functional interface that represents functions that take no arguments and return nothing.
Essentially, these functions only do side effects.
A `Thread` needs a `Runnable` to execute operations.

### Q3

The different threads do execute concurrently : the numbers are mixed up.
However, the scheduler is unpredictable so the order changes everytime ; this is *usual*.