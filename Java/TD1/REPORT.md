# TD2 - Java - Jimmy Teillard - Group 2

***To run the main function/class, run `sh gradlew run`
(or `gradle run` if Gradle is installed)***

***To run the set of tests, run `sh gradlew clean test`
(or `gradle clean test` if Gradle is installed)***

***If "BUILD SUCCESSFUL" shows up, that means they all passed.\
If "BUILD FAILED" shows up, that means that one of them didn't pass
(or that there's a problem with the JVM)***


## Exercise 1

### Q1

The `Path` API is part of the more robust and modern `java.nio` packages
(as opposed to `java.io.File`). It allows for better control over paths
and clearly separates the declaration of a file path and the actual opening
of the file.

### Q7

`try/catch`, like the name suggests, catches an exception immediately and stops its propagation.
On the contrary, `throws` sends the exception up in the function call hierarchy, 
to let a more appropriate function handle it.
In this case, throwing is adapted to the situation because we can't do anything meaningful when catching the `IOException`.

### Q9

The "try with resources" pattern is always better than the "simple try/catch" pattern in the case of handling resources
that need to be closed when we're done with it. Not only they're way simpler to read, they also manage closing
said resources no matter what happens in terms of exceptions and control flow.

## Exercise 2

## Q3

`Collectors.toUnmodifiableMap` associates a given key to a value from the reduction of the given stream.
(resulting in an immutable map)

## Q4 + 6

`Stream.flatMap` flattens a stream of streams into a single stream by removing one dimension
(in other words, it "concatenates" the streams into one).
We can flatten the list of actors into one single big stream that we convert to a Set to remove all duplicates.
Then we just retrieve the size of said set.

## Q7

`Stream.distinct` removes all duplicates in a stream. This replaces the step where we converted the stream to a set.

## Q8 + 9

The method should return a map associating each actor to his number of occurrences (`Map<String, Long>`).
`Stream.collect`'s purpose is to aggregate the elements of a stream following rules given by its parameters,
which are `Collectors`. Here, when we have our stream of all actors (with duplicates), we want to associate
each actor (`actor -> actor` eq. `Function.identity()`) to the number of occurrences (`Collectors.counting()`).
`collect` itself will return the wanted map.

### Q10

The method should return an `Optional<ActorMovieCount>`. Indeed, if the given map is empty, then the returned
value should be an empty optional. I replaced the `collect(maxBy...` by `max(Collector)` which accomplishes 
the exact same goal by using collectors too.