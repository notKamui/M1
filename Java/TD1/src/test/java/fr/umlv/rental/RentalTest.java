package fr.umlv.rental;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("static-method")
public class RentalTest {
    private static Set<Class<?>> implementedInterfaces(Class<?> clazz) {
        return Stream.<Class<?>>iterate(clazz, type -> type != Object.class, Class::getSuperclass)
                .flatMap(type -> Arrays.stream(type.getInterfaces()))
                .collect(toSet());
    }

    @Test @Tag("Q1")
    public void shouldCreateCarWithModelAndYear() {
        var car = new Car("ford mustang", 2014);
        assertEquals("ford mustang", car.model());
    }
    @Test @Tag("Q1")
    public void shouldGetErrorWhenCreatingCarWithoutModel() {
        assertThrows(NullPointerException.class, () -> new Car(null, 2014));
    }
    @Test @Tag("Q1")
    public void shouldCarBeARecord() {
        assertTrue(Car.class.isRecord());
    }


//    @Test @Tag("Q2")
//    public void shouldPrintACarWithTheModelAndYear() {
//        var car = new Car("ford mustang", 2020);
//        assertEquals("ford mustang 2020", car.toString());
//    }
//    @Test @Tag("Q2")
//    public void shouldPrintACarWithTheModelAndYear2() {
//        var car = new Car("toyota corolla", 2017);
//        assertEquals("toyota corolla 2017", car.toString());
//    }
//
//
//    @Test @Tag("Q3")
//    public void shouldAddLotsOfNewCarsToRental() {
//        var rental = new CarRental();
//        IntStream.range(0, 1_000_000).forEach(i -> rental.add(new Car("foo car", i)));
//    }
//    @Test @Tag("Q3")
//    public void shouldGetErrorWhenAddingNonExistentCarToRental() {
//        var rental = new CarRental();
//        assertThrows(NullPointerException.class, () -> rental.add(null));
//    }
//
//
//    @Test @Tag("Q4")
//    public void shouldConvertRentalToText() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2001));
//        rental.add(new Car("ford mustang", 2006));
//        assertEquals("audi tt 2001\nford mustang 2006", rental.toString());
//    }
//    @Test @Tag("Q4")
//    public void shouldConvertRentalToEmptyTextIfEmpty() {
//        var rental = new CarRental();
//        assertEquals("", rental.toString());
//    }
//
//
//    @Test @Tag("Q6")
//    public void shouldRemoveCarOfRental() {
//        var rental = new CarRental();
//        rental.add(new Car("ford mustang", 2013));
//        rental.remove(new Car("ford mustang", 2013));
//        assertEquals("", rental.toString());
//    }
//    @Test @Tag("Q6")
//    public void shouldGetErrorWhenRemovingANullCar() {
//        var rental = new CarRental();
//        assertThrows(NullPointerException.class, () -> rental.remove(null));
//    }
//    @Test @Tag("Q6")
//    public void shouldEqualsAndHashCodeCorrectlyWritten() {
//        var set = new HashSet<Car>();
//        set.add(new Car("ford mustang", 2013));
//        set.remove(new Car("ford mustang", 2013));
//        assertTrue(set.isEmpty());
//    }
//    @Test @Tag("Q6")
//    public void shouldEqualsTestStringsUsingEquals() {
//        var rental = new CarRental();
//        rental.add(new Car("ford mustang", 2017));
//        rental.remove(new Car(new String("ford mustang"), 2017));
//        assertEquals("", rental.toString());
//    }
//    @Test @Tag("Q6")
//    public void shouldGetErrorWhenRemovingCarOnEmptyRental() {
//        var rental = new CarRental();
//        assertThrows(IllegalStateException.class, () -> rental.remove(new Car("ford mustang", 2013)));
//    }
//
//
//    @Test @Tag("Q7")
//    public void shouldFindCarByYearInRental() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2012));
//        rental.add(new Car("ford mustang", 2014));
//        var list = rental.findAllByYear(2014);
//        assertAll(
//                () -> assertEquals(1, list.size()),
//                () -> assertEquals(list, List.of(new Car("ford mustang", 2014)))
//        );
//    }
//    @Test @Tag("Q7")
//    public void shouldNotFindAnyCarWhenSearchingNonExistantYear() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2015));
//        rental.add(new Car("ford mustang", 2013));
//        var list = rental.findAllByYear(2014);
//        assertTrue(list.isEmpty());
//    }
//
//
//    @Test @Tag("Q8")
//    public void shouldAddCarsOrCamelsOfRental() {
//        var rental = new CarRental();
//        rental.add(new Camel(2010));
//        rental.add(new Car("ford mustang", 2014));
//        rental.remove(new Car("ford mustang", 2014));
//        assertEquals("camel 2010", rental.toString());
//    }
//    @Test @Tag("Q8")
//    public void shouldAddAndRemoveCarsOrCamelsOfRental() {
//        var rental = new CarRental();
//        rental.add(new Camel(2010));
//        rental.add(new Car("ford mustang", 2014));
//        rental.remove(new Camel(2010));
//        assertEquals("ford mustang 2014", rental.toString());
//    }
//    @Test @Tag("Q8")
//    public void shouldACamelCanBeUsedWithASet() {
//        var set = new HashSet<Camel>();
//        set.add(new Camel(2013));
//        set.remove(new Camel(2013));
//        assertTrue(set.isEmpty());
//    }
//    @Test @Tag("Q8")
//    public void shouldACamelBeUsedWithAList() {
//        var camel = new Camel(2014);
//        var list = List.of(camel);
//        assertEquals("[camel 2014]", list.toString());
//    }
//    @Test @Tag("Q8")
//    public void shouldCamelBeARecord() {
//        assertTrue(Car.class.isRecord());
//    }
//    @Test @Tag("Q8")
//    public void shouldGetErrorWhenRemovingCamelOnEmptyRental() {
//        var rental = new CarRental();
//        assertThrows(IllegalStateException.class, () -> rental.remove(new Camel(2010)));
//    }
//    @Test @Tag("Q8")
//    public void shouldFindCarsAndCamelsByYearInRental() {
//        var rental = new CarRental();
//        rental.add(new Car("ford mustang", 2010));
//        rental.add(new Camel(2010));
//        var list = rental.findAllByYear(2010);
//        assertAll(
//                () -> assertTrue(list.contains(new Car("ford mustang", 2010))),
//                () -> assertTrue(list.contains(new Camel(2010)))
//        );
//    }
//    @Test @Tag("Q8")
//    public void shouldDistinguishBetweenCarsAndCamelWithSameYear() {
//        var rental = new CarRental();
//        rental.add(new Car("ford mustang", 2010));
//        rental.add(new Camel(2010));
//        var list = rental.findAllByYear(2010);
//        var set = new HashSet<>(list);
//        assertAll(
//                () -> assertTrue(set.contains(new Car("ford mustang", 2010))),
//                () -> assertTrue(set.contains(new Camel(2010)))
//        );
//    }
//
//
//    @Test @Tag("Q9")
//    public void shouldComputeOldCarInsuranceCostOfRental() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2001));
//        assertEquals(rental.insuranceCostAt(2017), 500);
//    }
//    @Test @Tag("Q9")
//    public void shouldComputeCarInsuranceCostOfRental() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2001));
//        assertEquals(rental.insuranceCostAt(2010), 200);
//    }
//    @Test @Tag("Q9")
//    public void shouldComputeCamelInsuranceCostOfRental() {
//        var rental = new CarRental();
//        rental.add(new Camel(2013));
//        assertEquals(rental.insuranceCostAt(2017), 400);
//    }
//    @Test @Tag("Q9")
//    public void shouldComputeInsuranceCostOfRental() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2001));
//        rental.add(new Car("ford mustang", 2009));
//        rental.add(new Camel(2013));
//        rental.add(new Camel(2010));
//        assertEquals(rental.insuranceCostAt(2017), 1800);
//    }
//    @Test @Tag("Q9")
//    public void shouldEmptyCarRentalHasAnInsuranceCostOfZero() {
//        var rental = new CarRental();
//        assertEquals(0, rental.insuranceCostAt(2000));
//    }
//    @Test @Tag("Q9")
//    public void shouldGetErrorIfWhenAskingAnInsuranceCostWithADateOlderThanTheCarCreation() {
//        var rental = new CarRental();
//        rental.add(new Car("audi tt", 2001));
//        assertThrows(IllegalArgumentException.class, () -> rental.insuranceCostAt(2000));
//    }
//    @Test @Tag("Q9")
//    public void shouldGetErrorIfWhenAskingAnInsuranceCostWithADateOlderThanTheCamelBirth() {
//        var rental = new CarRental();
//        rental.add(new Camel(2013));
//        assertThrows(IllegalArgumentException.class, () -> rental.insuranceCostAt(2012));
//    }
//
//
//    @Test @Tag("Q10")
//    public void shouldCarAndCamelHaveACommonSuperType() {
//        assertAll(
//                () -> assertFalse(implementedInterfaces(Car.class).isEmpty()),
//                () -> assertFalse(implementedInterfaces(Camel.class).isEmpty()),
//                () -> assertFalse(Collections.disjoint(implementedInterfaces(Car.class), implementedInterfaces(Camel.class)))
//        );
//    }
//
//
//    @Test @Tag("Q11")
//    public void shouldGetErrorWhenSearchingANullCar() {
//        var rental = new CarRental();
//        assertThrows(NullPointerException.class, () -> rental.findACarByModel(null));
//    }
//    @Test @Tag("Q11")
//    public void shouldFindACarByModelInRental() {
//        var rental = new CarRental();
//        rental.add(new Car("ford mustang", 2020));
//        rental.add(new Camel(2003));
//        Optional<Car> tmp = rental.findACarByModel("ford mustang");
//        assertEquals(new Car("ford mustang", 2020), tmp.orElseThrow());
//    }
//    @Test @Tag("Q11")
//    public void shouldNotFindACarByNonExistentModelInRental() {
//        var rental = new CarRental();
//        rental.add(new Car("renault alpine", 1992));
//        rental.add(new Camel(1992));
//        assertTrue(rental.findACarByModel("ford mustang").isEmpty());
//    }
}