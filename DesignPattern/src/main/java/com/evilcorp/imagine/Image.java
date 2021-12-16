package com.evilcorp.imagine;


public interface Image {

    String name();

    int size();

    double hue();

    static Image downloadLazy(String url) {
        return new LazyImage(url);
    }

    static Image download(String url) {
        var parts = url.split("/");
        var name = parts[parts.length - 1];
        var size = Math.abs(name.hashCode()) % 1_000_000;
        var hue = Math.abs(name.hashCode() % 255) / 255.0;
        System.out.println("Downloading image from " + url + " will take " + size % 10 + " seconds");
        try {
            Thread.sleep((size % 10) * 1000);
        } catch (InterruptedException e) {
            // not a good idea but it makes things easier
            throw new RuntimeException(e);
        }
        return new ActualImage(name, size, hue);
    }
}


