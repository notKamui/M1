# TD 10 - Java - Report - Jimmy Teillard

**IMPORTANT**

To launch the test suite, run the following command: `./gradlew test`

## Exercise 1

### Q2

I can write the iterator in a single place in `Reversible`
since no matter the implementation, the iterator will always be the same.
(since it depends on `Reversible.get` and `Reversible.size`). To be precise, it's going to be in the "list"
implementation, since the "array" is actually a subset.

## Exercise 2

### Q8

There are some cases where the implementation of `List` that we use has a `get` in O(n) time. This is the case
for `LinkedList`, for example. In which case, we should throw an `IllegalArgumentException` if we try to create
a `Reversible` with it. To check that the implementation of get is in constant time, we can check that it
implements `RandomAccess`.