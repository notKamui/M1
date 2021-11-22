package fr.umlv.queue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("static-method")
public class FifoTest {
    // Q2

    @Test @Tag("Q2")
    public void shouldGetAnErrorWhenCapacityIsNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Fifo<>(-3));
    }
    @Test @Tag("Q2")
    public void shouldGetAnErrorWhenCapacityIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new Fifo<>(0));
    }


    // Q3

    @Test @Tag("Q3")
    public void shouldGetAnErrorWhenOfferingToFullFifo() {
        var fifo = new Fifo<Integer>(1);
        fifo.offer(43);
        assertThrows(IllegalStateException.class, () -> fifo.offer(7));
    }
    @Test @Tag("Q3")
    public void shouldGetAnErrorWhenOfferingNull() {
        var fifo = new Fifo<>(234);
        assertThrows(NullPointerException.class, () -> fifo.offer(null));
    }


    // Q4

    @Test @Tag("Q4")
    public void shouldGetAnErrorWhenPollingFromEmptyFifo() {
        var fifo = new Fifo<>(1);
        assertThrows(IllegalStateException.class, fifo::poll);
    }
    @Test @Tag("Q4")
    public void shouldGetOfferedValueWhenPolling() {
        var fifo = new Fifo<Integer>(2);
        fifo.offer(9);
        assertEquals(9, fifo.poll());
        fifo.offer(2);
        fifo.offer(37);
        assertEquals(2, fifo.poll());
        fifo.offer(12);
        assertEquals(37, fifo.poll());
        assertEquals(12, fifo.poll());
    }
    @Test @Tag("Q4")
    public void shouldGetOfferedValueWhenPollingWithMixedTypes() {
        var fifo = new Fifo<>(20);
        for (var i = 0; i < 20; i++) {
            fifo.offer(i);
        }
        assertEquals(0, fifo.poll());
        fifo.offer("foo");
        for (var i = 1; i < 20; i++) {
            assertEquals(i, fifo.poll());
        }
        assertEquals("foo", fifo.poll());
    }
    @Test @Tag("Q4")
    public void shouldBeAbleToAddMoreThanCapacityAfterRemoval() {
        var fifo = new Fifo<String>(2);
        fifo.offer("foo");
        fifo.poll();
        fifo.offer("1");
        fifo.offer("2");
    }


    // Q5

    @Test @Tag("Q5")
    public void shouldPrintEmptyFifo() {
        var fifo = new Fifo<>(23);
        assertEquals("[]", fifo.toString());
    }
    @Test @Tag("Q5")
    public void shouldPrintFifoWithOneElement() {
        var fifo = new Fifo<String>(23);
        fifo.offer("joe");
        assertEquals("[joe]", fifo.toString());
    }
    @Test @Tag("Q5")
    public void shouldPrintFifoWithTwoElements() {
        var fifo = new Fifo<Integer>(23);
        fifo.offer(1456);
        fifo.offer(8390);
        assertEquals("[1456, 8390]", fifo.toString());
    }
    @Test @Tag("Q5")
    public void shouldNotAffectFifoWhenPrinting() {
        var fifo = new Fifo<Integer>(200);
        var list = new ArrayList<Integer>();
        for (var i = 0; i < 100; i++) {
            fifo.offer(i);
            list.add(i);
        }
        assertEquals(list.toString(), fifo.toString());
        for (var i = 0; i < 100; i++) {
            assertEquals(i, (int) fifo.poll());
        }
    }
    @Test @Tag("Q5")
    public void shouldPrintFifoInTheSameWayAsAList() {
        var fifo = new Fifo<Integer>(99);
        var list = new ArrayList<Integer>();
        for (var i = 0; i < 99; i++) {
            fifo.offer(i);
            list.add(i);
        }
        assertEquals(list.toString(), fifo.toString());
    }
    @Test @Tag("Q5")
    public void shouldBeAbleToAPrintEvenIfFull() {
        var fifo = new Fifo<String>(2);
        fifo.offer("foo");
        fifo.poll();
        fifo.offer("1");
        fifo.offer("2");
        assertEquals("[1, 2]", fifo.toString());
    }


    // Q7

    @Test @Tag("Q7")
    public void shoulgGetACorrectSize() {
        var fifo = new Fifo<String>(2);
        assertEquals(0, fifo.size());
        fifo.offer("foo");
        assertEquals(1, fifo.size());
        fifo.offer("bar");
        assertEquals(2, fifo.size());
        fifo.poll();
        assertEquals(1, fifo.size());
        fifo.poll();
        assertEquals(0, fifo.size());
    }
    @Test @Tag("Q7")
    public void shouldAnswerZeroWhenAskedForTheSizeOfAnEmptyFifo() {
        var fifo = new Fifo<>(1);
        assertEquals(0, fifo.size());
    }
    @Test @Tag("Q7")
    public void shouldAnswerOneWhenAskedForTheSizeAfterOneOffer() {
        var fifo = new Fifo<String>(1);
        fifo.offer("dooh");
        assertEquals(1, fifo.size());
    }
    @Test @Tag("Q7")
    public void shouldFindFifoEmptyOnlyAfterRemovingAllElement() {
        var fifo = new Fifo<String>(2);
        assertTrue(fifo.isEmpty());
        fifo.offer("oof");
        assertFalse(fifo.isEmpty());
        fifo.offer("rab");
        assertFalse(fifo.isEmpty());
        fifo.poll();
        fifo.poll();
        assertTrue(fifo.isEmpty());
    }


    // Q9

    @Test @Tag("Q9")
    public void shouldGetTheRightTypeOfIterator() {
        var fifo = new Fifo<String>(1);
        Iterator<String> it = fifo.iterator();
        assertNotNull(it);
    }
    @Test @Tag("Q9")
    public void shouldGetAnErrorWhenAskingNextWhenDoesNotHaveNext() {
        var fifo = new Fifo<String>(1);
        fifo.offer("bar");
        fifo.poll();
        var it = fifo.iterator();
        assertThrows(NoSuchElementException.class, () -> it.next());
    }
    @Test @Tag("Q9")
    public void shouldNotGetSideEffectsWhenUsingIteratorHasNext() {
        var fifo = new Fifo<Integer>(3);
        fifo.offer(117);
        fifo.offer(440);
        var it = fifo.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(117, (int) it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(440, (int) it.next());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }
    @Test @Tag("Q9")
    public void shouldIterateProperlyWhenTheNumberofOffersOvertakesOriginalCapacity() {
        var fifo = new Fifo<Integer>(2);
        fifo.offer(42);
        fifo.poll();
        fifo.offer(55);
        fifo.offer(333);
        var it = fifo.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(55, (int) it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(333, (int) it.next());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }
    @Test @Tag("Q9")
    public void shouldBeAbleToIterateTwice() {
        var fifo = new Fifo<Integer>(1);
        fifo.offer(898);

        var it = fifo.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(898, (int) it.next());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
        var it2 = fifo.iterator();
        assertTrue(it2.hasNext());
        assertTrue(it2.hasNext());
        assertEquals(898, (int) it2.next());
        assertFalse(it2.hasNext());
        assertFalse(it2.hasNext());
    }
    @Test @Tag("Q9")
    public void shouldGetConsistentAnswersFromHasNextWhenEmpty() {
        var fifo = new Fifo<>(1);
        var it = fifo.iterator();
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }
    @Test @Tag("Q9")
    public void shouldGetConsistentAnswersFromHasNextWhenNotEmpty() {
        var fifo = new Fifo<>(1);
        fifo.offer("one");
        var it = fifo.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
    }
    @Test @Tag("Q9")
    public void shouldIterateOverALargeNumberOfElements() {
        var fifo = new Fifo<Integer>(10_000);
        for (int i = 0; i < 10_000; i++) {
            fifo.offer(i);
        }
        var i = 0;
        var it = fifo.iterator();
        while (it.hasNext()) {
            assertEquals(i++, (int) it.next());
        }
        assertEquals(10_000, fifo.size());
    }
    @Test @Tag("Q9")
    public void shouldGetAnErrorWhenTryingToUseIteratorRemove() {
        var fifo = new Fifo<String>(1);
        fifo.offer("foooo");
        assertThrows(UnsupportedOperationException.class, () -> fifo.iterator().remove());
    }

    //this test needs a lot of memory (more than 8 gigs)
    // so it is disabled by default
    // use the option -Xmx9g when running the VM
// @Test @Tag("Q9")
//  public void shouldNotGetAnOverflowErrorWhenIteratingOverAnAlmostMaximalCapacityFifo() {
//    var fifo = new Fifo<Integer>(Integer.MAX_VALUE - 8);
//    for(var i = 0; i < Integer.MAX_VALUE / 2; i++) {
//      fifo.offer(i % 100);
//      fifo.poll();
//    }
//    for(var i = 0; i < Integer.MAX_VALUE - 8; i++) {
//      fifo.offer(i % 100);
//    }
//    var counter = 0;
//    for(var it = fifo.iterator(); it.hasNext(); counter = (counter + 1) % 100) {
//      assertEquals(counter, (int)it.next());
//    }
//  }


    // Q10

    @Test @Tag("Q10")
    public void shouldBeAbleToUseImplicitForEachLoop() {
        var fifo = new Fifo<Integer>(100);
        fifo.offer(222);
        fifo.poll();

        for (var i = 0; i < 100; i++) {
            fifo.offer(i);
        }
        var i = 0;
        for (int value : fifo) {
            assertEquals(i++, value);
        }
        assertEquals(100, fifo.size());
    }
}
