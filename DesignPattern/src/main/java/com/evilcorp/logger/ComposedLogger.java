package com.evilcorp.logger;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ComposedLogger implements Logger {
    private final List<Logger> loggers;

    public ComposedLogger(List<Logger> loggers) {
        requireNonNull(loggers);
        this.loggers = List.copyOf(loggers);
    }

    public ComposedLogger(Logger... loggers) {
        requireNonNull(loggers);
        this.loggers = Arrays.asList(loggers);
    }

    @Override
    public void log(Level level, String message) {
        loggers.forEach(logger -> logger.log(level, message));
    }
}
