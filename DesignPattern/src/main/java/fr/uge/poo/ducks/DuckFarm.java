package fr.uge.poo.ducks;

import java.util.ServiceLoader;

public final class DuckFarm {

    public static void main(String[] args) {
        var loader = ServiceLoader.load(Duck.class);
        for (Duck duck : loader) {
            System.out.println(duck.quack());
        }
    }
}
