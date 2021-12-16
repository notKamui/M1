package com.evilcorp.imagine;

import java.util.Objects;

record ActualImage(String name, int size, double hue) implements Image {
    ActualImage {
        Objects.requireNonNull(name);
        if (size <= 0) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image image)) return false;
        if (size != image.size()) return false;
        if (Double.compare(image.hue(), hue) != 0) return false;
        return name.equals(image.name());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + size;
        temp = Double.doubleToLongBits(hue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
