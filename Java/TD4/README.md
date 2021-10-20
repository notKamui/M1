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