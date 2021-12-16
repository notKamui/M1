package com.evilcorp.imagine;

import static java.util.Objects.requireNonNull;

class LazyImage implements Image {
    private Image image;
    private final String url;

    LazyImage(String url) {
        this.url = requireNonNull(url);
    }

    private Image lazyGet() {
        if (image == null) {
            image = Image.download(url);
        }
        return image;
    }

    @Override
    public String name() {
        return lazyGet().name();
    }

    @Override
    public int size() {
        return lazyGet().size();
    }

    @Override
    public double hue() {
        return lazyGet().hue();
    }
}
