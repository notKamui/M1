# TD1 Jimmy Teillard

## Q5

We can get a `Stream` from a `Collection`
thanks to the method `Collection.stream`.

With a stream, we can then use higher order functions 
(which expect lambdas/anonymous functions) to apply them to the elements
of the collection.

- `filter` expects a predicate to filter in the elements that abide to it.
- `map` expects a function, that receives a T (from the collection)
  and returns an R, and applies a transformation.
- `collect` generally aggregates the elements of the stream using a
  collector. `Collectors.joining` will allow us to get the `String`
  from the concatenation of the elements of the stream, separated
  by a given delimiter.

## Q6

The method which allows us to remove the correct object from a list given
another object is `Object.equals` (along with `Object.hashCode`).
The advantage of records (like `Car`) is that those methods are
automatically implemented.

```java
public record Car(String model, int year) {
    /* ... */
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return year == car.year && model.equals(car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, year);
    }
}
```

## Q7

In the case where there's no car from the requested year,
returning an empty list is acceptable.

## Q10

If `Car` and `Camel` were classes instead of records, 
we **could** have made `Vehicle` an abstract class, where `year` is
a field (but I find that kinda yuck).