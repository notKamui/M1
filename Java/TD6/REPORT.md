# TD6 - Java - Jimmy Teillard

## Exercise 1

### Q1

Because we need to emulate a linked list, `Entry` should have two private
fields `int value` and `Entry next`. `Entry` itself should be private and internal
to `IntHashSet` (so, private, static, and final), because we can thus make sure to
never expose how we implement it if we then want to do so. `Entry` can (and should)
be a record, since it only contains read-only data with getters.

### Q3

The modulo/remainder (%) is a very slow operation compared to bitwise comparisons,
so we should avoid it ; we're able to do so by doing bit-masking depending on 
the current size of the set.

If the size of the set is 2, the hash function may only need to check the
rightmost bit and apply a bitwise `and` to it. This is strictly equivalent
to do doing `value % 2 == 0`, but more efficient :

```
private int hash(int value) {
    return value & 1;
}
```

A more generalized version would look like this :

```
private int hash(int value) {
    return value & (internal.length - 1);
}
```

### Q4

`forEach` should receive a function of type `(int) -> void` ; this is
a `Consumer<Integer>`.