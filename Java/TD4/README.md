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

### Q5

A re-entrant lock means that it's a lock that holds an internal counter.
It gets incremented everytime we lock it, and decremented everytime we unlock it.

## Exercise 3

### Q7

A thread has to be interrupted in a cooperative manner. Indeed, sometimes the thread will be doing operations
that need to be *done* before accepting to be interrupted (e.g. IO operations ; you can't stop that).

