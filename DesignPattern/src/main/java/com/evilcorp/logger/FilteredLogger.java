package com.evilcorp.logger;

import java.util.Objects;

public class FilteredLogger implements Logger {
    private final Logger logger;
    private final Level level;
    private final boolean higher;
    private final Logger next;

    public FilteredLogger(Logger logger, Level level, boolean higher) {
        this(logger, level, higher, null);
    }

    public FilteredLogger(Logger logger, Level level, boolean higher, Logger next) {
        this.logger = Objects.requireNonNull(logger);
        this.level = Objects.requireNonNull(level);
        this.higher = higher;
        this.next = next;
    }

    @Override
    public void log(Level level, String message) {
        if (next != null) next.log(level, message);
        if (this.higher) {
            if (level.ordinal() <= this.level.ordinal()) {
                logger.log(level, message);
            }
        } else {
            if (level.ordinal() >= this.level.ordinal()) {
                logger.log(level, message);
            }
        }
    }
}
