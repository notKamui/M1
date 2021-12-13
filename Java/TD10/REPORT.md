# TD 10 - Java - Report - Jimmy Teillard

**IMPORTANT**

To launch the test suite, run the following command: `./gradlew test`

## Exercice 1

### Q2

I can write the iterator in a single place in `Reversible`
since no matter the implementation, the iterator will always be the same.
(since it depends on `Reversible.get` and `Reversible.size`). To be precise,
it's going to be in the "list" implementation, since the "array" is actually
a subset.