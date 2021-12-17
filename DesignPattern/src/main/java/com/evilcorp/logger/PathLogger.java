package com.evilcorp.logger;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PathLogger implements Logger, Closeable, AutoCloseable {
    private final BufferedWriter buffer;

    public PathLogger(Path path) {
        try {
            this.buffer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void log(Level level, String message) {
        try {
            buffer.write(level + " " + message);
            buffer.newLine();
            buffer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        try {
            buffer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
