package fr.uge.poo.imagine;

import com.evilcorp.imagine.Image;
import java.util.Map;

public class Application {
    public static void main(String[] args) {
        var map = Map.of(
            "cat","http://www.example.com/cat.png",
            "dog","http://www.example.com/dog.png",
            "mice","http://www.example.com/mice.png"
        );
        var images = map.values().stream().map(Image::downloadLazy).toList();
        System.out.println(images.get(0).hue());
    }
}
