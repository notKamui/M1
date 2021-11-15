package fr.umlv.set;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("static-method")
public class IntHashSetTest {
    // Q1

    @Test
    @Tag("Q1")
    public void entryIsNotDeclaredInThePackage() {
        assertThrows(ClassNotFoundException.class, () -> Class.forName("fr.umlv.set.Entry"));
    }

    @Test
    @Tag("Q1")
    public void entryIsDeclaredInsideIntHashSet() {
        assertTrue(Arrays.stream(IntHashSet.class.getDeclaredClasses())
                .anyMatch(type -> type.getName().equals("fr.umlv.set.IntHashSet$Entry")));
    }

    @Test
    @Tag("Q1")
    public void entryIsARecord() throws ClassNotFoundException {
        var type = Class.forName("fr.umlv.set.IntHashSet$Entry");
        assertTrue(type.isRecord());
    }

    @Test
    @Tag("Q1")
    public void entryIsStaticAndPrivateAndFinal() throws ClassNotFoundException {
        var type = Class.forName("fr.umlv.set.IntHashSet$Entry");
        var modifiers = type.getModifiers();
        assertAll(
                () -> assertTrue(Modifier.isPrivate(modifiers)),
                () -> assertTrue(Modifier.isStatic(modifiers)),
                () -> assertTrue(Modifier.isFinal(modifiers))
        );
    }

    @Test
    @Tag("Q1")
    public void entryHasTwoComponentsValueAndNext() throws ClassNotFoundException {
        var type = Class.forName("fr.umlv.set.IntHashSet$Entry");
        var components = type.getRecordComponents();
        assertNotNull(components);
        assertEquals(List.of("value", "next"), Arrays.stream(components).map(RecordComponent::getName).toList());
    }


    // Q3

    @Test
    @Tag("Q3")
    public void shouldAddOne() {
        var set = new IntHashSet();
        set.add(1);
        assertEquals(1, set.size());
    }

    @Test
    @Tag("Q3")
    public void shouldAddAnInteger() {
        var set = new IntHashSet();
        set.add(31_133);
        assertEquals(1, set.size());
    }

    @Test
    @Tag("Q3")
    public void shouldAddWithoutErrors() {
        var set = new IntHashSet();
        IntStream.range(0, 100).map(i -> i * 2 + 1).forEach(set::add);
        assertEquals(100, set.size());
    }

    @Test
    @Tag("Q3")
    public void shouldNotTakeTooLongToAddTheSameNumberMultipleTimes() {
        var set = new IntHashSet();
        assertTimeoutPreemptively(
                Duration.ofMillis(5_000),
                () -> IntStream.range(0, 1_000_000).map(i -> 42).forEach(set::add));
        assertEquals(1, set.size());
    }

    @Test
    @Tag("Q3")
    public void shouldAnswerZeroWhenAskingForSizeOfEmptySet() {
        var set = new IntHashSet();
        assertEquals(0, set.size());
    }

    @Test
    @Tag("Q3")
    public void shouldNotAddTwiceTheSameAndComputeSizeAccordingly() {
        var set = new IntHashSet();
        set.add(3);
        assertEquals(1, set.size());
        set.add(-777);
        assertEquals(2, set.size());
        set.add(3);
        assertEquals(2, set.size());
        set.add(-777);
        assertEquals(2, set.size());
        set.add(11);
        assertEquals(3, set.size());
        set.add(3);
        assertEquals(3, set.size());
    }


    // Q4

    @Test
    @Tag("Q4")
    public void shouldDoNoThingWhenForEachCalledOnEmptySet() {
        var set = new IntHashSet();
        set.forEach(__ -> fail("should not be called"));
    }

    @Test
    @Tag("Q4")
    public void shouldCompteTheSumOfAllTheElementsInASetUsingForEachAngGetTheSameAsTheFormula() {
        var length = 100;
        var set = new IntHashSet();
        IntStream.range(0, length).forEach(set::add);
        var sum = new int[]{0};
        set.forEach(value -> sum[0] += value);
        assertEquals(length * (length - 1) / 2, sum[0]);
    }

    @Test
    @Tag("Q4")
    public void shouldComputeIndenticalSetAndHashSetUsingForEachAndHaveSameSize() {
        var set = new IntHashSet();
        IntStream.range(0, 100).forEach(set::add);
        var hashSet = new HashSet<Integer>();
        set.forEach(hashSet::add);
        assertEquals(set.size(), hashSet.size());
    }

    @Test
    @Tag("Q4")
    public void shouldAddAllTheElementsOfASetToAListUsingForEach() {
        var set = new IntHashSet();
        IntStream.range(0, 100).forEach(set::add);
        var list = new ArrayList<Integer>();
        set.forEach(list::add);
        list.sort(null);
        IntStream.range(0, 100).forEach(i -> assertEquals(i, list.get(i)));
    }

    @Test
    @Tag("Q4")
    public void shouldNotUseNullAsAParameterForForEach() {
        var set = new IntHashSet();
        assertThrows(NullPointerException.class, () -> set.forEach(null));
    }

    @Test
    @Tag("Q4")
    public void shouldNotUseNullAsAParameterForForEach2() {
        var set = new IntHashSet();
        set.add(4);
        assertThrows(NullPointerException.class, () -> set.forEach(null));
    }


    // Q5

    @Test
    @Tag("Q5")
    public void shouldNotFindAnythingContainedInAnEmptySet() {
        var set = new IntHashSet();
        assertFalse(set.contains(4));
        assertFalse(set.contains(7));
        assertFalse(set.contains(1));
        assertFalse(set.contains(0));
        assertEquals(0, set.size());
    }

    @Test
    @Tag("Q5")
    public void shouldNotFindAnIntegerBeforeAddingItButShouldFindItAfter() {
        var set = new IntHashSet();
        for (int i = 0; i < 10; i++) {
            assertFalse(set.contains(i));
            set.add(i);
            assertTrue(set.contains(i));
        }
        assertEquals(10, set.size());
    }

    @Test
    @Tag("Q5")
    public void shoulAddAndTestContainsForAnExtremalValue() {
        var set = new IntHashSet();
        assertFalse(set.contains(Integer.MIN_VALUE));
        set.add(Integer.MIN_VALUE);
        assertTrue(set.contains(Integer.MIN_VALUE));
        set.add(Integer.MAX_VALUE);
        assertTrue(set.contains(Integer.MAX_VALUE));
        assertEquals(2, set.size());
    }
}
