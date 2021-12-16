package com.evilcorp.logger;

import java.util.Objects;

public class FilteredLogger implements Logger {
    private final Logger logger;
    private final Level level;
    private final boolean higher;

    public FilteredLogger(Logger logger, Level level, boolean higher) {
        this.logger = Objects.requireNonNull(logger);
        this.level = Objects.requireNonNull(level);
        this.higher = higher;
    }

    public FilteredLogger(Logger logger, Level level) {
        this(logger, level, true);
    }

    @Override
    public void log(Level level, String message) {
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
