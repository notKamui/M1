package fr.umlv.reversible;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ReversibleTest {

    @Test  @Tag("Q1")
    public void fromArrayIntegers() {
        Reversible<Integer> reversible = Reversible.fromArray(1, 2, 3);
        assertNotNull(reversible);
    }
    @Test  @Tag("Q1")
    public void fromArrayStrings() {
        Reversible<String> reversible = Reversible.fromArray("foo", "bar", "baz");
        assertNotNull(reversible);
    }
    @Test @Tag("Q1")
    public void sizeIntegers() {
        var reversible = Reversible.fromArray(1, 2, 3, 4);
        assertEquals(4, reversible.size());
    }
    @Test @Tag("Q1")
    public void sizeStrings() {
        var reversible = Reversible.fromArray("foo", "bar");
        assertEquals(2, reversible.size());
    }
    @Test @Tag("Q1")
    public void get() {
        var reversible = Reversible.fromArray("foo", "bar");
        assertAll(
            () -> assertEquals("foo", reversible.get(0)),
            () -> assertEquals("bar", reversible.get(1))
        );
    }
    @Test @Tag("Q1")
    public void getAndSize() {
        var array = IntStream.range(0, 1_000_000).boxed().toArray(Integer[]::new);
        var reversible = Reversible.fromArray(array);
        assertEquals(1_000_000, reversible.size());
    }
    @Test @Tag("Q1")
    public void getAfterMutation() {
        var array = new Integer[] { 123, 6, 42 };
        var reversible = Reversible.fromArray(array);
        array[0] = 1;
        assertEquals(1, reversible.get(0));
    }
    @Test @Tag("Q1")
    public void getPreconditions() {
        var reversible = Reversible.fromArray("foo", "bar");
        assertAll(
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.get(-1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.get(2))
        );
    }
    @Test @Tag("Q1")
    public void forArrayPreconditions() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> Reversible.fromArray((Object[])null)),
            () -> assertThrows(NullPointerException.class, () -> Reversible.fromArray("hello", null)),
            () -> assertThrows(NullPointerException.class, () -> Reversible.fromArray(null, 3))
        );
    }
    @Test @Tag("Q1")
    public void getAlterAfterCreation() {
        var array = new String[] { "foo", "bar", "baz", "whizz" };
        var reversible = Reversible.fromArray(array);
        array[1] = null;
        assertThrows(NullPointerException.class, () -> reversible.get(1));
    }
    @Test @Tag("Q1")
    public void tooManyPublicMethods() {
        var collectionMethods = Arrays.stream(Collection.class.getMethods()).map(Method::getName).collect(Collectors.toSet());
        var reversibleMethods = Arrays.stream(Reversible.class.getMethods()).map(Method::getName).collect(Collectors.toSet());
        reversibleMethods.removeAll(collectionMethods);
        reversibleMethods.removeAll(Set.of("fromArray", "get", "set", "first", "reversed", "last", "fromList"));
        assertTrue(reversibleMethods.isEmpty());
    }
    @Test @Tag("Q1")
    public void fromArrayInSameCompilationUnit() {
        var reversible = Reversible.fromArray("hello");
        assertSame(Reversible.class, reversible.getClass().getNestHost());
    }


    @Test @Tag("Q2")
    public void loop() {
        var reversible = Reversible.fromArray(4, 6, 8);
        var list = new ArrayList<Integer>();
        for(var value: reversible) {
            list.add(value);
        }
        assertEquals(List.of(4, 6, 8), list);
    }
    @Test @Tag("Q2")
    public void loopTyped() {
        var reversible = Reversible.fromArray("foo", "bar", "baz");
        var list = new ArrayList<String>();
        for(String value: reversible) {
            list.add(value);
        }
        assertEquals(List.of("foo", "bar", "baz"), list);
    }
    @Test @Tag("Q2")
    public void forEach() {
        var reversible = Reversible.fromArray(4, 6, 8);
        var list = new ArrayList<Integer>();
        reversible.forEach(list::add);
        assertEquals(List.of(4, 6, 8), list);
    }
    @Test @Tag("Q2")
    public void forEachEmpty() {
        var reversible = Reversible.<String>fromArray();
        reversible.forEach((Object o) -> fail());
    }
    @Test @Tag("Q2")
    public void iteratorNoElement() {
        var reversible = Reversible.fromArray("foo");
        var it = reversible.iterator();
        assertEquals("foo", it.next());
        assertThrows(NoSuchElementException.class, it::next);
    }
    @Test @Tag("Q2")
    public void iteratorRemove() {
        var reversible = Reversible.fromArray("hello");
        var it = reversible.iterator();
        assertEquals("hello", it.next());
        assertThrows(UnsupportedOperationException.class, it::remove);
    }


    @Test @Tag("Q3")
    public void reverseStrings() {
        Reversible<String> reversible = Reversible.fromArray("foo", "bar", "baz");
        Reversible<String> reversed = reversible.reversed();
        assertAll(
            () -> assertEquals(3, reversed.size()),
            () -> assertEquals("baz", reversed.get(0)),
            () -> assertEquals("bar", reversed.get(1)),
            () -> assertEquals("foo", reversed.get(2))
        );
    }
    @Test @Tag("Q3")
    public void reverseIntegers() {
        Reversible<Integer> reversible = Reversible.fromArray(42, 17);
        Reversible<Integer> reversed = reversible.reversed();
        assertAll(
            () -> assertEquals(2, reversed.size()),
            () -> assertEquals(17, reversed.get(0)),
            () -> assertEquals(42, reversed.get(1))
        );
    }
    @Test @Tag("Q3")
    public void reverseEmpty() {
        var reversible = Reversible.fromArray();
        assertEquals(0, reversible.reversed().size());
    }
    @Test @Tag("Q3")
    public void reverseExample() {
        var reversible = Reversible.fromArray("foo", "bar", "baz", "whizz");
        assertEquals(4, reversible.size());
        assertEquals("bar", reversible.get(1));
        assertEquals(4, reversible.reversed().size());
        assertEquals("baz", reversible.reversed().get(1));
    }
    @Test @Tag("Q3")
    public void reverseAfterMutation() {
        var array = new Integer[] { 123, 6, 42 };
        var reversible = Reversible.fromArray(array).reversed();
        array[0] = 1;
        assertEquals(1, reversible.get(2));
    }
    @Test @Tag("Q3")
    public void reverseLoop() {
        var reversible = Reversible.fromArray(1, 4, 5, 6);
        var list = new ArrayList<Integer>();
        for(var value: reversible.reversed()) {
            list.add(value);
        }
        assertEquals(List.of(6, 5, 4, 1), list);
    }
    @Test @Tag("Q3")
    public void reverseForEach() {
        var reversible = Reversible.fromArray(1, 4, 5, 6);
        var list = new ArrayList<Integer>();
        reversible.reversed().forEach(list::add);
        assertEquals(List.of(6, 5, 4, 1), list);
    }
    @Test @Tag("Q3")
    public void reversePerformance() {
        var array = IntStream.range(0, 1_000_000).boxed().toArray(Integer[]::new);
        var reversible = Reversible.fromArray(array).reversed();
        assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
            var expected = 1_000_000;
            for(var i = 0; i < 1_000_000; i++) {
                assertEquals(--expected, reversible.get(i));
            }
        });
    }
    @Test @Tag("Q3")
    public void reversePerformance2() {
        var array = IntStream.range(0, 1_000_000).boxed().toArray(Integer[]::new);
        var reversible = Reversible.fromArray(array);
        assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
            var expected = 1_000_000;
            for(var i = 0; i < 1_000_000; i++) {
                assertEquals(--expected, reversible.reversed().get(i));
            }
        });
    }
    @Test @Tag("Q3")
    public void reverseGetPrecondition() {
        var reversible = Reversible.fromArray("foo", "bar");
        assertAll(
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(-1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(2))
        );
    }
    @Test @Tag("Q3")
    public void reverseInSameCompilationUnit() {
        var reversible = Reversible.fromArray("hello").reversed();
        assertSame(Reversible.class, reversible.getClass().getNestHost());
    }


    @Test @Tag("Q4")
    public void reverseReverse() {
        var reversible = Reversible.fromArray("foo", "bar", "baz");
        assertSame(reversible, reversible.reversed().reversed());
    }


    @Test  @Tag("Q5")
    public void fromListIntegers() {
        Reversible<Integer> reversible = Reversible.fromList(List.of(1, 2, 3));
        assertNotNull(reversible);
    }
    @Test  @Tag("Q5")
    public void fromListStrings() {
        Reversible<String> reversible = Reversible.fromList(List.of("foo", "bar", "baz"));
        assertNotNull(reversible);
    }
    @Test @Tag("Q5")
    public void fromListSizeIntegers() {
        var reversible = Reversible.fromList(List.of(1, 2, 3, 4));
        assertEquals(4, reversible.size());
    }
    @Test @Tag("Q5")
    public void fromListSizeStrings() {
        var list = new ArrayList<>(List.of("foo", "bar"));
        var reversible = Reversible.fromList(list);
        assertEquals(2, reversible.size());
    }
    @Test @Tag("Q5")
    public void fromListGet() {
        var list = new ArrayList<>(List.of("foo", "bar"));
        var reversible = Reversible.fromList(list);
        assertAll(
            () -> assertEquals("foo", reversible.get(0)),
            () -> assertEquals("bar", reversible.get(1))
        );
    }
    @Test @Tag("Q5")
    public void fromListGetAndSize() {
        var list = IntStream.range(0, 1_000_000).boxed().toList();
        var reversible = Reversible.fromList(list);
        assertEquals(1_000_000, reversible.size());
    }
    @Test @Tag("Q5")
    public void fromListGetAfterListSet() {
        var list = new ArrayList<>(List.of(123, 6, 42));
        var reversible = Reversible.fromList(list);
        list.set(0, 1);
        assertAll(
            () -> assertEquals(3, reversible.size()),
            () -> assertEquals(1, reversible.get(0))
        );
    }
    @Test @Tag("Q5")
    public void fromListGetAfterAdd() {
        var list = new ArrayList<>(List.of(123, 6, 42));
        var reversible = Reversible.fromList(list);
        list.add(45);
        assertAll(
            () -> assertEquals(3, reversible.size()),
            () -> assertEquals(123, reversible.get(0))
        );
    }
    @Test @Tag("Q5")
    public void fromListGetAfterRemove() {
        var list = new ArrayList<>(List.of(123, 6, 42));
        var reversible = Reversible.fromList(list);
        list.remove(0);
        assertAll(
            () -> assertEquals(3, reversible.size()),
            () -> assertThrows(IllegalStateException.class, () -> reversible.get(0))
        );
    }
    @Test @Tag("Q5")
    public void fromListGetPreconditions() {
        var reversible = Reversible.fromList(List.of("foo", "bar"));
        assertAll(
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.get(-1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.get(-2))
        );
    }
    @Test @Tag("Q5")
    public void fromListSignature() {
        Reversible<Object> reversible = Reversible.fromList(List.of("foo", "bar"));
        assertNotNull(reversible);
    }
    @Test @Tag("Q5")
    public void fromListPreconditions() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> Reversible.fromList(null)),
            () -> assertThrows(NullPointerException.class, () -> Reversible.fromList(Arrays.asList("hello", null))),
            () -> assertThrows(NullPointerException.class, () -> Reversible.fromList(Arrays.asList(null, 3)))
        );
    }
    @Test @Tag("Q5")
    public void fromListGetAlterAfterCreation() {
        var list = new ArrayList<>(List.of("foo", "bar", "baz", "whizz" ));
        var reversible = Reversible.fromList(list);
        list.set(1, null);
        assertThrows(NullPointerException.class, () -> reversible.get(1));
    }
    @Test @Tag("Q5")
    public void fromListLoop() {
        var reversible = Reversible.fromList(List.of(4, 6, 8));
        var list = new ArrayList<Integer>();
        for(var value: reversible) {
            list.add(value);
        }
        assertEquals(List.of(4, 6, 8), list);
    }
    @Test @Tag("Q5")
    public void fromListLoopTyped() {
        var reversible = Reversible.fromList(List.of("foo", "bar", "baz"));
        var list = new ArrayList<String>();
        for(String value: reversible) {
            list.add(value);
        }
        assertEquals(List.of("foo", "bar", "baz"), list);
    }
    @Test @Tag("Q5")
    public void fromListLoopAfterAdd() {
        var values = new ArrayList<>(List.of("foo", "bar", "baz"));
        var reversible = Reversible.fromList(values);
        values.add("whizz");
        var list = new ArrayList<String>();
        for(String value: reversible) {
            list.add(value);
        }
        assertEquals(List.of("foo", "bar", "baz"), list);
    }
    @Test @Tag("Q5")
    public void fromListLoopAfterRemove() {
        var values = new ArrayList<>(List.of("foo", "bar", "baz"));
        var reversible = Reversible.fromList(values);
        values.remove("bar");
        assertThrows(ConcurrentModificationException.class, () -> {
            for (String ignored : reversible) {
                fail();
            }
        });
    }
    @Test @Tag("Q5")
    public void fromListForEach() {
        var reversible = Reversible.fromList(List.of(4, 6, 8));
        var list = new ArrayList<Integer>();
        reversible.forEach(list::add);
        assertEquals(List.of(4, 6, 8), list);
    }
    @Test @Tag("Q5")
    public void fromListForEachAfterAdd() {
        var values = new ArrayList<>(List.of(4, 6, 8));
        var reversible = Reversible.fromList(values);
        values.add(42);
        var list = new ArrayList<Integer>();
        reversible.forEach(list::add);
        assertEquals(List.of(4, 6, 8), list);
    }
    @Test @Tag("Q5")
    public void fromListForEachAfterRemove() {
        var values = new ArrayList<>(List.of("foo", "bar", "baz"));
        var reversible = Reversible.fromList(values);
        values.remove("bar");
        assertThrows(ConcurrentModificationException.class, () -> reversible.forEach(__ -> fail()));
    }
    @Test @Tag("Q5")
    public void fromListForEachAfterSetNull() {
        var list = new ArrayList<Integer>();
        list.add(34);
        list.add(42);
        var reversible = Reversible.fromList(list);
        list.set(0, null);
        assertThrows(NullPointerException.class, () -> reversible.forEach(__ -> fail()));
    }
    @Test @Tag("Q5")
    public void fromListForEachEmpty() {
        var reversible = Reversible.<String>fromList(List.of());
        reversible.forEach((Object o) -> fail());
    }
    @Test @Tag("Q5")
    public void fromListReverseStrings() {
        Reversible<String> reversible = Reversible.fromList(List.of("foo", "bar", "baz"));
        Reversible<String> reversed = reversible.reversed();
        assertAll(
            () -> assertEquals(3, reversed.size()),
            () -> assertEquals("baz", reversed.get(0)),
            () -> assertEquals("bar", reversed.get(1)),
            () -> assertEquals("foo", reversed.get(2))
        );
    }
    @Test @Tag("Q5")
    public void fromListReverseInts() {
        Reversible<Integer> reversible = Reversible.fromList(List.of(42, 17));
        Reversible<Integer> reversed = reversible.reversed();
        assertAll(
            () -> assertEquals(2, reversed.size()),
            () -> assertEquals(17, reversed.get(0)),
            () -> assertEquals(42, reversed.get(1))
        );
    }
    @Test @Tag("Q5")
    public void fromListReverseEmpty() {
        var reversible = Reversible.fromList(List.of());
        assertEquals(0, reversible.reversed().size());
    }
    @Test @Tag("Q5")
    public void fromListReverseLoop() {
        var reversible = Reversible.fromList(List.of(1, 4, 5, 6));
        var list = new ArrayList<Integer>();
        for(var value: reversible.reversed()) {
            list.add(value);
        }
        assertEquals(List.of(6, 5, 4, 1), list);
    }
    @Test @Tag("Q5")
    public void fromListReverseForEach() {
        var reversible = Reversible.fromList(List.of(1, 4, 5, 6));
        var list = new ArrayList<Integer>();
        reversible.reversed().forEach(list::add);
        assertEquals(List.of(6, 5, 4, 1), list);
    }
    @Test @Tag("Q5")
    public void fromListReverseGetPrecondition() {
        var reversible = Reversible.fromList(List.of("foo", "bar"));
        assertAll(
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(-1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(2))
        );
    }
    @Test @Tag("Q5")
    public void fromListReverseAfterAdd() {
        var list = new ArrayList<>(List.of("foo", "bar"));
        var reversible = Reversible.fromList(list);
        list.add("baz");
        assertAll(
            () -> assertEquals(2, reversible.reversed().size()),
            () -> assertEquals("bar", reversible.reversed().get(0)),
            () -> assertEquals("foo", reversible.reversed().get(1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(2)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(-1))
        );
    }
    @Test @Tag("Q5")
    public void fromListReverseAfterRemove() {
        var list = new ArrayList<>(List.of("foo", "bar"));
        var reversible = Reversible.fromList(list);
        list.remove("bar");
        assertAll(
            () -> assertEquals(2, reversible.reversed().size()),
            () -> assertThrows(IllegalStateException.class, () -> reversible.reversed().get(0)),
            () -> assertThrows(IllegalStateException.class, () -> reversible.reversed().get(1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(2)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> reversible.reversed().get(-1))
        );
    }
    @Test @Tag("Q5")
    public void fromListReverseReverse() {
        var reversible = Reversible.fromList(List.of("foo", "bar", "baz"));
        assertSame(reversible, reversible.reversed().reversed());
    }
    @Test @Tag("Q5")
    public void fromArrayAndFromListSharedCode() {
        var reversible1 = Reversible.fromArray(34, 628);
        var reversible2 = Reversible.fromList(List.of(42, 77, 900));
        assertAll(
            () -> assertSame(reversible1.getClass(), reversible2.getClass()),
            () -> assertSame(reversible1.iterator().getClass(), reversible2.iterator().getClass()),
            () -> assertSame(reversible1.reversed().getClass(), reversible2.reversed().getClass())
        );
    }
    @Test @Tag("Q5")
    public void fromListSameCompilationUnit() {
        var reversible = Reversible.fromList(List.of("hello"));
        assertSame(Reversible.class, reversible.getClass().getNestHost());
    }


    @Test @Tag("Q6")
    public void streamToList() {
        var list = IntStream.range(0, 1_000_000).boxed().toList();
        var reversible = Reversible.fromList(list);
        assertEquals(list, reversible.stream().toList());
    }
    @Test @Tag("Q6")
    public void streamAndMutation() {
        var array = new String[] { "foo", "bar", "baz" };
        var reversible = Reversible.fromArray(array);
        array[1] = "whizz";
        assertEquals(List.of("foo", "whizz", "baz"), reversible.stream().toList());
    }
    @Test @Tag("Q6")
    public void streamAndSet() {
        var array = new String[] { "foo", "bar", "baz" };
        var reversible = Reversible.fromArray(array);
        array[1] = "whizz";
        assertEquals(List.of("foo", "whizz", "baz"), reversible.stream().toList());
    }
    @Test @Tag("Q6")
    public void streamAndAdd() {
        var list = new ArrayList<>(List.of("foo", "bar", "baz"));
        var reversible = Reversible.fromList(list);
        list.add("whizz");
        assertEquals(List.of("foo", "bar", "baz"), reversible.stream().toList());
    }
    @Test @Tag("Q6")
    public void streamAndRemove() {
        var list = new ArrayList<>(List.of("foo", "bar", "baz"));
        var reversible = Reversible.fromList(list);
        list.remove("baz");
        assertThrows(ConcurrentModificationException.class, () -> reversible.stream().toList());
    }
    @Test @Tag("Q6")
    public void streamCount() {
        var array = IntStream.range(0, 1_000_000).boxed().toArray(Integer[]::new);
        var reversible = Reversible.fromArray(array);
        assertEquals(1_000_000, reversible.stream().map(__ -> fail()).count());
    }
    @Test @Tag("Q6")
    public void streamSplitIfZeroOrOneElement() {
        assertAll(
            () -> assertNull(Reversible.fromArray().stream().spliterator().trySplit()),
            () -> assertNull(Reversible.fromArray(1).stream().spliterator().trySplit())
        );
    }
    @Test @Tag("Q6")
    public void streamSpliteratorCharacteristics() {
        var reversible = Reversible.fromArray(43, 67, 97);
        var spliterator = reversible.stream().spliterator();
        assertTrue(spliterator.hasCharacteristics(Spliterator.NONNULL));
        assertTrue(spliterator.hasCharacteristics(Spliterator.ORDERED));
    }
    @Test @Tag("Q6")
    public void streamReverseToList() {
        var reversible = Reversible.fromArray(43, 67, 97);
        assertEquals(List.of(97, 67, 43), reversible.reversed().stream().toList());
    }
    @Test @Tag("Q6")
    public void streamReverseCount() {
        var reversible = Reversible.fromList(List.of(43, 67, 97));
        assertEquals(3, reversible.reversed().stream().map(__ -> fail()).count());
    }
    @Test @Tag("Q6")
    public void noPublicClassesInTheInterface() {
        assertEquals(0, Reversible.class.getClasses().length);
    }


    @Test @Tag("Q7")
    public void parallelStreamToList() {
        var list = IntStream.range(0, 1_000_000).boxed().toList();
        var reversible = Reversible.fromList(list);
        assertEquals(list, reversible.stream().parallel().toList());
    }
    @Test @Tag("Q7")
    public void parallelStreamCount() {
        var array = IntStream.range(0, 1_000_000).boxed().toArray(Integer[]::new);
        var reversible = Reversible.fromArray(array);
        assertEquals(1_000_000, reversible.stream().parallel().map(__ -> fail()).count());
    }
    @Test @Tag("Q7")
    public void parallelStreamUseSeveralThreads() {
        var list = IntStream.range(0, 1_000_000).boxed().toList();
        var reversible = Reversible.fromList(list);
        var threads = new HashSet<Thread>();
        var sum = reversible.stream().parallel().peek(__ -> threads.add(Thread.currentThread())).mapToLong(x -> x).sum();
        assertEquals(499_999_500_000L, sum);
        assertTrue(threads.size() > 1);
    }
    @Test @Tag("Q7")
    public void parallelStreamSplitInTheMiddle() {
        var list = IntStream.range(0, 1_000_000).boxed().toList();
        var reversible = Reversible.fromList(list);
        var spliterator = reversible.stream().spliterator();
        var spliterator2 = spliterator.trySplit();
        assertEquals(500_000, spliterator.estimateSize());
        assertEquals(500_000, spliterator2.estimateSize());
    }
    @Test @Tag("Q7")
    public void parallelReverseStreamToList() {
        var list = IntStream.range(0, 1_000_000).boxed().toList();
        var reversible = Reversible.fromList(list);
        var expectedList = new ArrayList<>(list);
        Collections.reverse(expectedList);
        assertEquals(expectedList, reversible.reversed().stream().parallel().toList());
    }
}