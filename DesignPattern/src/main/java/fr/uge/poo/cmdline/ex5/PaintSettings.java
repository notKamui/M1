package fr.uge.poo.cmdline.ex5;

import java.net.InetSocketAddress;
import java.util.Optional;
import static java.util.Objects.requireNonNull;

public class PaintSettings {
    public static class Builder {
        private String name;
        private boolean isLegacy = false;
        private int width = 500;
        private int height = 500;
        private boolean hasBorders = false;
        private int borderWidth = 10;
        private InetSocketAddress remoteServer;

        public Builder withName(String name) {
            this.name = requireNonNull(name);
            return this;
        }

        public Builder legacy() {
            this.isLegacy = true;
            return this;
        }

        public Builder notLegacy() {
            this.isLegacy = false;
            return this;
        }

        public Builder withSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder withBorders(int width) {
            this.hasBorders = true;
            this.borderWidth = width;
            return this;
        }

        public Builder withBorders() {
            this.hasBorders = true;
            this.borderWidth = 10;
            return this;
        }

        public Builder withoutBorders() {
            this.hasBorders = false;
            return this;
        }

        public Builder withRemoteServer(String address, int port) {
            requireNonNull(address);
            this.remoteServer = new InetSocketAddress(address, port);
            return this;
        }

        public PaintSettings build() {
            if (name == null) throw new IllegalStateException("Missing name property");
            if (width < 0 || height < 0) throw new IllegalStateException("Size of the window cannot be negative");
            if (borderWidth < 0) throw new IllegalStateException("Border width cannot be negative");
            return new PaintSettings(this);
        }
    }

    private final String name;
    private final boolean isLegacy;
    private final int width;
    private final int height;
    private final boolean hasBorders;
    private final int borderWidth;
    private final InetSocketAddress remoteServer;

    private PaintSettings(Builder builder) {
        requireNonNull(builder);
        name = builder.name;
        isLegacy = builder.isLegacy;
        width = builder.width;
        height = builder.height;
        hasBorders = builder.hasBorders;
        borderWidth = builder.borderWidth;
        remoteServer = builder.remoteServer;
    }

    public String getName() {
        return name;
    }

    public boolean isLegacy() {
        return isLegacy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasBorders() {
        return hasBorders;
    }

    public int getBorderWidth() {
        if (!hasBorders) throw new UnsupportedOperationException("Has no borders");
        return borderWidth;
    }

    public Optional<InetSocketAddress> getRemoteServer() {
        return Optional.ofNullable(remoteServer);
    }

    @Override
    public String toString() {
        return "PaintSettings{" +
                "name='" + name + '\'' +
                ", isLegacy=" + isLegacy +
                ", width=" + width +
                ", height=" + height +
                ", hasBorders=" + hasBorders +
                (hasBorders ? ", borderWidth=" + borderWidth : "") +
                ", remoteServer='" + remoteServer + '\'' +
                '}';
    }
}
