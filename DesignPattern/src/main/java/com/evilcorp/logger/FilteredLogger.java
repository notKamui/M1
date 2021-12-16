package com.evilcorp.logger;

import java.util.Objects;
import java.util.function.Predicate;

public class FilteredLogger implements Logger {
    private final Logger logger;
    private final Predicate<Level> filter;
    private final Logger next;

    public FilteredLogger(Logger logger, Predicate<Level> filter) {
        this(logger, filter, null);
    }

    public FilteredLogger(Logger logger, Predicate<Level> filter, Logger next) {
        this.logger = Objects.requireNonNull(logger);
        this.filter = Objects.requireNonNull(filter);
        this.next = next;
    }

    @Override
    public void log(Level level, String message) {
        if (next != null) next.log(level, message);
        if (filter.test(level)) logger.log(level, message);
    }
}
