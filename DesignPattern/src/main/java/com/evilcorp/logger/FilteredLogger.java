package com.evilcorp.logger;

import java.util.Objects;
import java.util.function.Predicate;

public class FilteredLogger implements Logger {
    private final Logger logger;
    private final Predicate<Level> filter;

    public FilteredLogger(Logger logger, Predicate<Level> filter) {
        this.logger = Objects.requireNonNull(logger);
        this.filter = Objects.requireNonNull(filter);
    }

    @Override
    public void log(Level level, String message) {
        if (filter.test(level)) logger.log(level, message);
    }
}
