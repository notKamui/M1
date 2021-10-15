package fr.uge.poo.ducks;

import java.util.ServiceLoader;

public final class DuckFarmBetter {

    public static void main(String[] args) {
        var duckFactoryLoader = ServiceLoader.load(DuckFactory.class);
        duckFactoryLoader.stream().forEach(df -> {
            var riri = df.get().withName("Riri");
            var fifi = df.get().withName("Fifi");
            var loulou = df.get().withName("Loulou");
            System.out.println(riri.quack());
            System.out.println(fifi.quack());
            System.out.println(loulou.quack());
        });
    }
}
