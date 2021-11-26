# Java Report - TD 8 - Jimmy Teillard

## Q5

`index(filter)` should receive a `Predicate<? super E>` 
(receives and element and returns a boolean)

## Q6

`forEach` should receive a `Consumer<? super Data<E>>`
because we should be able to receive any supertype of `Data`.

## Q7

`Index` should implement `Iterable<Data<E>>`. `iterator` must be implemented,
and returns an `Iterator<Data<E>>`.

## Q8

We cannot "just" concatenate the two arrays because it doesn't take into
account duplicates, nor the fact that the indices should be sorted.