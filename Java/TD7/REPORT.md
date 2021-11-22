# TD7

***Important***

To launch the test suite, run `./gradlew test` at the root of the project.

## Exercise 1

### Q1

One way to keep track of the fullness state of the FIFO is to have an internal
`size` field. If the head index is equal to the tail index : 
- if `size == 0`, then the FIFO is empty
- if `size == internal.length`, then the FIFO is full

### Q3

To check if the FIFO is full, I check that `internal.length == size`.
If that's the case, I throw an `IllegalStateException`

### Q4

We should throw an `IllegalStateException` if the FIFO is empty.

### Q6

Since, on the JVM, the garbage collector cannot free the memory allocated to
some objects which we still have the reference somewhere, a memory leak
can happen ; that means that to many objects are allocated memory and are
never freed.

### Q8

An iterator is an object that can be iterated over with two methods `hasNext`
and `next`. Our `iterator` should return an `Iterator<E>`.

### Q10

An `Iterable` is an object that has a method `iterator` and can be
iterated over (with a foreach for example)